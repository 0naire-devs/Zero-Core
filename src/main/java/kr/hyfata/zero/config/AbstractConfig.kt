package kr.hyfata.zero.config

import kr.hyfata.zero.util.config.ConfigUtil
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

abstract class AbstractConfig(private val configFilePath: String) {
    private lateinit var configFile: File
    lateinit var config: FileConfiguration
        private set
    private lateinit var plugin: JavaPlugin

    open fun init(plugin: JavaPlugin) {
        this.plugin = plugin
        configFile = File(plugin.dataFolder, configFilePath)
        ConfigUtil.createConfig(plugin, configFile, configFilePath)
        ConfigUtil.loadConfig(this, configFile)

        // set default value
        try {
            val defConfigStream = plugin.getResource(configFilePath) // jar resource
            if (defConfigStream != null) {
                val defConfig =
                    YamlConfiguration.loadConfiguration(InputStreamReader(defConfigStream, StandardCharsets.UTF_8))
                config.setDefaults(defConfig)
            }
        } catch (e: Exception) {
            plugin.logger.severe("Could not load jar config: " + e.message)
        }

        plugin.logger.info("Loaded config: $configFilePath")
    }

    fun setConfig(config: FileConfiguration) {
        this.config = config
    }

    fun getString(key: String, def: String? = null): String? {
        return config.getString(key, def)
    }

    fun getBoolean(key: String, def: Boolean = false): Boolean {
        return config.getBoolean(key, def)
    }

    fun getLong(key: String, def: Long = 0L): Long {
        return config.getLong(key, def)
    }

    fun getDouble(key: String, def: Double = 0.0): Double {
        return config.getDouble(key, def)
    }

    fun getInteger(key: String, def: Int = 0): Int {
        return config.getInt(key, def)
    }

    fun saveConfig() {
        try {
            config.save(configFile)
        } catch (_: IOException) {
            plugin.logger.severe(ConfigUtil.getSaveConfigErrorMsg(configFile))
        }
    }

    fun reloadConfig() {
        ConfigUtil.loadConfig(this, configFile)
    }
}