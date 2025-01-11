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
        ResultSet rs = ZeroDB.executeQuery(
                "SELECT m.* " +
                "FROM mailbox m " +
                "WHERE m.uuid = ? OR (m.uuid = 'all' AND NOT EXISTS (" +
                    "SELECT 1 " +
                    "FROM read_mail r " +
                    "WHERE r.uuid = ? " +
                    "AND r.mail_id = m.mail_id" +
                ")) " +
                "ORDER BY m.sent_time", uuid, uuid);
        while (rs.next()) {
            Mailbox mailbox = new Mailbox();
            mailbox.setMailId(rs.getInt("mail_id"));
            mailbox.setUuid(rs.getString("uuid"));
            mailbox.setItem(rs.getBytes("item"));
            mailbox.setExpiryTime(rs.getTimestamp("expiry_time"));
            mailbox.setSentTime(rs.getTimestamp("sent_time"));
            result.add(mailbox);
        }
        rs.close();
        return result;
    }

    public static void putMailbox(Mailbox mailbox) throws SQLException {
        ZeroDB.executeUpdate("insert into mailbox (uuid, item, expiry_time) values (?, ?, ?)",
                mailbox.getUuid(), mailbox.getItem(), mailbox.getExpiryTime());
    }

    public static void readMailbox(Player p, int mailId) throws SQLException {
        String uuid = p.getUniqueId().toString();
        ZeroDB.executeUpdate("insert into read_mail values(?, ?)", uuid, mailId);
    }

    public static void deleteMailbox(int mailId) throws SQLException {
        ZeroDB.executeUpdate("delete from mailbox where mail_id = ?", mailId);
    }

    public static int getRemainingMailCount(Player p) throws SQLException {
        int result;
        String uuid = p.getUniqueId().toString();
        ResultSet rs = ZeroDB.executeQuery(
                "SELECT COUNT(m.*) as count " +
                "FROM mailbox m " +
                "WHERE m.uuid = ? OR (m.uuid = 'all' AND NOT EXISTS (" +
                    "SELECT 1 " +
                    "FROM read_mail r " +
                    "WHERE r.uuid = ? AND r.mail_id = m.mail_id" +
                "))", uuid, uuid);
        rs.next();
        result = rs.getInt("count");
        rs.close();
        return result;
    }
}
