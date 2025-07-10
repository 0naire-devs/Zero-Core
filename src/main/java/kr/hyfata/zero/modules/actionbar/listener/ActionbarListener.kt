package kr.hyfata.zero.modules.actionbar.listener

import org.bukkit.attribute.Attribute
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class ActionbarListener : Listener {
    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.getPlayer()
        val maxHp = player.getAttribute(Attribute.MAX_HEALTH)!!.value
        val currentHp = player.health
        val hunger = player.foodLevel
    }
}