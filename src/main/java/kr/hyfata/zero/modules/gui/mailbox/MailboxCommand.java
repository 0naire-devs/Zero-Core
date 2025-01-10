package kr.hyfata.zero.modules.gui.mailbox;

import kr.hyfata.zero.ZeroCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MailboxCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (sender.hasPermission("zeromailbox.advenced") && args.length != 0) {
            switch(args[0]) {
                case "reload":
                    if (p.hasPermission("zeromailbox.advenced")) {
                        ZeroCore.configModules.getMailboxConfig().reloadConfig();
                        p.sendMessage("Reloaded config");
                    } else {
                        return false;
                    }
                    break;
                default:
                    p.sendMessage("잘못된 명령어 입력!");
            }
        } else {
            ZeroCore.modules.getZeroMailboxUI().openInventory(p);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("zeromailbox.advenced")) {
            return null;
        }
        List<String> list = null;
        switch (args.length) {
            case 1: {
                list = Arrays.asList("전체발송", "발송", "reload");
                break;
            }
            case 2: {
                if (args[0].equals("전체발송")) {
                    list = Arrays.asList("만료_날짜입력:", "2025-01-11");
                }
                break;
            }
            case 3: {
                if (args[0].equals("전체발송")) {
                    list = Arrays.asList("만료_시간입력(24시간제):", "13:00:30");
                } else if (args[0].equals("발송")) {
                    list = Arrays.asList("만료_날짜입력:", "2025-01-11");
                }
                break;
            }
            case 4: {
                if (args[0].equals("발송")) {
                    list = Arrays.asList("만료_시간입력(24시간제):", "13:00:30");
                }
            }
        }
        if (list == null)
            return null;
        String input = args[args.length - 1].toLowerCase();

        List<String> completions = null;
        for (String s : list) {
            if (s.startsWith(input)) {
                if (completions == null) {
                    completions = new ArrayList<>();
                }
                completions.add(s);
            }
        }

//        if (completions != null) {
//            Collections.sort(completions);
//        }
        return completions;
    }
}
