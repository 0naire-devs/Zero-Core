package kr.hyfata.zero.util

import kr.hyfata.zero.ZeroCore
import org.bukkit.entity.Player
import java.text.DecimalFormat

object TextFormatUtil {
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

    fun getFormattedText(player: Player, text: String): String {
        return getFormattedText(text)
            .replace("\${formattedBalance}", numberFont(getFormattedBalance(player)!!))
            .replace("\${balance}", getFormattedBalance(player)!!)
            .replace("\${playerName}", player.name)
            .replace("\${world}", getFormattedWorld(player.world.name)!!)
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