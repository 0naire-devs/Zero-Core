package kr.hyfata.zero.util.config

import kr.hyfata.zero.config.IConfig
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object ConfigUtil {
    private lateinit var plugin: JavaPlugin

    fun init(plugin: JavaPlugin) {
        ConfigUtil.plugin = plugin
    }

    fun createConfig(configFile: File, resourcePath: String) {
        if (!configFile.exists()) {
            plugin.saveResource(resourcePath, false) // copy jar resources config
        }
    }

    fun loadConfig(iConfig: IConfig, configFile: File, resourcePath: String) {
        iConfig.config = YamlConfiguration.loadConfiguration(configFile) // load data folder config file

        // set default value
        try {
            val defConfigStream = plugin.getResource(resourcePath) // jar resource
            if (defConfigStream != null) {
                val defConfig =
                    YamlConfiguration.loadConfiguration(InputStreamReader(defConfigStream, StandardCharsets.UTF_8))
                plugin.getConfig().setDefaults(defConfig)
            }
        } catch (e: Exception) {
            plugin.logger.severe("Could not load jar config: " + e.message)
        }

        plugin.logger.info("Loaded config: $resourcePath")
    }

    fun getSaveConfigErrorMsg(configFile: File?): String {
        return "Could not save config to $configFile"
    }
}
