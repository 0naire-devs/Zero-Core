package kr.hyfata.zero.gui;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;

public class InventoryEventListener implements Listener {
    private static final ArrayList<InventoryGUI> inventories = new ArrayList<>();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        for (InventoryGUI menu : inventories) {
            if (menu.contains(e.getInventory())) {
                menu.inventoryClickEvent(e);
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        for (InventoryGUI menu : inventories) {
            if (menu.contains(e.getInventory())) {
                menu.inventoryCloseEvent(e);
            }
        }
    }

    public static void registerInventory(InventoryGUI inventory) {
        inventories.add(inventory);
    }
}
