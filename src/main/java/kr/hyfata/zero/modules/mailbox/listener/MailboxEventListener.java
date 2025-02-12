package kr.hyfata.zero.modules.mailbox.listener;

import kr.hyfata.zero.modules.mailbox.ZeroMailbox;
import kr.hyfata.zero.modules.mailbox.handler.MailboxHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class MailboxEventListener implements Listener {
    ZeroMailbox zeroMailbox;
    public MailboxEventListener(ZeroMailbox zeroMailbox) {
        this.zeroMailbox = zeroMailbox;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CompletableFuture.runAsync(() -> MailboxHandler.sendRemainingMailCount(event.getPlayer()));
    }
}
