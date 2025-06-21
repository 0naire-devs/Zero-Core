package kr.hyfata.zero.helper.stats.player

import org.bukkit.entity.Player

object PlayerStatsManager {
    private val stats = HashMap<Player, PlayerStats>()

    fun load(p: Player) {
        val uuid = p.uniqueId.toString()
    }
}