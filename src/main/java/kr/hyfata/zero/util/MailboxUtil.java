package kr.hyfata.zero.util;

import kr.hyfata.zero.modules.gui.mailbox.Mailbox;
import kr.hyfata.zero.modules.gui.mailbox.MailboxDB;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.text.ParseException;

public class MailboxUtil {
    public static void sendMailToPlayer(Player sender, OfflinePlayer player, String expireDate, String expireTime) throws ParseException {
        ItemStack itemStack = sender.getInventory().getItemInMainHand();
        byte[] convertedItem = ItemUtil.itemStackToBase64(itemStack);

        Mailbox mailbox = new Mailbox();
        mailbox.setUuid(player.getUniqueId().toString());
        mailbox.setItem(convertedItem);
        mailbox.setExpiryTime(TimeUtil.stringToTimestamp(expireDate + " " + expireTime));
        try {
            MailboxDB.putMailbox(mailbox);
            if (player.isOnline())
                sendRemainingMailCount(player.getPlayer());
            sender.sendMessage(TextFormatUtil.getFormattedText("&a우편을 성공적으로 보냈습니다!"));
        } catch (SQLException e) {
            sender.sendMessage(TextFormatUtil.getFormattedText("&c우편을 보내는 도중 오류가 발생했습니다!"));
            e.printStackTrace(System.err);
        }
    }

    public static void sendMailToAll(Player sender, String expireDate, String expireTime) throws ParseException {
        ItemStack itemStack = sender.getInventory().getItemInMainHand();
        byte[] convertedItem = ItemUtil.itemStackToBase64(itemStack);

        Mailbox mailbox = new Mailbox();
        mailbox.setUuid("all");
        mailbox.setItem(convertedItem);
        mailbox.setExpiryTime(TimeUtil.stringToTimestamp(expireDate + " " + expireTime));
        try {
            MailboxDB.putMailbox(mailbox);
            for (Player p : Bukkit.getOnlinePlayers()) {
                sendRemainingMailCount(p);
            }
            sender.sendMessage(TextFormatUtil.getFormattedText("&a우편을 성공적으로 보냈습니다!"));
        } catch (SQLException e) {
            sender.sendMessage(TextFormatUtil.getFormattedText("&c우편을 보내는 도중 오류가 발생했습니다!"));
            e.printStackTrace(System.err);
        }
    }
    
    public static void sendRemainingMailCount(Player p) {
        int remainingMailCount = 0;
        try {
            remainingMailCount = MailboxDB.getRemainingMailCount(p);
        } catch (SQLException ignored) {}

        if (remainingMailCount > 0) {
            p.sendMessage(TextFormatUtil.getFormattedText("&9[우편] &f새로운 우편이 &e" + remainingMailCount + "개 &f있습니다!"));
            Component clickMessage = LegacyComponentSerializer.legacyAmpersand().deserialize(
                    TextFormatUtil.getFormattedText("&9[우편] &e&n여기&r&f를 눌러 우편함을 여세요!")
            ).clickEvent(ClickEvent.runCommand("/우편함"));
            p.sendMessage(clickMessage);
        } else {
            p.sendMessage(TextFormatUtil.getFormattedText("&9[우편] &f새로운 우편이 없습니다!"));
        }
    }
}
