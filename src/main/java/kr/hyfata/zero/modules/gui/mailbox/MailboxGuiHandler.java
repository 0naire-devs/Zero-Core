package kr.hyfata.zero.modules.gui.mailbox;

import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.config.IConfig;
import kr.hyfata.zero.modules.mailbox.Mailbox;
import kr.hyfata.zero.modules.mailbox.MailboxDB;
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

    public void setMailboxItemInventory(Inventory iv, Mailbox mailbox, int guiIndex) {
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

    public void setNavButton(Inventory iv, String configPath) {
        IConfig config = ZeroCore.configModules.getMailboxConfig();

        // set item name
        String name = TextFormatUtil.getFormattedText(config.getString(configPath + ".txt", "ERROR"));

        // set item lore
        String[] lore = TextFormatUtil.getFormattedText(config.getString(configPath + ".lore", ""))
                .split("\n");

        // set item(nav button)
        try {
            iv.setItem(config.getConfig().getInt(configPath + ".pos"),
                    ItemUtil.newItemStack(
                            Material.getMaterial(config.getString(configPath + ".item", "ARROW")), 0,
                            name,
                            lore
                    )
            );
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to convert config to ItemStack: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void setItemError(InventoryClickEvent e, String name, String... lore) {
        IConfig config = ZeroCore.configModules.getMailboxConfig();
        Material material = Material.getMaterial(config.getString("task.error.item", "RED_CONCRETE"));
        int customModelData = config.getConfig().getInt("task.error.custom_model_data", 0);
        InventoryUtil.setTempItem(e, material, customModelData, name, lore);
    }

    public void setItemSuccess(InventoryClickEvent e, String name, String... lore) {
        IConfig config = ZeroCore.configModules.getMailboxConfig();
        Material material = Material.getMaterial(config.getString("task.success.item", "GREEN_CONCRETE"));
        int customModelData = config.getConfig().getInt("task.success.custom_model_data", 0);
        InventoryUtil.setTempItem(e, material, customModelData, name, lore);
    }

    public boolean containsTaskItem(InventoryClickEvent e) {
        IConfig config = ZeroCore.configModules.getMailboxConfig();
        Inventory iv = e.getInventory();

        Material err_material = Material.getMaterial(config.getString("task.error.item", "RED_CONCRETE"));
        int err_customModelData = config.getConfig().getInt("task.error.custom_model_data", 0);
        Material suc_material = Material.getMaterial(config.getString("task.success.item", "GREEN_CONCRETE"));
        int suc_customModelData = config.getConfig().getInt("task.success.custom_model_data", 0);

        return InventoryUtil.inventoryContains(iv, err_material, err_customModelData) ||
                InventoryUtil.inventoryContains(iv, suc_material, suc_customModelData);
    }
}
