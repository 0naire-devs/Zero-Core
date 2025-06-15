package kr.hyfata.zero;

import kr.hyfata.zero.config.IConfig;
import kr.hyfata.zero.config.JangsaConfig;
import kr.hyfata.zero.config.MailboxConfig;
import kr.hyfata.zero.config.ScoreboardConfig;
import org.bukkit.plugin.java.JavaPlugin;

public class ZeroConfig {
    IConfig scoreboardConfig = new ScoreboardConfig();
    IConfig mailboxConfig = new MailboxConfig();
    IConfig jangsaConfig = new JangsaConfig();

    public ZeroConfig(JavaPlugin plugin) {
        scoreboardConfig.init(plugin);
        mailboxConfig.init(plugin);
        jangsaConfig.init(plugin);
    }

    public void save() {
        scoreboardConfig.saveConfig();
        mailboxConfig.saveConfig();
        jangsaConfig.saveConfig();
    }

    public IConfig getScoreboardConfig() {
        return scoreboardConfig;
    }

    public IConfig getMailboxConfig() {
        return mailboxConfig;
    }

    public IConfig getJangsaConfig() {
        return jangsaConfig;
    }
}
