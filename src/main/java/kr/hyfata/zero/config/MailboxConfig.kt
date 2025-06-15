package kr.hyfata.zero.config

import kr.hyfata.zero.util.config.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class MailboxConfig : IConfig {
    private val configFilePath = "gui/mailbox.yml"
    private lateinit var configFile: File
    override lateinit var config: FileConfiguration
    private lateinit var plugin: JavaPlugin

    override fun init(plugin: JavaPlugin) {
        this.plugin = plugin
        configFile = File(plugin.dataFolder, configFilePath)
        ConfigUtil.createConfig(configFile, configFilePath)
        ConfigUtil.loadConfig(this, configFile, configFilePath)
    }

    override fun getString(key: String, def: String?): String? {
        return config.getString(key, def)
    }

    override fun getBoolean(key: String, def: Boolean): Boolean {
        return config.getBoolean(key, def)
    }

    override fun saveConfig() {
        try {
            config.save(configFile)
        } catch (_: IOException) {
            plugin.logger.severe(ConfigUtil.getSaveConfigErrorMsg(configFile))
        }
    }

    override fun reloadConfig() {
        ConfigUtil.loadConfig(this, configFile, configFilePath)
    }
}
