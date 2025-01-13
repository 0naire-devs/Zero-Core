package kr.hyfata.zero.modules.gui.mailbox;

import kr.hyfata.zero.modules.gui.InventoryEventListener;
import kr.hyfata.zero.modules.gui.InventoryGUI;
import kr.hyfata.zero.modules.mailbox.Mailbox;
import kr.hyfata.zero.modules.mailbox.MailboxCommand;
import kr.hyfata.zero.modules.mailbox.MailboxDB;
import kr.hyfata.zero.util.InventoryUtil;
import kr.hyfata.zero.util.ItemUtil;
import kr.hyfata.zero.util.TextFormatUtil;
import kr.hyfata.zero.util.TimeUtil;
import kr.hyfata.zero.modules.mailbox.util.MailboxConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class ZeroMailbox implements InventoryGUI {
    private final HashMap<Inventory, MailboxInventoryInfo> inventories = new HashMap<>();
    private final MailboxGuiHandler handler;
    JavaPlugin plugin;

    public ZeroMailbox(JavaPlugin plugin) {
        this.plugin = plugin;
        this.handler = new MailboxGuiHandler(plugin);
        InventoryEventListener.registerInventory(this);
        Objects.requireNonNull(plugin.getCommand("우편함")).setExecutor(new MailboxCommand());
        Objects.requireNonNull(plugin.getCommand("우편함")).setTabCompleter(new MailboxCommand());
    }

    @Override
    public void openInventory(Player p) {
        Inventory iv = Bukkit.createInventory(p, MailboxConfigUtil.getRows() * 9,
                TextFormatUtil.getFormattedText(p, MailboxConfigUtil.getMailBoxTitle()));

        p.openInventory(iv);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            MailboxInventoryInfo info = handler.createMailboxInventoryInfo(p);
            if (info == null)
                return;

            inventories.put(iv, info);
            setItems(iv, 1);
        });
    }

    public void setItems(Inventory iv, int page) {
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
                // remove expired items
                if (TimeUtil.isExpired(mailbox.getExpiryTime())) {
                    deleteMailboxFromDB(mailbox.getMailId());
                    mailboxes.remove(mailboxIndex);

                    mailboxIndex--;
                    guiSlot--;
                    continue;
                }
                handler.setMailboxItemToInventory(iv, mailbox, guiSlot);
            } else {
                break;
            }
        }

        // set buttons
        handler.setNavButton(iv, MailboxConfigUtil.getAllRewardsButton(), page);
        if (page != 1)
            handler.setNavButton(iv, MailboxConfigUtil.getPreviousButton(), page);
        if (mailboxes != null && currentPageEndIndex < mailboxes.size())
            handler.setNavButton(iv, MailboxConfigUtil.getNextButton(), page);
    }

    private void deleteMailboxFromDB(int mailId) {
        try {
            MailboxDB.deleteMailbox(mailId);
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to delete expired mailbox from DB: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void onDisable() {
        for (Inventory inv : inventories.keySet()) {
            inv.close();
        }
    }

    @Override
    public void inventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);
        if (handler.containsTaskItem(e) ||
                (e.getClickedInventory() != null && e.getClickedInventory().equals(e.getWhoClicked().getInventory()))
        ) {
            return;
        }

        if (InventoryUtil.isValidItem(e)) {
            onValidItemClick(e);
        }
    }

    private void onValidItemClick(InventoryClickEvent e) {
        Inventory iv = e.getInventory();

        if (handler.buttonPosContains(MailboxConfigUtil.getPreviousButton(), e.getSlot())) {
            setItems(iv, inventories.get(iv).getCurrentPage() - 1); // goto previous page
        } else if (handler.buttonPosContains(MailboxConfigUtil.getNextButton(), e.getSlot())) {
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
            handler.setItemError(e, "&c인벤토리 공간이 부족합니다.");
        } else if (handler.buttonPosContains(MailboxConfigUtil.getAllRewardsButton(), e.getSlot())) { // get all rewards
            if (mailboxes.isEmpty()) {
                handler.setItemError(e, "&c이미 모든 보상을 수령했습니다!");
            } else {
                CompletableFuture.runAsync(() -> getAllRewards(e));
            }
        } else { // get a reward
            int guiRow1 = MailboxConfigUtil.getRewardItemRowRange()[0];
            int idx = e.getSlot() - (guiRow1 - 1) * 9;

            CompletableFuture.runAsync(() -> {
                try {
                    getReward(p, iv, idx);
                    setItems(iv, inventories.get(iv).getCurrentPage()); // reload inventory
                } catch (InvalidConfigurationException | SQLException ex) {
                    ex.printStackTrace(System.err);
                    handler.setItemError(e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.getMessage());
                }
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
                handler.setItemError(e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.getMessage());
                plugin.getLogger().severe("Failed to get reward: " + ex.getMessage());
                return;
            }
        }

        setItems(e.getInventory(), 1); // goto first page
        if (inventoryFull) {
            handler.setItemError(e, "&c인벤토리 공간이 부족합니다!", "&7여유 공간을 확보해주세요!");
        } else {
            handler.setItemSuccess(e, "&a보상을 성공적으로 수령했습니다!");
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

    @Override
    public void inventoryCloseEvent(InventoryCloseEvent e) {
        inventories.remove(e.getInventory());
    }

    @Override
    public boolean contains(Inventory inventory) {
        return inventories.containsKey(inventory);
    }
}
