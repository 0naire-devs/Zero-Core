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

        for (Player player : getServer().getOnlinePlayers()) {
            createScoreboard(player);
        }

        getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (FastBoard board : this.boards.values()) {
                updateBoard(board);
            }
        }, 0, 20);
        plugin.getLogger().info("Zero Scoreboard has been enabled.");
    }

    private void setListeners() {
        Objects.requireNonNull(plugin.getCommand("zeroscoreboard")).setExecutor(new ScoreboardCommand());
        Objects.requireNonNull(plugin.getCommand("zeroscoreboard")).setTabCompleter(new ScoreboardCommand());
        getServer().getPluginManager().registerEvents(new ScoreboardListener(this), plugin);
    }

    public void onDisable() {
        for (Player player : getServer().getOnlinePlayers()) {
            removeScoreboard(player);
        }
        plugin.getLogger().info("Zero Scoreboard has been disabled.");
    }

    public void createScoreboard(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle(TextFormatUtil.getFormattedScoreText(player,
                ZeroCore.configModules.getScoreboardConfig().getString("scoreboard.title", "&cERROR")));

        this.boards.put(player.getUniqueId(), board);
    }

    public void removeScoreboard(Player player) {
        FastBoard board = this.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    private void updateBoard(FastBoard board) {
        board.updateLines(TextFormatUtil.getFormattedScoreText(board.getPlayer(),
                ZeroCore.configModules.getScoreboardConfig().getString("scoreboard.message", "&cERROR"))
                .split("\n"));
    }
}
