package kr.hyfata.zero.config;

import kr.hyfata.zero.util.config.ConfigUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class MailboxConfig implements IConfig {
    private final String configFilePath = "gui/mailbox.yml";
    private File configFile;
    private FileConfiguration config;
    private JavaPlugin plugin;

    @Override
    public void init(JavaPlugin plugin) {
        this.plugin = plugin;
        configFile = new File(plugin.getDataFolder(), configFilePath);
        ConfigUtil.createConfig(configFile, configFilePath);
        ConfigUtil.loadConfig(this, configFile, configFilePath);
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
            plugin.getLogger().severe(ConfigUtil.getSaveConfigErrorMsg(configFile));
        }
    }

    @Override
    public void reloadConfig() {
        ConfigUtil.loadConfig(this, configFile, configFilePath);
    }
}
