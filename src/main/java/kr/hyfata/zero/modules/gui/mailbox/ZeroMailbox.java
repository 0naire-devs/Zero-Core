package kr.hyfata.zero.modules.gui.mailbox;

import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.config.IConfig;
import kr.hyfata.zero.modules.gui.InventoryEventListener;
import kr.hyfata.zero.modules.gui.InventoryGUI;
import kr.hyfata.zero.util.InventoryEventUtil;
import kr.hyfata.zero.util.ItemUtil;
import kr.hyfata.zero.util.TextFormatUtil;
import kr.hyfata.zero.util.TimeUtil;
import net.kyori.adventure.text.Component;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ZeroMailbox implements InventoryGUI {
    private final HashMap<Inventory, MailboxInventoryInfo> inventories = new HashMap<>();
    JavaPlugin plugin;

    public ZeroMailbox(JavaPlugin plugin) {
        this.plugin = plugin;
        InventoryEventListener.registerInventory(this);
    }

    @Override
    public void openInventory(Player p) {
        Inventory iv = Bukkit.createInventory(p, ZeroCore.configModules.getMailboxConfig().getConfig().getInt("mailbox.rows") * 9,
                TextFormatUtil.getFormattedText(p, ZeroCore.configModules.getMailboxConfig().getString("mailbox.title", "&cERROR")));

        p.openInventory(iv);
        CompletableFuture.runAsync(() -> {
            try {
                MailboxInventoryInfo info = new MailboxInventoryInfo();
                info.setMailboxes(MailboxDB.getMailbox(p));
                inventories.put(iv, info);
            } catch (SQLException e) {
                p.closeInventory();
                p.sendMessage("[우편함] 오류가 발생하여 우편함을 볼 수 없습니다!");
                return;
            }
            setItems(iv, 1);
        });
    }

    public void setItems(Inventory iv, int page) {
        inventories.get(iv).setCurrentPage(page);
        iv.clear();

        ArrayList<Mailbox> mailbox = inventories.get(iv).getMailboxes(); // Mailbox items
        IConfig config = ZeroCore.configModules.getMailboxConfig();

        int row1 = config.getConfig().getInt("mailbox.reward_item_row1");
        int row2 = config.getConfig().getInt("mailbox.reward_item_row2");
        int itemCount = (row2 - row1) * 9; // total mailbox item
        int startIndex = (page - 1) * itemCount; // var mailbox's start index

        // set mailbox items
        for (int mailboxIndex = startIndex, guiIndex = row1 * 9; mailboxIndex < (startIndex + itemCount); mailboxIndex++, guiIndex++) {
            if (mailbox == null)
                break;
            if (mailboxIndex < mailbox.size()) {
                try {
                    ItemStack itemStack = ItemUtil.base64ToItemStack(mailbox.get(mailboxIndex).getItem());
                    List<Component> lore = new ArrayList<>();
                    lore.add(Component.text(""));
                    lore.add(Component.text(TimeUtil.getRemainingTimeText(mailbox.get(mailboxIndex).getExpiryTime())));
                    iv.setItem(guiIndex, ItemUtil.addLore(itemStack, lore));
                } catch (InvalidConfigurationException e) {
                    plugin.getLogger().severe("Failed to convert base64 to ItemStack: " + e.getMessage());
                }
            }
        }

        setButton(iv, "buttons.get_all_rewards", page);
        if (page != 1)
            setButton(iv, "buttons.previous", page);
        if (mailbox != null && startIndex + itemCount > mailbox.size())
            setButton(iv, "buttons.next", page);
    }

    private void setButton(Inventory iv, String path, int page) {
        IConfig config = ZeroCore.configModules.getMailboxConfig();
        String strPage = Integer.toString(page);
        iv.setItem(config.getConfig().getInt(path + ".pos"),
                ItemUtil.newItemStack(
                        Material.getMaterial(config.getString(path + ".item", "minecraft:arrow")), 0,
                        Component.text(TextFormatUtil.getFormattedText(config.getString(path + ".txt", "ERROR")).replace("${currentPage}", strPage)),
                        Component.text(TextFormatUtil.getFormattedText(config.getString(path + ".lore", "ERROR")).replace("${currentPage}", strPage))
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
        if (InventoryEventUtil.isValidItem(e)) {
            IConfig config = ZeroCore.configModules.getMailboxConfig();
            Inventory iv = e.getInventory();

            if (e.getSlot() == config.getConfig().getInt("buttons.get_all_rewards.pos")) {
                e.setCancelled(true);
            } else if (e.getSlot() == config.getConfig().getInt("buttons.previous.pos")) {
                e.setCancelled(true);
                setItems(iv, inventories.get(iv).getCurrentPage() - 1);
            } else if (e.getSlot() == config.getConfig().getInt("buttons.next.pos")) {
                e.setCancelled(true);
                setItems(iv, inventories.get(iv).getCurrentPage() + 1);
            }
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
