package kr.hyfata.zero.modules.actionbar

import org.bukkit.plugin.java.JavaPlugin

class ZeroActionbar(var plugin: JavaPlugin) {
    fun onDisable() {
        plugin.logger.info("Zero Actionbar has been disabled.")
    }
}