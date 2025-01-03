package kr.hyfata.zero;

import kr.hyfata.zero.modules.gui.mailbox.ZeroMailboxUI;
import kr.hyfata.zero.modules.scoreboard.ZeroScoreBoard;
import org.bukkit.plugin.java.JavaPlugin;

public class ZeroModules {
    ZeroScoreBoard zeroScoreBoard;
    ZeroMailboxUI zeroMailboxUI;

    public ZeroModules(JavaPlugin plugin) {
        zeroScoreBoard = new ZeroScoreBoard(plugin);
        zeroMailboxUI = new ZeroMailboxUI(plugin);
    }

    public void onDisable() {
        zeroScoreBoard.onDisable();
        zeroMailboxUI.onDisable();
    }

    public ZeroScoreBoard getZeroScoreBoard() {
        return zeroScoreBoard;
    }

    public ZeroMailboxUI getZeroMailboxUI() {
        return zeroMailboxUI;
    }
}
