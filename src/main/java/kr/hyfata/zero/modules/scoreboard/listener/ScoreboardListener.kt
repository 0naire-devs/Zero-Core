package kr.hyfata.zero.modules.scoreboard.listener

import kr.hyfata.zero.ZeroCore
import kr.hyfata.zero.modules.scoreboard.ZeroScoreBoard
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class ScoreboardListener(private val zeroScoreBoard: ZeroScoreBoard) : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ZeroCore.Companion.zeroConfig.scoreboardConfig.getBoolean("scoreboard.enabled", true).let {
            if (!it) {
                return
            }
        }
        val player = event.getPlayer()
        zeroScoreBoard.createScoreboard(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.getPlayer()
        zeroScoreBoard.removeScoreboard(player)
    }
}