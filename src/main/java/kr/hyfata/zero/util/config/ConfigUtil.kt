package kr.hyfata.zero.util.config;

import kr.hyfata.zero.config.IConfig;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigUtil {
    private static JavaPlugin plugin;

    public static void init(JavaPlugin plugin) {
        ConfigUtil.plugin = plugin;
    }

    public static void createConfig(File configFile, String resourcePath) {
        if (!configFile.exists()) {
            plugin.saveResource(resourcePath, false); // copy jar resources config
        }
    }

    public static void loadConfig(IConfig iConfig, File configFile, String resourcePath) {
        iConfig.setConfig(YamlConfiguration.loadConfiguration(configFile)); // load data folder config file

        // set default value
        try {
            InputStream defConfigStream = plugin.getResource(resourcePath); // jar resource
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));
                plugin.getConfig().setDefaults(defConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load jar config: " + e.getMessage());
        }

        plugin.getLogger().info("Loaded config: " + resourcePath);
    }

    public static String getSaveConfigErrorMsg(File configFile) {
        return "Could not save config to " + configFile;
    }
}
