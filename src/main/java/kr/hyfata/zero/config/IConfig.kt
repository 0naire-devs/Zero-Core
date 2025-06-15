package kr.hyfata.zero.config

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin

interface IConfig {
    fun init(plugin: JavaPlugin)
    var config: FileConfiguration
    fun getString(key: String, def: String?): String?
    fun getBoolean(key: String, def: Boolean): Boolean
    fun saveConfig()
    fun reloadConfig()
}
