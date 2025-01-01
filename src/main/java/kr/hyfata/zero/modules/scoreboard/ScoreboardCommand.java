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
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        Player p = (Player) commandSender;
        if (args.length != 0) {
            switch(args[0]) {
                case "reload":
                    ZeroCore.configModules.getScoreboardConfig().reloadConfig();
                    ZeroCore.modules.getZeroScoreBoard().removeScoreboard(p);
                    ZeroCore.modules.getZeroScoreBoard().createScoreboard(p);
                    p.sendMessage("Reloaded config");
                    break;
                case "on":
                    ZeroCore.modules.getZeroScoreBoard().removeScoreboard(p);
                    ZeroCore.modules.getZeroScoreBoard().createScoreboard(p);
                    p.sendMessage("Scoreboard is on");
                    break;
                case "off":
                    ZeroCore.modules.getZeroScoreBoard().removeScoreboard(p);
                    p.sendMessage("Scoreboard is off");
                    break;
                default:
                    p.sendMessage("Fuck you");
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
