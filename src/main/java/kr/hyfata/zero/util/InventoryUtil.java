package kr.hyfata.zero.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

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

    public static void setTempItem(InventoryClickEvent e, Material material, String name, String... lore) {
        String formattedName = TextFormatUtil.getFormattedText(name);
        String[] formattedLore = new String[lore.length];
        for (int i = 0; i < lore.length; i++) {
            formattedLore[i] = TextFormatUtil.getFormattedText(lore[i]);
        }

        InventoryUtil.resetAfterSecond(e);
        try {
            e.setCurrentItem(ItemUtil.newItemStack(material, 0, formattedName, formattedLore));
        } catch (Exception ex) {
            ex.printStackTrace(System.err); // material is null
        }
    }

    public static void setTempItem(InventoryClickEvent e, Material material, int customModelData, String name, String... lore) {
        String formattedName = TextFormatUtil.getFormattedText(name);
        String[] formattedLore = new String[lore.length];
        for (int i = 0; i < lore.length; i++) {
            formattedLore[i] = TextFormatUtil.getFormattedText(lore[i]);
        }

        try {
            ItemStack itemStack = ItemUtil.newItemStack(material, 0, formattedName, formattedLore);
            itemStack.getItemMeta().setCustomModelData(customModelData);
            InventoryUtil.resetAfterSecond(e);
            e.setCurrentItem(itemStack);
        } catch (Exception ex) {
            ex.printStackTrace(System.err); // material is null
        }
    }

    public static boolean inventoryContains(Inventory inventory, Material material, int customModelData) {
        if (inventory == null || material == null) {
            return false;
        }

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() == material) {
                if (item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta.hasCustomModelData() && meta.getCustomModelData() == customModelData) {
                        return true; // Material과 custom_model_data가 모두 일치
                    }
                } else if (customModelData == 0){
                    return true; // customModelData가 0이고 meta가 없는 경우도 일치하는 것으로 간주
                }
            }
        }
        return false; // 일치하는 아이템을 찾지 못함
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
