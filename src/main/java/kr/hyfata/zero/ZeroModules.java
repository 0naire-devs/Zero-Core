package kr.hyfata.zero;

import kr.hyfata.zero.modules.gui.mailbox.ZeroMailbox;
import kr.hyfata.zero.modules.scoreboard.ZeroScoreBoard;
import org.bukkit.plugin.java.JavaPlugin;

public class ZeroModules {
    ZeroScoreBoard zeroScoreBoard;
    ZeroMailbox zeroMailbox;

    public ZeroModules(JavaPlugin plugin) {
        zeroScoreBoard = new ZeroScoreBoard(plugin);
        zeroMailbox = new ZeroMailbox(plugin);
    }

    public void onDisable() {
        zeroScoreBoard.onDisable();
        zeroMailbox.onDisable();
    }

    public ZeroScoreBoard getZeroScoreBoard() {
        return zeroScoreBoard;
    }

    public ZeroMailbox getZeroMailboxUI() {
        return zeroMailbox;
    }
}
