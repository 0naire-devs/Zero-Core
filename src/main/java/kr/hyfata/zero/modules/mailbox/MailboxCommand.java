package kr.hyfata.zero.modules.mailbox;

import kr.hyfata.zero.ZeroCore;
import kr.hyfata.zero.modules.mailbox.util.MailboxUtil;
import kr.hyfata.zero.util.TextFormatUtil;
import kr.hyfata.zero.util.TimeUtil;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MailboxCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        Player p = (Player) sender;
        if (sender.hasPermission("zeromailbox.advenced") && args.length != 0) {
            switch(args[0]) {
                case "reload": {
                    ZeroCore.configModules.getMailboxConfig().reloadConfig();
                    p.sendMessage("Reloaded config");
                    break;
                }
                case "발송": {
                    OfflinePlayer target = p.getServer().getOfflinePlayer(args[1]);
                    if (!target.hasPlayedBefore()) {
                        sender.sendMessage(TextFormatUtil.getFormattedText("&c플레이어를 찾을 수 없습니다!"));
                    } else {
                        CompletableFuture.runAsync(() -> {
                            try {
                                MailboxUtil.sendMailToPlayer(p, target, args[2], args[3]);
                            } catch (ParseException e) {
                                sender.sendMessage(TextFormatUtil.getFormattedText("&c만료날짜 파싱에 실패했습니다! 만료날짜를 다시 확인해주세요!"));
                            }
                        });
                    }
                    break;
                }
                case "전체발송": {
                    CompletableFuture.runAsync(() -> {
                        try {
                            MailboxUtil.sendMailToAll(p, args[1], args[2]);
                        } catch (ParseException e) {
                            sender.sendMessage(TextFormatUtil.getFormattedText("&c만료날짜 파싱에 실패했습니다! 만료날짜를 다시 확인해주세요!"));
                        }
                    });
                    break;
                }
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
        String currentDateTime = TimeUtil.getCurrentDateTimeString();
        String currentDate = currentDateTime.split(" ")[0];
        String currentTime = currentDateTime.split(" ")[1];

        switch (args.length) {
            case 1: {
                list = Arrays.asList("전체발송", "발송", "reload");
                break;
            }
            case 2: {
                if (args[0].equals("전체발송")) {
                    list = Arrays.asList("만료_날짜입력:", currentDate);
                }
                break;
            }
            case 3: {
                if (args[0].equals("전체발송")) {
                    list = Arrays.asList("만료_시간입력(24시간제):", currentTime);
                } else if (args[0].equals("발송")) {
                    list = Arrays.asList("만료_날짜입력:", currentDate);
                }
                break;
            }
            case 4: {
                if (args[0].equals("발송")) {
                    list = Arrays.asList("만료_시간입력(24시간제):", currentTime);
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
        return completions;
    }
}
