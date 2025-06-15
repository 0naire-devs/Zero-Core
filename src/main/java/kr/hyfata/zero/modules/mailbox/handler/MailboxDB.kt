package kr.hyfata.zero.modules.mailbox.handler

import kr.hyfata.zero.modules.mailbox.dto.Mailbox
import kr.hyfata.zero.zeroDBCore.ZeroDB
import org.bukkit.entity.Player
import java.sql.SQLException
import kotlin.collections.ArrayList

class MailboxDB {
    @Throws(SQLException::class)
    fun getMailboxes(p: Player): ArrayList<Mailbox?> {
        val result = ArrayList<Mailbox?>()
        val uuid = p.uniqueId.toString()
        val query = "SELECT m.* " +
                "FROM mailbox m " +
                "WHERE m.uuid = ? OR (m.uuid = 'all' AND NOT EXISTS (" +
                "SELECT 1 " +
                "FROM read_mail r " +
                "WHERE r.uuid = ? " +
                "AND r.mail_id = m.mail_id" +
                ")) " +
                "ORDER BY m.sent_time"
        ZeroDB.executeQuery(query, uuid, uuid).use { rs ->
            rs.statement.use { stmt ->
                stmt.connection.use { ignored ->
                    while (rs.next()) {
                        val mailbox = Mailbox()
                        mailbox.mailId = rs.getInt("mail_id")
                        mailbox.uuid = rs.getString("uuid")
                        mailbox.item = rs.getBytes("item")
                        mailbox.expiryTime = rs.getTimestamp("expiry_time")
                        mailbox.sentTime = rs.getTimestamp("sent_time")
                        result.add(mailbox)
                    }
                }
            }
        }
        return result
    }

    @Throws(SQLException::class)
    fun putMailbox(mailbox: Mailbox) {
        ZeroDB.executeUpdate(
            "insert into mailbox (uuid, item, expiry_time) values (?, ?, ?)",
            mailbox.uuid, mailbox.item, mailbox.expiryTime
        )
    }

    @Throws(SQLException::class)
    fun readMailbox(p: Player, mailId: Int) {
        val uuid = p.uniqueId.toString()
        ZeroDB.executeUpdate("insert into read_mail values(?, ?)", uuid, mailId)
    }

    @Throws(SQLException::class)
    fun deleteMailbox(mailId: Int) {
        ZeroDB.executeUpdate("delete from mailbox where mail_id = ?", mailId)
    }

    @Throws(SQLException::class)
    fun cleanupExpiredMailboxes() {
        ZeroDB.executeUpdate("delete from mailbox where expiry_time < now() at time zone 'Asia/Seoul'")
    }

    @Throws(SQLException::class)
    fun getRemainingMailCount(p: Player): Int {
        val result: Int
        val uuid = p.uniqueId.toString()
        val query = "SELECT COUNT(m.*) as count " +
                "FROM mailbox m " +
                "WHERE (m.expiry_time > now() at time zone 'Asia/Seoul') " +
                "AND (m.uuid = ? OR (m.uuid = 'all' AND NOT EXISTS (" +
                "SELECT 1 " +
                "FROM read_mail r " +
                "WHERE r.uuid = ? AND r.mail_id = m.mail_id" +
                ")))"
        ZeroDB.executeQuery(query, uuid, uuid).use { rs ->
            rs.statement.use { stmt ->
                stmt.connection.use { ignored ->
                    rs.next()
                    result = rs.getInt("count")
                }
            }
        }
        return result
    }
}
