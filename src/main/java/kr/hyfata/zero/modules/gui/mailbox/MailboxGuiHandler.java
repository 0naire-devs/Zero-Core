package kr.hyfata.zero.modules.gui.mailbox;

import kr.hyfata.zero.modules.mailbox.Mailbox;
import kr.hyfata.zero.modules.mailbox.MailboxDB;
import kr.hyfata.zero.modules.mailbox.util.MailboxConfigUtil;
import kr.hyfata.zero.util.InventoryUtil;
import kr.hyfata.zero.util.ItemUtil;
import kr.hyfata.zero.util.TimeUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MailboxGuiHandler {
    private final JavaPlugin plugin;

    public MailboxGuiHandler(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public MailboxInventoryInfo createMailboxInventoryInfo(Player p) {
        try {
            MailboxInventoryInfo info = new MailboxInventoryInfo();
            info.setMailboxes(MailboxDB.getMailboxes(p));
            return info;
        } catch (Exception e) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                p.closeInventory();
                p.sendMessage("[우편함] 오류가 발생하여 우편함을 볼 수 없습니다!");
                e.printStackTrace(System.err);
            });
        }
        return null;
    }

    public void setMailboxItemToInventory(Inventory iv, Mailbox mailbox, int guiIndex) {
        try {
            ItemStack itemStack = ItemUtil.base64ToItemStack(mailbox.getItem());

            List<Component> lore = new ArrayList<>();
            lore.add(Component.text(""));
            lore.add(LegacyComponentSerializer.legacyAmpersand().deserialize(
                    TimeUtil.getRemainingTimeText(mailbox.getExpiryTime())
            )); // add expire date

            iv.setItem(guiIndex, ItemUtil.addLore(itemStack, lore)); // set mailbox item
        } catch (InvalidConfigurationException e) {
            plugin.getLogger().severe("Failed to convert base64 to ItemStack: " + e.getMessage());
        }
    }

    public boolean buttonPosContains(MailboxButton button, int pos) {
        List<Integer> positions = button.getPositions();
        for (int p : positions) {
            if (p == pos)
                return true;
        }
        return false;
    }

    public void setNavButton(Inventory iv, MailboxButton button, int page) {
        List<Integer> positions = button.getPositions();
        for (int pos : positions) {
            try {
                ItemStack item = ItemUtil.newItemStack(
                        button.getItem(), 0,
                        button.getName().replace("${page}", String.valueOf(page)),
                        button.getLore(page)
                );
                item.getItemMeta().setCustomModelData(button.getCustomModelData());
                iv.setItem(pos, item);
            } catch (Exception e) {
                plugin.getLogger().severe("Failed to convert config to ItemStack: " + e.getMessage());
                e.printStackTrace(System.err);
            }
        }
    }

    public void setItemError(InventoryClickEvent e, String name, String... lore) {
        Material material = MailboxConfigUtil.getErrorMaterial();
        int customModelData = MailboxConfigUtil.getErrorCustomModelData();
        InventoryUtil.setTempItem(e, material, customModelData, name, lore);
    }

    public void setItemSuccess(InventoryClickEvent e, String name, String... lore) {
        Material material = MailboxConfigUtil.getSuccessMaterial();
        int customModelData = MailboxConfigUtil.getSuccessCustomModelData();
        InventoryUtil.setTempItem(e, material, customModelData, name, lore);
    }

    public boolean containsTaskItem(InventoryClickEvent e) {
        Inventory iv = e.getInventory();

        Material err_material = MailboxConfigUtil.getErrorMaterial();
        int err_customModelData = MailboxConfigUtil.getErrorCustomModelData();
        Material suc_material = MailboxConfigUtil.getSuccessMaterial();
        int suc_customModelData = MailboxConfigUtil.getSuccessCustomModelData();

        return InventoryUtil.inventoryContains(iv, err_material, err_customModelData) ||
                InventoryUtil.inventoryContains(iv, suc_material, suc_customModelData);
    }
}
