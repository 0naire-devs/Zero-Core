package kr.hyfata.zero.config;

import kr.hyfata.zero.util.ConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class ScoreboardConfig implements IConfig {
    JavaPlugin plugin;
    FileConfiguration config;
    File configFile;

    @Override
    public void init(JavaPlugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), "scoreboard.yml");
        ConfigUtil.loadConfig(this, configFile);
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }

    @Override
    public String getString(String key, String def) {
        return config.getString(key, def);
    }

    @Override
    public boolean getBoolean(String key, Boolean def) {
        return config.getBoolean(key, def);
    }

    @Override
    public void setConfig(FileConfiguration config) {
        this.config = config;
    }

    @Override
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save config to " + configFile);
        }
    }

    @Override
    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }
}
