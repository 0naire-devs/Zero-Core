package kr.hyfata.zero.helper.format

import kr.hyfata.zero.ZeroCore
import kr.hyfata.zero.util.VaultUtil
import org.bukkit.entity.Player
import java.text.DecimalFormat

object TextFormatHelper {
    fun getFormattedText(player: Player, text: String): String {
        val placeholders = mapOf(
            "formattedBalance" to { numberFont(getFormattedBalance(player) ?: "\uF801\uE02D") },
            "balance" to { getFormattedBalance(player) ?: "0" },
            "playerName" to { player.name },
            "world" to { getFormattedWorld(player.world.name) ?: "" },
            "level" to { player.level.toString() }
        )

        var result = getFormattedText(text) // 기존 getFormattedText(text: String) 함수를 호출하여 기본 서식 적용
        for ((placeholder, valueProvider) in placeholders) {
            result = result.replace("\${$placeholder}", valueProvider())
        }
        return result
    }

    fun numberFont(text: String): String {
        return text.replace("1", "\uF801\uE024")
            .replace("2", "\uF801\uE025")
            .replace("3", "\uF801\uE026")
            .replace("4", "\uF801\uE027")
            .replace("5", "\uF801\uE028")
            .replace("6", "\uF801\uE029")
            .replace("7", "\uF801\uE02A")
            .replace("8", "\uF801\uE02B")
            .replace("9", "\uF801\uE02C")
            .replace("0", "\uF801\uE02D")
            .replace(",", "\uF801\uE02E")
    }

    fun getFormattedText(text: String): String {
        return text.replace("&", "§")
            .replace("\\\n", "")
    }

    fun getFormattedTextList(vararg texts: String?): Array<String?> {
        return texts.map { text -> text?.let { getFormattedText(it) } }.toTypedArray()
    }

    private fun getFormattedBalance(player: Player?): String? {
        val balance = VaultUtil.getBalance(player)
        val df = DecimalFormat("#,###") // 소수점 이하 자리 제거
        return df.format(balance)
    }

    fun getFormattedWorld(world: String): String? {
        return ZeroCore.Companion.zeroConfig.scoreboardConfig.getString("worlds.$world", world)
    }
}