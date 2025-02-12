package kr.hyfata.zero.modules.mailbox;

import kr.hyfata.zero.modules.mailbox.util.MailboxUtil;
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
        CompletableFuture.runAsync(() -> MailboxUtil.sendRemainingMailCount(event.getPlayer()));
    }
}
