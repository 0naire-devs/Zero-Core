package kr.hyfata.zero

import kr.hyfata.zero.config.IConfig
import kr.hyfata.zero.config.MailboxConfig
import kr.hyfata.zero.config.ScoreboardConfig
import org.bukkit.plugin.java.JavaPlugin

class ZeroConfig(plugin: JavaPlugin) {
    var scoreboardConfig: IConfig = ScoreboardConfig()
    var mailboxConfig: IConfig = MailboxConfig()

    init {
        scoreboardConfig.init(plugin)
        mailboxConfig.init(plugin)
    }

    fun save() {
        scoreboardConfig.saveConfig()
        mailboxConfig.saveConfig()
    }
}
