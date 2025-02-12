package kr.hyfata.zero.modules.scoreboard;

import kr.hyfata.zero.ZeroCore;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScoreboardCommand implements CommandExecutor, TabExecutor {
    ZeroScoreBoard zeroScoreBoard;
    public ScoreboardCommand(ZeroScoreBoard zeroScoreBoard) {
        this.zeroScoreBoard = zeroScoreBoard;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) commandSender;
        if (args.length != 0) {
            switch(args[0]) {
                case "reload":
                    ZeroCore.getZeroConfig().getScoreboardConfig().reloadConfig();
                    zeroScoreBoard.removeScoreboardAllPlayers();
                    zeroScoreBoard.createScoreboardAllPlayers();
                    p.sendMessage("Reloaded config");
                    break;
                case "on":
                    zeroScoreBoard.removeScoreboard(p);
                    zeroScoreBoard.createScoreboard(p);
                    p.sendMessage("§a스코어보드가 켜졌습니다!");
                    break;
                case "off":
                    zeroScoreBoard.removeScoreboard(p);
                    p.sendMessage("§c스코어보드가 꺼졌습니다!");
                    break;
                default:
                    p.sendMessage("잘못된 명령어 입력!");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = Arrays.asList("reload", "on", "off");
        String input = args[0].toLowerCase();

        List<String> completions = null;
        for (String s : list) {
            if (s.startsWith(input)) {
                if (completions == null) {
                    completions = new ArrayList<>();
                }
                completions.add(s);
            }
        }

        if (completions != null) {
            Collections.sort(completions);
        }
        return completions;
    }
}
