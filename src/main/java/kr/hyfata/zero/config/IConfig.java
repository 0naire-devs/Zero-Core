package kr.hyfata.zero.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public interface IConfig {
    void init(JavaPlugin plugin);
    FileConfiguration getConfig();
    String getString(String key, String def);
    boolean getBoolean(String key, Boolean def);
    void setConfig(FileConfiguration config);
    void saveConfig();
    void reloadConfig();
}
