package kr.hyfata.zero.modules.mailbox;

import kr.hyfata.zero.gui.InventoryEventListener;
import kr.hyfata.zero.gui.InventoryGUI;
import kr.hyfata.zero.modules.mailbox.dto.Mailbox;
import kr.hyfata.zero.modules.mailbox.dto.MailboxButton;
import kr.hyfata.zero.modules.mailbox.dto.MailboxInventoryInfo;
import kr.hyfata.zero.modules.mailbox.handler.MailboxCommand;
import kr.hyfata.zero.modules.mailbox.handler.MailboxDB;
import kr.hyfata.zero.util.InventoryUtil;
import kr.hyfata.zero.util.ItemUtil;
import kr.hyfata.zero.util.TextFormatUtil;
import kr.hyfata.zero.util.TimeUtil;
import kr.hyfata.zero.modules.mailbox.util.MailboxConfigUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.bukkit.Bukkit.getServer;

public class ZeroMailbox implements InventoryGUI {
    private final HashMap<Inventory, MailboxInventoryInfo> inventories = new HashMap<>();
    JavaPlugin plugin;

    public ZeroMailbox(JavaPlugin plugin) {
        this.plugin = plugin;
        InventoryEventListener.registerInventory(this);
        setListeners();
        plugin.getLogger().info("Zero Mailbox has been enabled.");
    }

    private void setListeners() {
        Objects.requireNonNull(plugin.getCommand("우편함")).setExecutor(new MailboxCommand());
        Objects.requireNonNull(plugin.getCommand("우편함")).setTabCompleter(new MailboxCommand());
        getServer().getPluginManager().registerEvents(new MailboxEventListener(this), plugin);
    }

    public void onDisable() {
        for (Inventory inv : inventories.keySet()) {
            inv.close();
        }
    }

    @Override
    public void openInventory(Player p) {
        Inventory iv = Bukkit.createInventory(p, MailboxConfigUtil.getRows() * 9,
                TextFormatUtil.getFormattedText(p, MailboxConfigUtil.getMailBoxTitle()));

        p.openInventory(iv);

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            cleanupExpiredMailboxes();
            MailboxInventoryInfo info = createMailboxInventoryInfo(p);
            if (info == null)
                return;

            inventories.put(iv, info);
            setItems(iv, 1);
        });
    }

    private void setItems(Inventory iv, int page) {
        inventories.get(iv).setCurrentPage(page);
        iv.clear();

        ArrayList<Mailbox> mailboxes = inventories.get(iv).getMailboxes(); // Mailbox items

        int[] guiRows = MailboxConfigUtil.getRewardItemRowRange();
        int guiStartSlot = (guiRows[0] - 1) * 9;
        int itemCount = (guiRows[1] - guiRows[0]) * 9; // total mailbox item

        int currentPageStartIndex = (page - 1) * itemCount; // var mailbox's start index
        int currentPageEndIndex = currentPageStartIndex + itemCount;

        // set mailbox items
        for (int mailboxIndex = currentPageStartIndex, guiSlot = guiStartSlot; mailboxIndex < currentPageEndIndex; mailboxIndex++, guiSlot++) {
            if (mailboxes == null || mailboxes.isEmpty())
                break;
            if (mailboxIndex < mailboxes.size()) {
                Mailbox mailbox = mailboxes.get(mailboxIndex);
                setMailboxItemToInventory(iv, mailbox, guiSlot);
            } else {
                break;
            }
        }

        // set buttons
        setNavButton(iv, MailboxConfigUtil.getAllRewardsButton(), page);
        if (page != 1)
            setNavButton(iv, MailboxConfigUtil.getPreviousButton(), page);
        if (mailboxes != null && currentPageEndIndex < mailboxes.size())
            setNavButton(iv, MailboxConfigUtil.getNextButton(), page);
    }

    @Override
    public void inventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);
        if (containsTaskItem(e) ||
                (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getWhoClicked().getInventory())) ||
                inventories.get(e.getInventory()).isShouldCancel()
        ) {
            return;
        }

        if (InventoryUtil.isValidItem(e)) {
            onValidItemClick(e);
        }
    }

    private void onValidItemClick(InventoryClickEvent e) {
        Inventory iv = e.getInventory();

        if (buttonPosContains(MailboxConfigUtil.getPreviousButton(), e.getSlot())) {
            setItems(iv, inventories.get(iv).getCurrentPage() - 1); // goto previous page
        } else if (buttonPosContains(MailboxConfigUtil.getNextButton(), e.getSlot())) {
            setItems(iv, inventories.get(iv).getCurrentPage() + 1); // goto next page
        } else {
            rewardClickEvent(e);
        }
    }

    private void rewardClickEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Inventory iv = e.getInventory();
        ArrayList<Mailbox> mailboxes = inventories.get(iv).getMailboxes();

        if (InventoryUtil.isInventoryFull((p))) {
            setItemError(e, "&c인벤토리 공간이 부족합니다.");
        } else if (buttonPosContains(MailboxConfigUtil.getAllRewardsButton(), e.getSlot())) { // get all rewards
            if (mailboxes.isEmpty()) {
                setItemError(e, "&c이미 모든 보상을 수령했습니다!");
            } else {
                if (inventories.get(iv).isShouldCancel()) {
                    return;
                }
                inventories.get(iv).setShouldCancel(true);
                CompletableFuture.runAsync(() -> {
                    getAllRewards(e);
                    inventories.get(iv).setShouldCancel(false);
                });
            }
        } else { // get a reward
            int guiRow1 = MailboxConfigUtil.getRewardItemRowRange()[0];
            int idx = e.getSlot() - (guiRow1 - 1) * 9;

            if (inventories.get(iv).isShouldCancel()) {
                return;
            }
            inventories.get(iv).setShouldCancel(true);
            CompletableFuture.runAsync(() -> {
                try {
                    getReward(p, iv, idx);
                    setItems(iv, inventories.get(iv).getCurrentPage()); // reload inventory
                } catch (InvalidConfigurationException | SQLException ex) {
                    ex.printStackTrace(System.err);
                    setItemError(e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.getMessage());
                }
                inventories.get(iv).setShouldCancel(false);
            });
        }
    }

    private void getAllRewards(InventoryClickEvent e) {
        ArrayList<Mailbox> mailboxes = inventories.get(e.getInventory()).getMailboxes();
        int size = mailboxes.size();
        Player p = (Player) e.getWhoClicked();
        boolean inventoryFull = false;

        for (int i = 0; i < size; i++) {
            if (InventoryUtil.isInventoryFull(p)) {
                inventoryFull = true;
                break;
            }

            try {
                getReward(p, e.getInventory(), 0);
            } catch (InvalidConfigurationException | SQLException ex) {
                setItemError(e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.getMessage());
                plugin.getLogger().severe("Failed to get reward: " + ex.getMessage());
                return;
            }
        }

        setItems(e.getInventory(), 1); // goto first page
        if (inventoryFull) {
            setItemError(e, "&c인벤토리 공간이 부족합니다!", "&7여유 공간을 확보해주세요!");
        } else {
            setItemSuccess(e, "&a보상을 성공적으로 수령했습니다!");
        }
    }

    private void getReward(Player p, Inventory iv, int index) throws InvalidConfigurationException, SQLException {
        MailboxInventoryInfo info = inventories.get(iv);
        Mailbox mailbox = info.getMailboxes().get(index);

        // db
        if (mailbox.getUuid().equals("all")) {
            MailboxDB.readMailbox(p, mailbox.getMailId());
        } else {
            MailboxDB.deleteMailbox(mailbox.getMailId());
        }

        p.getInventory().addItem(ItemUtil.base64ToItemStack(mailbox.getItem()));
        inventories.get(iv).getMailboxes().remove(index);
    }

    private MailboxInventoryInfo createMailboxInventoryInfo(Player p) {
        try {
            MailboxInventoryInfo info = new MailboxInventoryInfo();
            info.setMailboxes(MailboxDB.getMailboxes(p));
            return info;
        } catch (Exception e) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                p.closeInventory();
                p.sendMessage("[우편함] 오류가 발생하여 우편함을 볼 수 없습니다!");
                e.printStackTrace(System.err);
            });
        }
        return null;
    }

    private void setMailboxItemToInventory(Inventory iv, Mailbox mailbox, int guiIndex) {
        try {
            ItemStack itemStack = ItemUtil.base64ToItemStack(mailbox.getItem());

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(""));
            lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    TimeUtil.getRemainingTimeText(mailbox.getExpiryTime())
            )); // add expire date

            iv.setItem(guiIndex, ItemUtil.addLore(itemStack, lore)); // set mailbox item
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to convert base64 to ItemStack: " + e.getMessage());
        }
    }

    private void setNavButton(Inventory iv, MailboxButton button, int page) {
        List<Integer> positions = button.getPositions();
        for (int pos : positions) {
            ItemStack item = ItemUtil.newItemStack(
                    button.getItem(), 1, button.getCustomModelData(),
                    button.getName().replace("${page}", String.valueOf(page)),
                    button.getLore(page)
            );
            iv.setItem(pos, item);
        }
    }

    private boolean buttonPosContains(MailboxButton button, int pos) {
        List<Integer> positions = button.getPositions();
        for (int p : positions) {
            if (p == pos)
                return true;
        }
        return false;
    }

    private List<Integer> getButtonPos(int pos) {
        MailboxButton getAllRewardsButton = MailboxConfigUtil.getAllRewardsButton();
        MailboxButton previousButton = MailboxConfigUtil.getPreviousButton();
        MailboxButton nextButton = MailboxConfigUtil.getNextButton();

        if (buttonPosContains(getAllRewardsButton, pos))
            return getAllRewardsButton.getPositions();
        if (buttonPosContains(previousButton, pos))
            return previousButton.getPositions();
        if (buttonPosContains(nextButton, pos))
            return nextButton.getPositions();

        return Collections.singletonList(pos);
    }

    private void setItemError(InventoryClickEvent e, String name, String... lore) {
        Material errMaterial = MailboxConfigUtil.getErrorMaterial();
        String formattedName = TextFormatUtil.getFormattedText(name);
        String[] formattedLore = TextFormatUtil.getFormattedTextList(lore);
        int customModelData = MailboxConfigUtil.getErrorCustomModelData();
        List<Integer> positions = getButtonPos(e.getSlot());

        ItemStack errorItem = ItemUtil.newItemStack(errMaterial, 1, customModelData, formattedName, formattedLore);
        InventoryUtil.setTempItem(e.getInventory(), errorItem, e.getCurrentItem(), positions.stream().mapToInt(Integer::intValue).toArray());
    }

    private void setItemSuccess(InventoryClickEvent e, String name, String... lore) {
        Material material = MailboxConfigUtil.getSuccessMaterial();
        String formattedName = TextFormatUtil.getFormattedText(name);
        String[] formattedLore = TextFormatUtil.getFormattedTextList(lore);
        int customModelData = MailboxConfigUtil.getSuccessCustomModelData();
        List<Integer> positions = getButtonPos(e.getSlot());

        ItemStack item = ItemUtil.newItemStack(material, 1, customModelData, formattedName, formattedLore);
        InventoryUtil.setTempItem(e.getInventory(), item, e.getCurrentItem(), positions.stream().mapToInt(Integer::intValue).toArray());
    }

    private boolean containsTaskItem(InventoryClickEvent e) {
        Inventory iv = e.getInventory();

        Material err_material = MailboxConfigUtil.getErrorMaterial();
        int err_customModelData = MailboxConfigUtil.getErrorCustomModelData();
        Material suc_material = MailboxConfigUtil.getSuccessMaterial();
        int suc_customModelData = MailboxConfigUtil.getSuccessCustomModelData();

        return InventoryUtil.inventoryContains(iv, err_material, err_customModelData) ||
                InventoryUtil.inventoryContains(iv, suc_material, suc_customModelData);
    }

    private void cleanupExpiredMailboxes() {
        try {
            MailboxDB.cleanupExpiredMailboxes();
        } catch (SQLException e) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                plugin.getLogger().severe("Failed to cleanup expired mailboxes: " + e.getMessage());
                e.printStackTrace(System.err);
            });
        }
    }

    @Override
    public void inventoryCloseEvent(InventoryCloseEvent e) {
        inventories.remove(e.getInventory());
    }

    @Override
    public boolean contains(Inventory inventory) {
        return inventories.containsKey(inventory);
    }
}
