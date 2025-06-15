package kr.hyfata.zero.modules.mailbox.dto

import org.bukkit.Material

class MailboxButton {
    private lateinit var lore: Array<String?>

    var positions: MutableList<Int?>? = null
    var name: String? = null
    var item: Material? = null
    var customModelData: Int = 0

    fun getLore(page: Int): Array<String?> {
        val result = arrayOfNulls<String>(lore.size)
        for (i in lore.indices) {
            result[i] = lore[i]!!.replace("\${page}", page.toString())
        }
        return result
    }

    fun setLore(lore: Array<String?>) {
        this.lore = lore
    }
}
