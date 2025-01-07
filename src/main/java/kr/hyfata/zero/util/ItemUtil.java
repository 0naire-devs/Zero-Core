package kr.hyfata.zero.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class ItemUtil {
    public static byte[] itemStackToBase64(ItemStack item) throws IllegalStateException {
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.set("i", item);
        String yamlStr = yaml.saveToString();
        return Base64.getEncoder().encode(yamlStr.getBytes());
    }

    public static ItemStack base64ToItemStack(byte[] base64) throws InvalidConfigurationException {
        String decoded = new String(Base64.getDecoder().decode(base64));
        YamlConfiguration yaml = new YamlConfiguration();
        yaml.loadFromString(decoded);
        return yaml.getItemStack("i");
    }

    public static ItemStack addLore(ItemStack item, List<Component> loreToAdd) {
        ItemMeta meta = item.getItemMeta();
        @Nullable List<Component> lore = meta.lore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        lore.addAll(loreToAdd);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack newItemStack(Material material, int num, Component name, Component... lore) {
        ItemStack chestItem = new ItemStack(material, 1, (short) num);
        ItemMeta chestMeta = chestItem.getItemMeta();

        assert chestMeta != null;
        chestMeta.addItemFlags(ItemFlag.values());
        chestMeta.displayName(name);
        chestMeta.lore(Arrays.asList(lore));

        chestItem.setItemMeta(chestMeta);
        return chestItem;
    }

    public static ItemStack newItemStack(Material material, int num, Component name) {
        ItemStack chestItem = new ItemStack(material, 1, (short) num);
        ItemMeta chestMeta = chestItem.getItemMeta();

        assert chestMeta != null;
        chestMeta.addItemFlags(ItemFlag.values());
        chestMeta.displayName(name);
        chestMeta.lore(null);

        chestItem.setItemMeta(chestMeta);
        return chestItem;
    }
}
