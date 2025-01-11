package kr.hyfata.zero;

import kr.hyfata.zero.util.MailboxUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.concurrent.CompletableFuture;

public class GlobalEventListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        CompletableFuture.runAsync(() -> MailboxUtil.sendRemainingMailCount(event.getPlayer()));
    }
}
