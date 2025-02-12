package kr.hyfata.zero.modules.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.util.TextFormatUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class ZeroScoreBoard {
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private final JavaPlugin plugin;

    public ZeroScoreBoard(JavaPlugin plugin) {
        this.plugin = plugin;
        setListeners();
        createScoreboardAllPlayers();
        getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (FastBoard board : this.boards.values()) {
                updateBoard(board);
            }
        }, 0, 20);
        plugin.getLogger().info("Zero Scoreboard has been enabled.");
    }

    private void setListeners() {
        ScoreboardCommand command = new ScoreboardCommand(this);
        Objects.requireNonNull(plugin.getCommand("zeroscoreboard")).setExecutor(command);
        Objects.requireNonNull(plugin.getCommand("zeroscoreboard")).setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new ScoreboardListener(this), plugin);
    }

    public void onDisable() {
        removeScoreboardAllPlayers();
        plugin.getLogger().info("Zero Scoreboard has been disabled.");
    }

    public void createScoreboardAllPlayers() {
        for (Player player : getServer().getOnlinePlayers()) {
            createScoreboard(player);
        }
    }

    public void createScoreboard(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle(TextFormatUtil.getFormattedText(player,
                ZeroCore.getZeroConfig().getScoreboardConfig().getString("scoreboard.title", "&cERROR")));

        this.boards.put(player.getUniqueId(), board);
    }

    public void removeScoreboardAllPlayers() {
        for (Player player : getServer().getOnlinePlayers()) {
            removeScoreboard(player);
        }
    }

    public void removeScoreboard(Player player) {
        FastBoard board = this.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    private void updateBoard(FastBoard board) {
        board.updateLines(TextFormatUtil.getFormattedText(board.getPlayer(),
                ZeroCore.getZeroConfig().getScoreboardConfig().getString("scoreboard.message", "&cERROR"))
                        .replace("\\\n", "")
                .split("\n"));
    }
}
