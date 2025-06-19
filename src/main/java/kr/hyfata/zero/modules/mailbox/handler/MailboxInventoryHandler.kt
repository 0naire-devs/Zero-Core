package kr.hyfata.zero.modules.mailbox.handler

import kr.hyfata.zero.gui.InventoryEventListener
import kr.hyfata.zero.gui.InventoryGUI
import kr.hyfata.zero.modules.mailbox.dto.Mailbox
import kr.hyfata.zero.modules.mailbox.dto.MailboxButton
import kr.hyfata.zero.modules.mailbox.dto.MailboxInventoryInfo
import kr.hyfata.zero.util.InventoryUtil
import kr.hyfata.zero.util.ItemUtil
import kr.hyfata.zero.helper.format.TextFormatHelper
import kr.hyfata.zero.util.TimeUtil
import kr.hyfata.zero.util.config.MailboxConfigUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory
import org.bukkit.plugin.java.JavaPlugin
import java.sql.SQLException
import java.util.concurrent.CompletableFuture

class MailboxInventoryHandler(var plugin: JavaPlugin) : InventoryGUI {
    private val inventories = HashMap<Inventory, MailboxInventoryInfo>()
    private val db = MailboxDB()

    init {
        InventoryEventListener.Companion.registerInventory(this)
    }

    override fun openInventory(p: Player) {
        val iv = Bukkit.createInventory(
            p, MailboxConfigUtil.rows * 9,
            TextFormatHelper.getFormattedText(p, MailboxConfigUtil.mailBoxTitle!!)
        )

        p.openInventory(iv)

        Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
            cleanupExpiredMailboxes()
            val info = createMailboxInventoryInfo(p)
            if (info == null) return@Runnable

            inventories.put(iv, info)
            setItems(iv, 1)
        })
    }

    private fun setItems(iv: Inventory, page: Int) {
        inventories.get(iv)!!.currentPage = page
        iv.clear()

        val mailboxes = inventories.get(iv)!!.mailboxes // Mailbox items

        val guiRows = MailboxConfigUtil.rewardItemRowRange
        val guiStartSlot = (guiRows[0] - 1) * 9
        val itemCount = (guiRows[1] - guiRows[0]) * 9 // total mailbox item

        val currentPageStartIndex = (page - 1) * itemCount // var mailbox's start index
        val currentPageEndIndex = currentPageStartIndex + itemCount

        // set mailbox items
        var mailboxIndex = currentPageStartIndex
        var guiSlot = guiStartSlot
        while (mailboxIndex < currentPageEndIndex) {
            if (mailboxes == null || mailboxes.isEmpty()) break
            if (mailboxIndex < mailboxes.size) {
                val mailbox = mailboxes[mailboxIndex]
                setMailboxItemToInventory(iv, mailbox!!, guiSlot)
            } else {
                break
            }
            mailboxIndex++
            guiSlot++
        }

        // set buttons
        setNavButton(iv, MailboxConfigUtil.allRewardsButton, page)
        if (page != 1) setNavButton(iv, MailboxConfigUtil.previousButton, page)
        if (mailboxes != null && currentPageEndIndex < mailboxes.size) setNavButton(
            iv,
            MailboxConfigUtil.nextButton,
            page
        )
    }

    override fun inventoryClickEvent(e: InventoryClickEvent) {
        e.isCancelled = true
        if (containsTaskItem(e) ||
            (e.clickedInventory != null && e.clickedInventory == e.whoClicked.inventory) ||
            inventories.get(e.inventory)!!.isShouldCancel
        ) {
            return
        }

        if (InventoryUtil.isValidItem(e)) {
            onValidItemClick(e)
        }
    }

    private fun onValidItemClick(e: InventoryClickEvent) {
        val iv = e.inventory

        if (buttonPosContains(MailboxConfigUtil.previousButton, e.slot)) {
            setItems(iv, inventories.get(iv)!!.currentPage - 1) // goto previous page
        } else if (buttonPosContains(MailboxConfigUtil.nextButton, e.slot)) {
            setItems(iv, inventories.get(iv)!!.currentPage + 1) // goto next page
        } else {
            rewardClickEvent(e)
        }
    }

    private fun rewardClickEvent(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        val iv = e.inventory
        val mailboxes = inventories.get(iv)!!.mailboxes

        if (InventoryUtil.isInventoryFull((p))) {
            setItemError(e, "&c인벤토리 공간이 부족합니다.")
        } else if (buttonPosContains(MailboxConfigUtil.allRewardsButton, e.slot)) { // get all rewards
            mailboxes?.let {
                if (it.isEmpty()) {
                    setItemError(e, "&c이미 모든 보상을 수령했습니다!")
                } else {
                    if (inventories.get(iv)!!.isShouldCancel) {
                        return
                    }
                    inventories.get(iv)!!.isShouldCancel = true
                    CompletableFuture.runAsync {
                        getAllRewards(e)
                        inventories.get(iv)!!.isShouldCancel = false
                    }
                }
            }
        } else { // get a reward
            val guiRow1 = MailboxConfigUtil.rewardItemRowRange[0]
            val idx = e.slot - (guiRow1 - 1) * 9

            if (inventories.get(iv)!!.isShouldCancel) {
                return
            }
            inventories.get(iv)!!.isShouldCancel = true
            CompletableFuture.runAsync {
                try {
                    getReward(p, iv, idx)
                    setItems(iv, inventories.get(iv)!!.currentPage) // reload inventory
                } catch (ex: InvalidConfigurationException) {
                    ex.printStackTrace(System.err)
                    setItemError(e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.message)
                } catch (ex: SQLException) {
                    ex.printStackTrace(System.err)
                    setItemError(e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.message)
                }
                inventories.get(iv)!!.isShouldCancel = false
            }
        }
    }

    private fun getAllRewards(e: InventoryClickEvent) {
        val mailboxes = inventories.get(e.inventory)!!.mailboxes!!
        val size = mailboxes.size
        val p = e.whoClicked as Player
        var inventoryFull = false

        repeat(size) {
            if (InventoryUtil.isInventoryFull(p)) {
                inventoryFull = true
                return@repeat
            }

            try {
                getReward(p, e.inventory, 0)
            } catch (ex: InvalidConfigurationException) {
                setItemError(e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.message)
                plugin.logger.severe("Failed to get reward: " + ex.message)
                return
            } catch (ex: SQLException) {
                setItemError(e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.message)
                plugin.logger.severe("Failed to get reward: " + ex.message)
                return
            }
        }

        setItems(e.inventory, 1) // goto first page
        if (inventoryFull) {
            setItemError(e, "&c인벤토리 공간이 부족합니다!", "&7여유 공간을 확보해주세요!")
        } else {
            setItemSuccess(e, "&a보상을 성공적으로 수령했습니다!")
        }
    }

    @Throws(InvalidConfigurationException::class, SQLException::class)
    private fun getReward(p: Player, iv: Inventory?, index: Int) {
        val info: MailboxInventoryInfo = inventories[iv]!!
        val mailbox = info.mailboxes?.get(index)

        // db
        if (mailbox?.uuid == "all") {
            db.readMailbox(p, mailbox.mailId)
        } else {
            mailbox?.let { db.deleteMailbox(it.mailId) }
        }

        ItemUtil.base64ToItemStack(mailbox?.item)?.let { p.inventory.addItem(it) }
        inventories[iv]!!.mailboxes?.removeAt(index)
    }

    private fun createMailboxInventoryInfo(p: Player): MailboxInventoryInfo? {
        try {
            val info = MailboxInventoryInfo()
            info.mailboxes = db.getMailboxes(p)
            return info
        } catch (e: Exception) {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                p.closeInventory()
                p.sendMessage("[우편함] 오류가 발생하여 우편함을 볼 수 없습니다!")
                e.printStackTrace(System.err)
            })
        }
        return null
    }

    private fun setMailboxItemToInventory(iv: Inventory, mailbox: Mailbox, guiIndex: Int) {
        try {
            val itemStack = ItemUtil.base64ToItemStack(mailbox.item)

            val lore = ArrayList<Component?>()
            lore.add(Component.text(""))
            lore.add(
                LegacyComponentSerializer.legacyAmpersand().deserialize(
                    TimeUtil.getRemainingTimeText(mailbox.expiryTime!!)
                )
            ) // add expire date

            iv.setItem(guiIndex, ItemUtil.addLore(itemStack!!, lore)) // set mailbox item
        } catch (e: InvalidConfigurationException) {
            plugin.logger.severe("Failed to convert base64 to ItemStack: " + e.message)
        }
    }

    private fun setNavButton(iv: Inventory, button: MailboxButton, page: Int) {
        val positions = button.positions!!
        for (pos in positions) {
            val item = ItemUtil.newItemStack(
                button.item, 1, button.customModelData,
                button.name?.replace("\${page}", page.toString()),
                *button.getLore(page)
            )
            iv.setItem(pos!!, item)
        }
    }

    private fun buttonPosContains(button: MailboxButton, pos: Int): Boolean {
        val positions = button.positions!!
        for (p in positions) {
            if (p == pos) return true
        }
        return false
    }

    private fun getButtonPos(pos: Int): MutableList<Int?> {
        val getAllRewardsButton = MailboxConfigUtil.allRewardsButton
        val previousButton = MailboxConfigUtil.previousButton
        val nextButton = MailboxConfigUtil.nextButton

        if (buttonPosContains(getAllRewardsButton, pos)) return getAllRewardsButton.positions!!
        if (buttonPosContains(previousButton, pos)) return previousButton.positions!!
        if (buttonPosContains(nextButton, pos)) return nextButton.positions!!

        return mutableListOf(pos)
    }

    private fun setItemError(e: InventoryClickEvent, name: String, vararg lore: String?) {
        val errMaterial = MailboxConfigUtil.errorMaterial
        val formattedName = TextFormatHelper.getFormattedText(name)
        val formattedLore = TextFormatHelper.getFormattedTextList(*lore)
        val customModelData = MailboxConfigUtil.errorCustomModelData
        val positions = getButtonPos(e.slot)

        val errorItem = ItemUtil.newItemStack(errMaterial, 1, customModelData, formattedName, *formattedLore)
        InventoryUtil.setTempItem(
            e.inventory,
            errorItem,
            e.getCurrentItem(),
            *positions.stream().mapToInt { obj: Int? -> obj!! }.toArray()
        )
    }

    private fun setItemSuccess(e: InventoryClickEvent, name: String, vararg lore: String?) {
        val material = MailboxConfigUtil.successMaterial
        val formattedName = TextFormatHelper.getFormattedText(name)
        val formattedLore = TextFormatHelper.getFormattedTextList(*lore)
        val customModelData = MailboxConfigUtil.successCustomModelData
        val positions = getButtonPos(e.slot)

        val item = ItemUtil.newItemStack(material, 1, customModelData, formattedName, *formattedLore)
        InventoryUtil.setTempItem(
            e.inventory,
            item,
            e.getCurrentItem(),
            *positions.stream().mapToInt { obj: Int? -> obj!! }.toArray()
        )
    }

    private fun containsTaskItem(e: InventoryClickEvent): Boolean {
        val iv = e.inventory

        val errMaterial = MailboxConfigUtil.errorMaterial
        val errCustomModelData = MailboxConfigUtil.errorCustomModelData
        val sucMaterial = MailboxConfigUtil.successMaterial
        val successCustomModelData = MailboxConfigUtil.successCustomModelData

        return InventoryUtil.inventoryContains(iv, errMaterial, errCustomModelData) ||
                InventoryUtil.inventoryContains(iv, sucMaterial, successCustomModelData)
    }

    private fun cleanupExpiredMailboxes() {
        try {
            db.cleanupExpiredMailboxes()
        } catch (e: SQLException) {
            Bukkit.getScheduler().runTask(plugin, Runnable {
                plugin.getLogger().severe("Failed to cleanup expired mailboxes: " + e.message)
                e.printStackTrace(System.err)
            })
        }
    }

    override fun inventoryCloseEvent(e: InventoryCloseEvent) {
        inventories.remove(e.getInventory())
    }

    override fun closeInventoryAllPlayers() {
        for (inv in inventories.keys) {
            inv.close()
        }
    }

    override fun contains(inventory: Inventory): Boolean {
        return inventories.containsKey(inventory)
    }
}
