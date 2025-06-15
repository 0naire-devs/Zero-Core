package kr.hyfata.zero.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

object InventoryUtil {
    fun isValidItem(e: InventoryClickEvent): Boolean {
        return e.getCurrentItem() != null && !e.getCurrentItem()!!.type
            .isAir() && (e.action != InventoryAction.HOTBAR_SWAP) && e.click != ClickType.SHIFT_LEFT && e.click != ClickType.SHIFT_RIGHT
    }

    fun resetAfterSecond(iv: Inventory, item: ItemStack?, vararg slot: Int) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                for (s in slot) {
                    iv.setItem(s, item)
                }
            }
        }, 1000)
    }

    fun setTempItem(iv: Inventory, tempItem: ItemStack?, afterItem: ItemStack?, vararg slot: Int) {
        resetAfterSecond(iv, afterItem, *slot)
        for (s in slot) {
            iv.setItem(s, tempItem)
        }
    }

    fun inventoryContains(inventory: Inventory?, material: Material?, customModelData: Int): Boolean {
        if (inventory == null || material == null) {
            return false
        }

        for (item in inventory.contents) {
            if (item != null && item.type == material) {
                if (item.hasItemMeta()) {
                    val meta = item.itemMeta
                    if (meta.hasCustomModelData() && meta.customModelData == customModelData) {
                        return true // Material과 custom_model_data가 모두 일치
                    }
                } else if (customModelData == 0) {
                    return true // customModelData가 0이고 meta가 없는 경우도 일치하는 것으로 간주
                }
            }
        }
        return false // 일치하는 아이템을 찾지 못함
    }

    fun isInventoryFull(player: Player): Boolean {
        val inventory: Inventory = player.inventory
        return isInventoryFull(inventory)
    }

    fun isInventoryFull(inventory: Inventory): Boolean {
        val firstEmpty = inventory.firstEmpty()
        return firstEmpty == -1
    }
}
