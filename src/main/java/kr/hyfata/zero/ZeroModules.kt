package kr.hyfata.zero

import kr.hyfata.zero.modules.mailbox.ZeroMailbox
import kr.hyfata.zero.modules.scoreboard.ZeroScoreBoard
import org.bukkit.plugin.java.JavaPlugin

class ZeroModules(plugin: JavaPlugin) {
    var zeroScoreBoard: ZeroScoreBoard = ZeroScoreBoard(plugin)
    var zeroMailbox: ZeroMailbox = ZeroMailbox(plugin)

    fun onDisable() {
        zeroScoreBoard.onDisable()
        zeroMailbox.onDisable()
    }
}
