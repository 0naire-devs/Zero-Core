package kr.hyfata.zero.modules.gui.mailbox;

import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.config.IConfig;
import kr.hyfata.zero.modules.gui.InventoryEventListener;
import kr.hyfata.zero.modules.gui.InventoryGUI;
import kr.hyfata.zero.util.InventoryUtil;
import kr.hyfata.zero.util.ItemUtil;
import kr.hyfata.zero.util.TextFormatUtil;
import kr.hyfata.zero.util.TimeUtil;
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

public class ZeroMailbox implements InventoryGUI {
    private final HashMap<Inventory, MailboxInventoryInfo> inventories = new HashMap<>();
    JavaPlugin plugin;

    public ZeroMailbox(JavaPlugin plugin) {
        this.plugin = plugin;
        InventoryEventListener.registerInventory(this);
        Objects.requireNonNull(plugin.getCommand("우편함")).setExecutor(new MailboxCommand());
        Objects.requireNonNull(plugin.getCommand("우편함")).setTabCompleter(new MailboxCommand());
    }

    @Override
    public void openInventory(Player p) {
        Inventory iv = Bukkit.createInventory(p, ZeroCore.configModules.getMailboxConfig().getConfig().getInt("mailbox.rows") * 9,
                TextFormatUtil.getFormattedText(p, ZeroCore.configModules.getMailboxConfig().getString("mailbox.title", "&cERROR")));

        p.openInventory(iv);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                MailboxInventoryInfo info = new MailboxInventoryInfo();
                info.setMailboxes(MailboxDB.getMailbox(p));
                inventories.put(iv, info);
            } catch (Exception e) {
                p.closeInventory();
                p.sendMessage("[우편함] 오류가 발생하여 우편함을 볼 수 없습니다!");
                e.printStackTrace(System.err);
            }
            setItems(iv, 1);
        });
    }

    public void setItems(Inventory iv, int page) {
        inventories.get(iv).setCurrentPage(page);
        iv.clear();

        ArrayList<Mailbox> mailboxes = inventories.get(iv).getMailboxes(); // Mailbox items
        IConfig config = ZeroCore.configModules.getMailboxConfig();

        int guiRow1 = config.getConfig().getInt("mailbox.reward_item_row1");
        int guiRow2 = config.getConfig().getInt("mailbox.reward_item_row2");
        int itemCount = (guiRow2 - guiRow1) * 9; // total mailbox item
        int mailboxStartIndex = (page - 1) * itemCount; // var mailbox's start index

        // set mailbox items
        for (int mailboxIndex = mailboxStartIndex, guiIndex = (guiRow1 - 1) * 9; mailboxIndex < (mailboxStartIndex + itemCount); mailboxIndex++, guiIndex++) {
            if (mailboxes == null || mailboxes.isEmpty())
                break;
            if (mailboxIndex < mailboxes.size()) {
                // remove expired items
                if (TimeUtil.isExpired(mailboxes.get(mailboxIndex).getExpiryTime())) {
                    try {
                        MailboxDB.deleteMailbox(mailboxes.get(mailboxIndex).getMailId());
                    } catch (SQLException e) {
                        plugin.getLogger().severe("Failed to delete expired mailbox from DB: " + e.getMessage());
                        e.printStackTrace(System.err);
                    }
                    mailboxes.remove(mailboxIndex);
                    mailboxIndex--;
                    guiIndex--;
                    continue;
                }
                setMailboxItem(iv, mailboxes.get(mailboxIndex), guiIndex);
            } else {
                break;
            }
        }

        // set buttons
        try {
            setNavButton(iv, "buttons.get_all_rewards", page);
            if (page != 1)
                setNavButton(iv, "buttons.previous", page);
            if (mailboxes != null && mailboxStartIndex + itemCount < mailboxes.size())
                setNavButton(iv, "buttons.next", page);
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to convert mailbox to ItemStack: " + e.getMessage());
        }
    }

    private void setMailboxItem(Inventory iv, Mailbox mailbox, int guiIndex) {
        try {
            ItemStack itemStack = ItemUtil.base64ToItemStack(mailbox.getItem());
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(""));
            lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    TimeUtil.getRemainingTimeText(mailbox.getExpiryTime())
            ));
            iv.setItem(guiIndex, ItemUtil.addLore(itemStack, lore));
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to convert base64 to ItemStack: " + e.getMessage());
        }
    }

    private void setNavButton(Inventory iv, String path, int page) throws Exception {
        IConfig config = ZeroCore.configModules.getMailboxConfig();
        String strPage = Integer.toString(page);

        // set item name
        String name = TextFormatUtil.getFormattedText(config.getString(path + ".txt", "ERROR"))
                .replace("${currentPage}", strPage);

        // set item lore
        String[] lore = TextFormatUtil.getFormattedText(config.getString(path + ".lore", ""))
                .replace("${currentPage}", strPage)
                .split("\n");

        // set item(nav button)
        iv.setItem(config.getConfig().getInt(path + ".pos"),
                ItemUtil.newItemStack(
                        Material.getMaterial(config.getString(path + ".item", "ARROW")), 0,
                        name,
                        lore
                )
        );
    }

    public void onDisable() {
        for (Inventory inv : inventories.keySet()) {
            inv.close();
        }
    }

    @Override
    public void inventoryClickEvent(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getInventory().contains(Material.RED_CONCRETE) ||
                e.getInventory().contains(Material.GREEN_CONCRETE)) {
            return;
        }

        if (InventoryUtil.isValidItem(e)) {
            ArrayList<Mailbox> mailboxes = inventories.get(e.getInventory()).getMailboxes();
            IConfig config = ZeroCore.configModules.getMailboxConfig();
            Inventory iv = e.getInventory();

            int getAllRewardButtonPos = config.getConfig().getInt("buttons.get_all_rewards.pos");
            int previousButtonPos = config.getConfig().getInt("buttons.previous.pos");
            int nextButtonPos = config.getConfig().getInt("buttons.next.pos");

            if (e.getSlot() == getAllRewardButtonPos) { // get all rewards
                if (mailboxes.isEmpty()) {
                    setTempItem(Material.RED_CONCRETE, e, "&c이미 모든 보상을 수령했습니다!");
                } else if (InventoryUtil.isInventoryFull((Player) e.getWhoClicked())) {
                    setTempItem(Material.RED_CONCRETE, e, "&c우편물 수령 불가!",
                            "&7인벤토리 공간이 충분하지 않습니다.",
                            "&7인벤토리 공간을 충분히 확보한 후 다시 시도해주세요!");
                } else {
                    getAllRewards(e);
                }
            } else if (e.getSlot() == previousButtonPos) {
                setItems(iv, inventories.get(iv).getCurrentPage() - 1); // goto previous page
            } else if (e.getSlot() == nextButtonPos) {
                setItems(iv, inventories.get(iv).getCurrentPage() + 1); // goto next page
            } else if (InventoryUtil.isInventoryFull((Player) e.getWhoClicked())) {
                setTempItem(Material.RED_CONCRETE, e, "&c인벤토리 공간이 부족합니다.");
            } else { // get a reward
                int guiRow1 = config.getConfig().getInt("mailbox.reward_item_row1");
                int idx = e.getSlot() - (guiRow1 - 1) * 9;
                try {
                    getReward((Player) e.getWhoClicked(), e.getInventory(), idx);
                    setItems(iv, inventories.get(iv).getCurrentPage()); // reload inventory
                } catch (InvalidConfigurationException | SQLException ex) {
                    plugin.getLogger().severe("Filed to get reward: " + ex.getMessage());
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    private void getAllRewards(InventoryClickEvent e) {
        ArrayList<Mailbox> mailboxes = inventories.get(e.getInventory()).getMailboxes();
        for (int i = 0; i < mailboxes.size(); i++) {
            if (InventoryUtil.isInventoryFull((Player) e.getWhoClicked())) {
                setTempItem(Material.RED_CONCRETE, e, "&c인벤토리 공간이 부족합니다!", "&c여유 공간을 확보해주세요!");
                break;
            }

            try {
                getReward((Player) e.getWhoClicked(), e.getInventory(), i);
            } catch (InvalidConfigurationException | SQLException ex) {
                setTempItem(Material.RED_CONCRETE, e, "&c보상을 수령받는 도중 오류가 발생했습니다!", "&c" + ex.getMessage());
                plugin.getLogger().severe("Failed to get reward: " + ex.getMessage());
            }
        }
        setItems(e.getInventory(), 1); // goto first page
    }

    private void setTempItem(Material material, InventoryClickEvent e, String name, String... lore) {
        String formattedName = TextFormatUtil.getFormattedText(name);
        String[] formattedLore = new String[lore.length];
        for (int i = 0; i < lore.length; i++) {
            formattedLore[i] = TextFormatUtil.getFormattedText(lore[i]);
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            InventoryUtil.resetAfterSecond(e);
            try {
                e.setCurrentItem(ItemUtil.newItemStack(material, 0, formattedName, formattedLore));
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private void getReward(Player p, Inventory iv, int index) throws InvalidConfigurationException, SQLException {
        MailboxInventoryInfo info = inventories.get(iv);
        Mailbox mailbox = info.getMailboxes().get(index);

        p.getInventory().addItem(ItemUtil.base64ToItemStack(mailbox.getItem()));

        // db
        if (mailbox.getUuid().equals("all")) {
            MailboxDB.readMailbox(p, mailbox.getMailId());
        } else {
            MailboxDB.deleteMailbox(mailbox.getMailId());
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
