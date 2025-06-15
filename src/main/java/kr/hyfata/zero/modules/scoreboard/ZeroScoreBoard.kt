package kr.hyfata.zero.modules.scoreboard

import fr.mrmicky.fastboard.FastBoard
import kr.hyfata.zero.ZeroCore
import kr.hyfata.zero.util.TextFormatUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class ZeroScoreBoard(private val plugin: JavaPlugin) {
    private val boards: MutableMap<UUID?, FastBoard> = HashMap<UUID?, FastBoard>()

    init {
        setListeners()
        createScoreboardAllPlayers()
        Bukkit.getServer().scheduler.runTaskTimer(plugin, Runnable {
            for (board in this.boards.values) {
                updateBoard(board)
            }
        }, 0, 20)
        plugin.logger.info("Zero Scoreboard has been enabled.")
    }

    private fun setListeners() {
        val command = ScoreboardCommand(this)
        plugin.getCommand("zeroscoreboard")?.setExecutor(command)
        plugin.getCommand("zeroscoreboard")?.tabCompleter = command
        Bukkit.getServer().pluginManager.registerEvents(ScoreboardListener(this), plugin)
    }

    fun onDisable() {
        removeScoreboardAllPlayers()
        plugin.logger.info("Zero Scoreboard has been disabled.")
    }

    fun createScoreboardAllPlayers() {
        for (player in Bukkit.getServer().onlinePlayers) {
            createScoreboard(player)
        }
    }

    fun createScoreboard(player: Player) {
        val board = FastBoard(player)
        board.updateTitle(
            TextFormatUtil.getFormattedText(
                player,
                ZeroCore.Companion.zeroConfig.scoreboardConfig.getString("scoreboard.title", "&cERROR")!!
            )
        )

        this.boards.put(player.uniqueId, board)
    }

    fun removeScoreboardAllPlayers() {
        for (player in Bukkit.getServer().onlinePlayers) {
            removeScoreboard(player)
        }
    }

    fun removeScoreboard(player: Player) {
        val board = this.boards.remove(player.uniqueId)
        board?.delete()
    }

    private fun updateBoard(board: FastBoard) {
        board.updateLines(
            *TextFormatUtil.getFormattedText(
                board.player,
                ZeroCore.Companion.zeroConfig.scoreboardConfig.getString("scoreboard.message", "&cERROR")!!
            )
                .replace("\\\n", "")
                .split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        )
    }
}
