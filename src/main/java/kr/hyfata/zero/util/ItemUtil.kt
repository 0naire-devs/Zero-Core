package kr.hyfata.zero.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.Material
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*

object ItemUtil {
    @Throws(IllegalStateException::class)
    fun itemStackToBase64(item: ItemStack?): ByteArray? {
        val yaml = YamlConfiguration()
        yaml.set("i", item)
        val yamlStr = yaml.saveToString()
        return Base64.getEncoder().encode(yamlStr.toByteArray())
    }

    @Throws(InvalidConfigurationException::class)
    fun base64ToItemStack(base64: ByteArray?): ItemStack? {
        val decoded = String(Base64.getDecoder().decode(base64))
        val yaml = YamlConfiguration()
        yaml.loadFromString(decoded)
        return yaml.getItemStack("i")
    }

    fun addLore(item: ItemStack, loreToAdd: MutableList<Component?>): ItemStack {
        val meta = item.itemMeta
        var lore = meta.lore()

        if (lore == null) {
            lore = ArrayList()
        }

        lore.addAll(loreToAdd)
        meta.lore(lore)
        item.setItemMeta(meta)
        return item
    }

    private fun newItemStack(
        material: Material?,
        amount: Int,
        customModelData: Int,
        name: Component?,
        vararg lore: Component?
    ): ItemStack? {
        if (material == null) {
            System.err.println(("Material cannot be null"))
            return null
        }
        val item = ItemStack(material, amount, 0.toShort())
        val meta = checkNotNull(item.itemMeta)

        meta.addItemFlags(*ItemFlag.entries.toTypedArray())
        meta.displayName(name)
        if (lore.size != 1 || !PlainTextComponentSerializer.plainText().serialize(lore[0]!!)
                .isEmpty()
        ) {
            meta.lore(listOf(*lore))
        } else {
            meta.lore(null)
        }
        meta.setCustomModelData(customModelData)

        item.setItemMeta(meta)
        return item
    }

    fun newItemStack(
        material: Material?,
        amount: Int,
        customModelData: Int,
        name: String?,
        vararg lore: String?
    ): ItemStack? {
        val convertedName: TextComponent? = name?.let { LegacyComponentSerializer.legacyAmpersand().deserialize(it) }

        val loreComponent: MutableList<Component?> = ArrayList<Component?>()
        for (s in lore) {
            s?.let { loreComponent.add(LegacyComponentSerializer.legacyAmpersand().deserialize(it)) }
        }
        val convertedLore = loreComponent.toTypedArray<Component?>()

        return newItemStack(material, amount, customModelData, convertedName, *convertedLore)
    }
}
