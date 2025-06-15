package kr.hyfata.zero.modules.mailbox.listener

import kr.hyfata.zero.modules.mailbox.ZeroMailbox
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.concurrent.CompletableFuture

class MailboxEventListener(var zeroMailbox: ZeroMailbox) : Listener {
    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        CompletableFuture.runAsync { zeroMailbox.handler.sendRemainingMailCount(event.getPlayer()) }
    }
}
