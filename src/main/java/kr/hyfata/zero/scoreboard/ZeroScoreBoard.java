package kr.hyfata.zero.scoreboard;

import fr.mrmicky.fastboard.FastBoard;
import kr.hyfata.zero.textutil.UniqueText;
import kr.hyfata.zero.vault.VaultUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class ZeroScoreBoard {
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    UniqueText ut = new UniqueText();

    public ZeroScoreBoard(JavaPlugin plugin) {
        getServer().getPluginManager().registerEvents(new ScoreboardListener(this), plugin);

        getServer().getScheduler().runTaskTimer(plugin, () -> {
            for (FastBoard board : this.boards.values()) {
                updateBoard(board);
            }
        }, 0, 20);
    }

    public void createScoreboard(Player player) {
        FastBoard board = new FastBoard(player);
        board.updateTitle("§eZERO RPG");

        this.boards.put(player.getUniqueId(), board);
    }

    public void removeScoreboard(Player player) {
        FastBoard board = this.boards.remove(player.getUniqueId());

        if (board != null) {
            board.delete();
        }
    }

    private void updateBoard(FastBoard board) {
        String money = Double.toString(VaultUtil.getBalance(board.getPlayer()));
        board.updateLines(
                "",
                "\uE023§f" + ut.makeBoldtext(money),
                "",
                "§b* §7현재 지역: §ftest",
                ""
        );
    }
}
