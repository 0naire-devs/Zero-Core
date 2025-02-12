package kr.hyfata.zero.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public interface InventoryGUI {
    void openInventory(Player p);
    void inventoryClickEvent(InventoryClickEvent e);
    void inventoryCloseEvent(InventoryCloseEvent e);
    boolean contains(org.bukkit.inventory.Inventory inventory);
}