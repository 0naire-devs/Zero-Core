package kr.hyfata.zero;

import kr.hyfata.zero.modules.scoreboard.ZeroScoreBoard;
import org.bukkit.plugin.java.JavaPlugin;

public class ZeroModules {
    ZeroScoreBoard zeroScoreBoard;

    public void init(JavaPlugin plugin) {
        zeroScoreBoard = new ZeroScoreBoard(plugin);
    }

    public void onDisable() {
        zeroScoreBoard.onDisable();
    }

    public ZeroScoreBoard getZeroScoreBoard() {
        return zeroScoreBoard;
    }
}
