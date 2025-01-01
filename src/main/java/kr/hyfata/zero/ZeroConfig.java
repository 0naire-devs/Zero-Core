package kr.hyfata.zero;

import kr.hyfata.zero.config.IConfig;
import kr.hyfata.zero.config.ScoreboardConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class ZeroConfig {
    IConfig scoreboardConfig = new ScoreboardConfig();

    public void init(JavaPlugin plugin) {
        scoreboardConfig.init(plugin);
    }

    public void save() {
        scoreboardConfig.saveConfig();
    }

    public IConfig getScoreboardConfig() {
        return scoreboardConfig;
    }
}
