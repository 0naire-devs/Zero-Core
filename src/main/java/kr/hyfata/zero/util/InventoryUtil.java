package kr.hyfata.zero.util;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Timer;

public class InventoryUtil {
    public static boolean isValidItem(InventoryClickEvent e) {
        return e.getCurrentItem() != null && !e.getCurrentItem().getType().isAir() &&
                !e.getAction().equals(InventoryAction.HOTBAR_SWAP) &&
                e.getClick() != ClickType.SHIFT_LEFT && e.getClick() != ClickType.SHIFT_RIGHT;
    }

    public static void resetAfterSecond(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        Timer timer = new Timer();
        timer.schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                e.getInventory().setItem(e.getSlot(), item);
            }
        }, 1000);
    }

    public static boolean isInventoryFull(Player player) {
        Inventory inventory = player.getInventory();
        return isInventoryFull(inventory);
    }

    public static boolean isInventoryFull(Inventory inventory) {
        int firstEmpty = inventory.firstEmpty();
        return firstEmpty == -1;
    }
}
