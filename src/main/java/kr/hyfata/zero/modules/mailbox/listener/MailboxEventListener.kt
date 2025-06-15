package kr.hyfata.zero.modules.mailbox.listener;

import kr.hyfata.zero.modules.mailbox.ZeroMailbox;
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
        CompletableFuture.runAsync(() -> zeroMailbox.getHandler().sendRemainingMailCount(event.getPlayer()));
    }
}
