package kr.hyfata.zero.modules.scoreboard

import kr.hyfata.zero.ZeroCore
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.util.*

class ScoreboardCommand(var zeroScoreBoard: ZeroScoreBoard) : CommandExecutor, TabExecutor {
    override fun onCommand(commandSender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        val p = commandSender as Player
        if (args.isNotEmpty()) {
            when (args[0]) {
                "reload" -> {
                    ZeroCore.Companion.zeroConfig.scoreboardConfig.reloadConfig()
                    zeroScoreBoard.removeScoreboardAllPlayers()
                    zeroScoreBoard.createScoreboardAllPlayers()
                    p.sendMessage("Reloaded config")
                }

                "on" -> {
                    zeroScoreBoard.removeScoreboard(p)
                    zeroScoreBoard.createScoreboard(p)
                    p.sendMessage("§a스코어보드가 켜졌습니다!")
                }

                "off" -> {
                    zeroScoreBoard.removeScoreboard(p)
                    p.sendMessage("§c스코어보드가 꺼졌습니다!")
                }

                else -> p.sendMessage("잘못된 명령어 입력!")
            }
        }
        return true
    }

    override fun onTabComplete(
        commandSender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): MutableList<String> {
        val list = mutableListOf("reload", "on", "off")
        val input = args[0].lowercase(Locale.getDefault())

        val completions: MutableList<String> = list.filter { it.startsWith(input) }.toMutableList()

        completions.sort()
        return completions
    }
}
