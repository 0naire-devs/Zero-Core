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
        for (int mailboxIndex = mailboxStartIndex, guiIndex = guiRow1 * 9; mailboxIndex < (mailboxStartIndex + itemCount); mailboxIndex++, guiIndex++) {
            if (mailboxes == null || mailboxes.isEmpty())
                break;
            if (mailboxIndex < mailboxes.size()) {
                // remove expired items
                if (TimeUtil.isExpired(mailboxes.get(mailboxIndex).getExpiryTime())) {
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
            setButton(iv, "buttons.get_all_rewards", page);
            if (page != 1)
                setButton(iv, "buttons.previous", page);
            if (mailboxes != null && mailboxStartIndex + itemCount < mailboxes.size())
                setButton(iv, "buttons.next", page);
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

    private void setButton(Inventory iv, String path, int page) throws Exception {
        IConfig config = ZeroCore.configModules.getMailboxConfig();
        String strPage = Integer.toString(page);

        // set item lore
        List<Component> loreComponent = new ArrayList<>();
        String[] lore = TextFormatUtil.getFormattedText(config.getString(path + ".lore", ""))
                .replace("${currentPage}", strPage)
                .split("\n");
        for (String s : lore) {
            loreComponent.add(LegacyComponentSerializer.legacyAmpersand().deserialize(s));
        }

        // set item name
        Component nameComponent = LegacyComponentSerializer.legacyAmpersand().deserialize(
                TextFormatUtil.getFormattedText(config.getString(path + ".txt", "ERROR"))
                        .replace("${currentPage}", strPage)
        );

        // set item
        iv.setItem(config.getConfig().getInt(path + ".pos"),
                ItemUtil.newItemStack(
                        Material.getMaterial(config.getString(path + ".item", "ARROW")), 0,
                        nameComponent,
                        loreComponent.toArray(new Component[0])
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

        if (InventoryUtil.isValidItem(e)) {
            IConfig config = ZeroCore.configModules.getMailboxConfig();
            Inventory iv = e.getInventory();

            if (e.getSlot() == config.getConfig().getInt("buttons.get_all_rewards.pos")) {
                getAllRewards(e);
            } else if (e.getSlot() == config.getConfig().getInt("buttons.previous.pos")) {
                setItems(iv, inventories.get(iv).getCurrentPage() - 1);
            } else if (e.getSlot() == config.getConfig().getInt("buttons.next.pos")) {
                setItems(iv, inventories.get(iv).getCurrentPage() + 1);
            }
        }
    }

    private void getAllRewards(InventoryClickEvent e) {
        if (inventories.get(e.getInventory()).getMailboxes().isEmpty()) {
            e.getInventory().close();
            e.getWhoClicked().sendMessage(TextFormatUtil.getFormattedText("&c이미 모든 보상을 수령했습니다!"));
        }
    }

    private void getReward(Player p, Inventory iv, int index) {
        MailboxInventoryInfo info = inventories.get(iv);
        Mailbox mailbox = info.getMailboxes().get(index);
        p.getInventory().addItem();
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
