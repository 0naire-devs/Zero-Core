package kr.hyfata.zero.util;

import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.vault.VaultUtil;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class TextFormatUtil {
    public static String numberFont(String text) {
        return text.replace("1", "\uF801\uE024")
                .replace("2", "\uF801\uE025")
                .replace("3", "\uF801\uE026")
                .replace("4", "\uF801\uE027")
                .replace("5", "\uF801\uE028")
                .replace("6", "\uF801\uE029")
                .replace("7", "\uF801\uE02A")
                .replace("8", "\uF801\uE02B")
                .replace("9", "\uF801\uE02C")
                .replace("0", "\uF801\uE02D")
                .replace(",", "\uF801\uE02E");
    }

    public static String getFormattedText(Player player, String text) {
        return getFormattedText(text)
                .replace("${formattedBalance}", numberFont(getFormattedBalance(player)))
                .replace("${balance}", getFormattedBalance(player))
                .replace("${playerName}", player.getName())
                .replace("${world}", getFormattedWorld(player.getWorld().getName()));
    }

    public static String getFormattedText(String text) {
        return text.replace("&","§")
                .replace("\\\n", "");
    }

    public static String[] getFormattedTextList(String... texts) {
        for (int i = 0; i < texts.length; i++) {
            texts[i] = getFormattedText(texts[i]);
        }
        return texts;
    }

    private static String getFormattedBalance(Player player) {
        double balance = VaultUtil.getBalance(player);
        DecimalFormat df = new DecimalFormat("#,###"); // 소수점 이하 자리 제거
        return df.format(balance);
    }

    public static String getFormattedWorld(String world) {
        return ZeroCore.configModules.getScoreboardConfig().getString("worlds." + world, world);
    }
}