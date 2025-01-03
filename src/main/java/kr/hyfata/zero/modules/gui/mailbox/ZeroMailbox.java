package kr.hyfata.zero.modules.gui.mailbox;

import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.modules.gui.InventoryEventListener;
import kr.hyfata.zero.modules.gui.InventoryGUI;
import kr.hyfata.zero.util.TextFormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class ZeroMailbox implements InventoryGUI {
    private final ArrayList<Inventory> inventories = new ArrayList<>();
    JavaPlugin plugin;

    public ZeroMailbox(JavaPlugin plugin) {
        this.plugin = plugin;
        InventoryEventListener.registerInventory(this);
    }

    @Override
    public void openInventory(Player p) {
        Inventory iv = Bukkit.createInventory(p, ZeroCore.configModules.getMailboxConfig().getConfig().getInt("mailbox.rows") * 9,
                TextFormatUtil.getFormattedText(p, ZeroCore.configModules.getMailboxConfig().getString("mailbox.title", "&cERROR")));

        setItems(iv, 1);
        p.openInventory(iv);
        inventories.add(iv);
    }

    public void setItems(Inventory iv, int page) {
        iv.clear();
    }

    public void onDisable() {
        for (Inventory inv : inventories) {
            inv.close();
        }
    }

    @Override
    public void inventoryClickEvent(InventoryClickEvent e) {

    }

    @Override
    public void inventoryCloseEvent(InventoryCloseEvent e) {
        inventories.remove(e.getInventory());
    }

    @Override
    public boolean contains(Inventory inventory) {
        return inventories.contains(inventory);
    }

}
