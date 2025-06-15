package kr.hyfata.zero.modules.mailbox.handler

import kr.hyfata.zero.modules.mailbox.dto.Mailbox
import kr.hyfata.zero.util.ItemUtil
import kr.hyfata.zero.util.TextFormatUtil
import kr.hyfata.zero.util.TimeUtil
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.sql.SQLException
import java.text.ParseException
import java.util.*

class MailboxHandler {
    var db: MailboxDB = MailboxDB()

    @Throws(ParseException::class)
    fun sendMailToPlayer(sender: Player, target: OfflinePlayer, expireDate: String?, expireTime: String?) {
        sendMailTo(sender, target.uniqueId.toString(), expireDate, expireTime)
    }

    @Throws(ParseException::class)
    fun sendMailToAll(sender: Player, expireDate: String?, expireTime: String?) {
        sendMailTo(sender, "all", expireDate, expireTime)
    }

    @Throws(ParseException::class)
    private fun sendMailTo(sender: Player, targetUUID: String, expireDate: String?, expireTime: String?) {
        val itemStack = sender.inventory.itemInMainHand
        if (itemStack.type.isAir()) {
            sender.sendMessage(TextFormatUtil.getFormattedText("&c손에 아무것도 들고 있지 않아 우편을 전송하지 못했습니다!"))
            return
        }
        val convertedItem = ItemUtil.itemStackToBase64(itemStack)

        val mailbox = Mailbox()
        mailbox.uuid = targetUUID
        mailbox.item = convertedItem
        mailbox.expiryTime = TimeUtil.stringToTimestamp("$expireDate $expireTime")
        try {
            db.putMailbox(mailbox)
            if (targetUUID == "all") {
                for (p in Bukkit.getOnlinePlayers()) {
                    sendRemainingMailCount(p)
                }
            } else {
                val target = Bukkit.getOfflinePlayer(UUID.fromString(targetUUID))
                if (target.isOnline) {
                    val p = target.player
                    if (p != null) {
                        sendRemainingMailCount(p)
                    }
                }
            }
            sender.sendMessage(TextFormatUtil.getFormattedText("&a우편을 성공적으로 보냈습니다!"))
        } catch (e: SQLException) {
            sender.sendMessage(TextFormatUtil.getFormattedText("&c우편을 보내는 도중 오류가 발생했습니다!"))
            e.printStackTrace(System.err)
        }
    }

    fun sendRemainingMailCount(p: Player) {
        var remainingMailCount = 0
        try {
            remainingMailCount = db.getRemainingMailCount(p)
        } catch (_: SQLException) {
        }

        if (remainingMailCount > 0) {
            p.sendMessage(TextFormatUtil.getFormattedText("&9[우편] &f새로운 우편이 &e" + remainingMailCount + "개 &f있습니다!"))
            val clickMessage: Component = LegacyComponentSerializer.legacyAmpersand().deserialize(
                TextFormatUtil.getFormattedText("&9[우편] &e&n여기&r&f를 눌러 우편함을 여세요!")
            ).clickEvent(ClickEvent.runCommand("/우편함"))
            p.sendMessage(clickMessage)
        } else {
            p.sendMessage(TextFormatUtil.getFormattedText("&9[우편] &f새로운 우편이 없습니다!"))
        }
    }
}
