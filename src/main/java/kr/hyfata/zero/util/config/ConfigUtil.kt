package kr.hyfata.zero.util.config

import kr.hyfata.zero.config.AbstractConfig
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

object ConfigUtil {
    fun createConfig(plugin: JavaPlugin, configFile: File, resourcePath: String) {
        if (!configFile.exists()) {
            plugin.saveResource(resourcePath, false) // copy jar resources config
        }
    }

        fun loadConfig(abstractConfig: AbstractConfig, configFile: File) {
        abstractConfig.setConfig(YamlConfiguration.loadConfiguration(configFile)) // load data folder config file
    }

    fun getSaveConfigErrorMsg(configFile: File?): String {
        return "Could not save config to $configFile"
    }
}