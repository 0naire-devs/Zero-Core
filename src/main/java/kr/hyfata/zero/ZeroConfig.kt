package kr.hyfata.zero

import kr.hyfata.zero.config.AbstractConfig
import kr.hyfata.zero.modules.mailbox.MailboxConfig
import kr.hyfata.zero.modules.scoreboard.ScoreboardConfig
import org.bukkit.plugin.java.JavaPlugin

class ZeroConfig(plugin: JavaPlugin) {
    var scoreboardConfig: AbstractConfig = ScoreboardConfig()
    var mailboxConfig: AbstractConfig = MailboxConfig()

    init {
        scoreboardConfig.init(plugin)
        mailboxConfig.init(plugin)
    }

    fun save() {
        scoreboardConfig.saveConfig()
        mailboxConfig.saveConfig()
    }
}
