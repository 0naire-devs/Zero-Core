package kr.hyfata.zero.modules.mailbox.util;

import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.config.IConfig;
import kr.hyfata.zero.modules.gui.mailbox.MailboxButton;
import kr.hyfata.zero.util.TextFormatUtil;
import org.bukkit.Material;

public class MailboxConfigUtil {
    public static IConfig getConfig() {
        return ZeroCore.configModules.getMailboxConfig();
    }

    public static String getMailBoxTitle() {
        return getConfig().getString("mailbox.title", "ERROR");
    }

    public static int getRows() {
        return getConfig().getConfig().getInt("mailbox.rows", 6);
    }

    public static int[] getRewardItemRowRange() {
        int[] result = new int[2];
        result[0] = getConfig().getConfig().getInt("mailbox.reward_item_row_start", 1);
        result[1] = getConfig().getConfig().getInt("mailbox.reward_item_row_end", 6);
        return result;
    }

    public static Material getErrorMaterial() {
        return Material.getMaterial(getConfig().getString("task.error.item", "RED_CONCRETE"));
    }

    public static int getErrorCustomModelData() {
        return getConfig().getConfig().getInt("task.error.custom_model_data", 0);
    }

    public static Material getSuccessMaterial() {
        return Material.getMaterial(getConfig().getString("task.success.item", "GREEN_CONCRETE"));
    }

    public static int getSuccessCustomModelData() {
        return getConfig().getConfig().getInt("task.success.custom_model_data", 0);
    }

    private static MailboxButton getButton(String path) {
        MailboxButton result = new MailboxButton();
        IConfig config = getConfig();

        result.setPositions(config.getConfig().getIntegerList(path + ".pos"));
        result.setName(TextFormatUtil.getFormattedText(config.getString(path + ".txt", "ERROR")));
        result.setLore(TextFormatUtil.getFormattedText(config.getString(path + ".lore", "")).split("\n"));
        result.setItem(Material.getMaterial(config.getString(path + ".item", "STONE")));
        result.setCustomModelData(config.getConfig().getInt(path + ".custom_model_data", 0));
        return result;
    }

    public static MailboxButton getAllRewardsButton() {
        return getButton("buttons.get_all_rewards");
    }

    public static MailboxButton getPreviousButton() {
        return getButton("buttons.previous");
    }

    public static MailboxButton getNextButton() {
        return getButton("buttons.next");
    }
}
