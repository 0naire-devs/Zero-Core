package kr.hyfata.zero.modules.mailbox.handler;

import kr.hyfata.zero.modules.mailbox.dto.Mailbox;
import kr.hyfata.zero.zeroDBCore.ZeroDB;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MailboxDB {
    public ArrayList<Mailbox> getMailboxes(Player p) throws SQLException {
        ArrayList<Mailbox> result = new ArrayList<>();
        String uuid = p.getUniqueId().toString();
        String query = "SELECT m.* " +
                "FROM mailbox m " +
                "WHERE m.uuid = ? OR (m.uuid = 'all' AND NOT EXISTS (" +
                "SELECT 1 " +
                "FROM read_mail r " +
                "WHERE r.uuid = ? " +
                "AND r.mail_id = m.mail_id" +
                ")) " +
                "ORDER BY m.sent_time";
        try (ResultSet rs = ZeroDB.executeQuery(query, uuid, uuid);
             Statement stmt = rs.getStatement();
             Connection ignored = stmt.getConnection()) {
            while (rs.next()) {
                Mailbox mailbox = new Mailbox();
                mailbox.setMailId(rs.getInt("mail_id"));
                mailbox.setUuid(rs.getString("uuid"));
                mailbox.setItem(rs.getBytes("item"));
                mailbox.setExpiryTime(rs.getTimestamp("expiry_time"));
                mailbox.setSentTime(rs.getTimestamp("sent_time"));
                result.add(mailbox);
            }
        }
        return result;
    }

    public void putMailbox(Mailbox mailbox) throws SQLException {
        ZeroDB.executeUpdate("insert into mailbox (uuid, item, expiry_time) values (?, ?, ?)",
                mailbox.getUuid(), mailbox.getItem(), mailbox.getExpiryTime());
    }

    public void readMailbox(Player p, int mailId) throws SQLException {
        String uuid = p.getUniqueId().toString();
        ZeroDB.executeUpdate("insert into read_mail values(?, ?)", uuid, mailId);
    }

    public void deleteMailbox(int mailId) throws SQLException {
        ZeroDB.executeUpdate("delete from mailbox where mail_id = ?", mailId);
    }

    public void cleanupExpiredMailboxes() throws SQLException {
        ZeroDB.executeUpdate("delete from mailbox where expiry_time < now() at time zone 'Asia/Seoul'");
    }

    public int getRemainingMailCount(Player p) throws SQLException {
        int result;
        String uuid = p.getUniqueId().toString();
        String query = "SELECT COUNT(m.*) as count " +
                "FROM mailbox m " +
                "WHERE m.uuid = ? OR (m.uuid = 'all' AND NOT EXISTS (" +
                "SELECT 1 " +
                "FROM read_mail r " +
                "WHERE r.uuid = ? AND r.mail_id = m.mail_id" +
                "))";
        try (ResultSet rs = ZeroDB.executeQuery(query, uuid, uuid);
             Statement stmt = rs.getStatement();
             Connection ignored = stmt.getConnection()) {
            rs.next();
            result = rs.getInt("count");
        }
        return result;
    }
}
