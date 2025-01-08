package kr.hyfata.zero.modules.gui.mailbox;

import kr.hyfata.zero.zeroDBCore.ZeroDB;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MailboxDB {
    public static ArrayList<Mailbox> getMailbox(Player p) throws SQLException {
        ArrayList<Mailbox> result = new ArrayList<>();
        String uuid = p.getUniqueId().toString();
        ResultSet rs = ZeroDB.executeQuery("select * from mailbox where uuid = ? or uuid = 'all' order by sent_time", uuid);
        while (rs.next()) {
            Mailbox mailbox = new Mailbox();
            mailbox.setMailId(rs.getInt("mail_id"));
            mailbox.setUuid(rs.getString("uuid"));
            mailbox.setItem(rs.getBytes("item"));
            mailbox.setExpiryTime(rs.getTimestamp("expiry_time"));
            mailbox.setSentTime(rs.getTimestamp("sent_time"));
            result.add(mailbox);
        }
        return result;
    }

    public static void putMailbox(Player p, Mailbox mailbox) throws SQLException {
        String uuid = p.getUniqueId().toString();
        ZeroDB.executeUpdate("insert into mailbox (uuid, item, expiry_time) values (?, ?, ?)", uuid, mailbox.getItem(), mailbox.getExpiryTime());
    }

    public static void readMailbox(Player p, int mailId) throws SQLException {
        String uuid = p.getUniqueId().toString();
        ZeroDB.executeUpdate("insert into read_mail where uuid = ? and mail_id = ?", uuid, mailId);
    }

    public static void deleteMailbox(int mailId) throws SQLException {
        ZeroDB.executeUpdate("delete from mailbox where mail_id = ?", mailId);
    }
}
