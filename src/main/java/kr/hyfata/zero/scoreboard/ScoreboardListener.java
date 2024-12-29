package kr.hyfata.zero.scoreboard;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ScoreboardListener implements Listener {
    private final ZeroScoreBoard zeroScoreBoard;
    public ScoreboardListener(ZeroScoreBoard zeroScoreBoard) {
        this.zeroScoreBoard = zeroScoreBoard;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        zeroScoreBoard.createScoreboard(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        zeroScoreBoard.removeScoreboard(player);
    }
}
