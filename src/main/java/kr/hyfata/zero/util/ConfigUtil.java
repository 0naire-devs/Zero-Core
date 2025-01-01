package kr.hyfata.zero.util;

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

    public static void loadConfig(IConfig iConfig, File configFile) {
        if (!configFile.exists()) {
            createConfig(configFile);
        }

        iConfig.setConfig(YamlConfiguration.loadConfiguration(configFile));
        try {
            InputStream defConfigStream = plugin.getResource(configFile.getName());
            if (defConfigStream != null) {
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream, StandardCharsets.UTF_8));
                iConfig.getConfig().setDefaults(defConfig);
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Could not load config: " + e.getMessage());
        }
    }

    public static void createConfig(File configFile) {
        configFile.getParentFile().mkdirs();
        plugin.saveResource(configFile.getName(), false); // copy resources config
    }
}
