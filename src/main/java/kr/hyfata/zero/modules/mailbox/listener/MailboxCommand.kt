package kr.hyfata.zero.modules.mailbox.listener

import kr.hyfata.zero.ZeroCore
import kr.hyfata.zero.modules.mailbox.ZeroMailbox
import kr.hyfata.zero.util.TextFormatUtil
import kr.hyfata.zero.util.TimeUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import java.text.ParseException
import java.util.*
import java.util.concurrent.CompletableFuture

class MailboxCommand(var mailbox: ZeroMailbox) : CommandExecutor, TabExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val p = sender as Player
        if (sender.hasPermission("zeromailbox.advenced") && args.isNotEmpty()) {
            when (args[0]) {
                "reload" -> {
                    ZeroCore.Companion.zeroConfig.mailboxConfig.reloadConfig()
                    p.sendMessage("Reloaded config")
                }

                "발송" -> {
                    val target = p.server.getOfflinePlayer(args[1])
                    if (!target.hasPlayedBefore()) {
                        sender.sendMessage(TextFormatUtil.getFormattedText("&c플레이어를 찾을 수 없습니다!"))
                    } else {
                        CompletableFuture.runAsync {
                            try {
                                mailbox.handler.sendMailToPlayer(p, target, args[2], args[3])
                            } catch (_: ParseException) {
                                sender.sendMessage(TextFormatUtil.getFormattedText("&c만료날짜 파싱에 실패했습니다! 만료날짜를 다시 확인해주세요!"))
                            }
                        }
                    }
                }

                "전체발송" -> {
                    CompletableFuture.runAsync {
                        try {
                            mailbox.handler.sendMailToAll(p, args[1], args[2])
                        } catch (_: ParseException) {
                            sender.sendMessage(TextFormatUtil.getFormattedText("&c만료날짜 파싱에 실패했습니다! 만료날짜를 다시 확인해주세요!"))
                        }
                    }
                }

                else -> p.sendMessage("잘못된 명령어 입력!")
            }
        } else {
            mailbox.inventoryHandler.openInventory(p)
        }
        return true
    }


    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<String>
    ): MutableList<String?>? {
        if (!sender.hasPermission("zeromailbox.advenced")) {
            return null
        }
        var list: MutableList<String>? = null
        val currentDateTime = TimeUtil.currentDateTimeString
        val currentDate: String = currentDateTime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
        val currentTime: String = currentDateTime.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

        when (args.size) {
            1 -> {
                list = mutableListOf("전체발송", "발송", "reload")
            }

            2 -> {
                if (args[0] == "전체발송") {
                    list = mutableListOf("만료_날짜입력:", currentDate)
                }
            }

            3 -> {
                if (args[0] == "전체발송") {
                    list = mutableListOf("만료_시간입력(24시간제):", currentTime)
                } else if (args[0] == "발송") {
                    list = mutableListOf("만료_날짜입력:", currentDate)
                }
            }

            4 -> {
                if (args[0] == "발송") {
                    list = mutableListOf("만료_시간입력(24시간제):", currentTime)
                }
            }
        }
        if (list == null) return null
        val input = args[args.size - 1].lowercase(Locale.getDefault())

        var completions: MutableList<String?>? = null
        for (s in list) {
            if (s.startsWith(input)) {
                if (completions == null) {
                    completions = ArrayList<String?>()
                }
                completions.add(s)
            }
        }
        return completions
    }
}
