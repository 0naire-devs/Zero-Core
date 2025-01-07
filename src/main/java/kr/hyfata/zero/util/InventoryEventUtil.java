package kr.hyfata.zero.util;

import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryEventUtil {
    public static boolean isValidItem(InventoryClickEvent e) {
        return e.getCurrentItem() != null && !e.getCurrentItem().getType().isAir();
    }
}
