package kr.hyfata.zero.modules.mailbox.dto;

import org.bukkit.Material;

import java.util.List;

public class MailboxButton {
    private List<Integer> positions;
    private String name;
    private String[] lore;
    private Material item;
    private int customModelData;

    public List<Integer> getPositions() {
        return positions;
    }

    public void setPositions(List<Integer> positions) {
        this.positions = positions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getLore(int page) {
        String[] result = new String[lore.length];
        for (int i = 0; i < lore.length; i++) {
            result[i] = lore[i].replace("${page}", String.valueOf(page));
        }
        return result;
    }

    public void setLore(String[] lore) {
        this.lore = lore;
    }

    public Material getItem() {
        return item;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }
}
