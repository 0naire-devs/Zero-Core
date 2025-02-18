package kr.hyfata.zero.modules.jangsa.listener;

import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.modules.jangsa.ZeroJangsa;
import org.bukkit.Bukkit;
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

public class JangsaCommand implements CommandExecutor, TabExecutor {
    ZeroJangsa zeroJangsa;
    public JangsaCommand(ZeroJangsa zeroJangsa) {
        this.zeroJangsa = zeroJangsa;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) commandSender;
        if (args.length != 0) {
            switch(args[0]) {
                case "reload":
                    ZeroCore.getZeroConfig().getJangsaConfig().reloadConfig();
                    p.sendMessage("Reloaded config");
                    break;
                case "위치설정":
                    if (!p.isOp()) {
                        p.sendMessage("이 명령어를 사용할 권한이 없습니다!");
                        break;
                    }
                    if (args.length == 1 || args[1].isEmpty() || !Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                        p.sendMessage("사용법: /장사 위치설정 [플레이어 이름]");
                    }
                    // TODO: set pos
                    break;
                case "위치제거":
                    if (!p.isOp()) {
                        p.sendMessage("이 명령어를 사용할 권한이 없습니다!");
                        break;
                    }
                    if (args.length == 1 || args[1].isEmpty() || !Bukkit.getOfflinePlayer(args[1]).hasPlayedBefore()) {
                        p.sendMessage("사용법: /장사 위치제거 [플레이어 이름]");
                    }
                    // TODO: remove pos
                    break;
                case "시작":
                    if (args.length == 2 && !args[1].isEmpty()) {
                        if (!p.isOp()) {
                            p.sendMessage("다른 플레이어의 장사를 시작할 권한이 없습니다!");
                        } else {
                            // TODO: start jangsa other player(args[1])
                        }
                    } else {
                        // TODO: start jangsa itself
                    }
                    break;
                case "종료":
                    // TODO: stop jangsa itself
                    break;
                case "아이템설정":
                    if (args.length == 1 || args[1].isEmpty() || !args[1].matches("\\d+")) {
                        p.sendMessage("사용법: /장사 아이템설정 [가격(자연수)] - 손에 들고있는 아이템 콘피그에 등록");
                    }
                    // TODO: set item
                    break;
                default:
                    p.sendMessage("잘못된 명령어 입력!");
            }
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        List<String> defaultCommand = Arrays.asList("시작", "종료");
        List<String> opCommand = Arrays.asList("위치설정", "위치제거", "reload");

        if (commandSender.isOp()) {
            defaultCommand.addAll(opCommand);
        }

        String input = args[args.length - 1].toLowerCase();

        List<String> completions = null;
        for (String s : defaultCommand) {
            if (s.startsWith(input)) {
                if (completions == null) {
                    completions = new ArrayList<>();
                }
                completions.add(s);
            }
        }
        return completions;
    }
}
