package kr.hyfata.zero.util.config

import kr.hyfata.zero.ZeroCore
import kr.hyfata.zero.config.AbstractConfig
import kr.hyfata.zero.modules.mailbox.dto.MailboxButton
import kr.hyfata.zero.helper.format.TextFormatHelper
import org.bukkit.Material

object MailboxConfigUtil {
        val config: AbstractConfig
        get() = ZeroCore.Companion.zeroConfig.mailboxConfig

    val mailBoxTitle: String?
        get() = config.getString("mailbox.title", "ERROR")

    val rows: Int
        get() = config.config.getInt("mailbox.rows", 6)

    val rewardItemRowRange: IntArray
        get() {
            val result = IntArray(2)
            config.config.let { result[0] = it.getInt("mailbox.reward_item_row_start", 1) }
            config.config.let { result[1] = it.getInt("mailbox.reward_item_row_end", 6) }
            return result
        }

    val errorMaterial: Material?
        get() = config.getString("task.error.item", "RED_CONCRETE")?.let { Material.getMaterial(it) }

    val errorCustomModelData: Int
        get() = config.config.getInt("task.error.custom_model_data", 0)

    val successMaterial: Material?
        get() = config.getString(
            "task.success.item",
            "GREEN_CONCRETE"
        )?.let {
            Material.getMaterial(
                it
            )
        }

    val successCustomModelData: Int
        get() = config.config.getInt("task.success.custom_model_data", 0)

    private fun getButton(path: String?): MailboxButton {
        val result = MailboxButton()
            

        result.positions = config.config.getIntegerList("$path.pos")
        result.name = config.getString("$path.txt", "ERROR")?.let { TextFormatHelper.getFormattedText(it) }
        config.getString("$path.lore", "")?.let { TextFormatHelper.getFormattedText(it) }?.split("\n".toRegex())
            ?.dropLastWhile { it.isEmpty() }?.let {
                result.setLore(
                    it.toTypedArray()
                )
            }
        result.item = config.getString("$path.item", "STONE")?.let { Material.getMaterial(it) }
        config.config.let { result.customModelData = it.getInt("$path.custom_model_data", 0) }
        return result
    }

    val allRewardsButton: MailboxButton
        get() = getButton("buttons.get_all_rewards")

    val previousButton: MailboxButton
        get() = getButton("buttons.previous")

    val nextButton: MailboxButton
        get() = getButton("buttons.next")
}
