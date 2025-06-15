package kr.hyfata.zero.modules.mailbox

import kr.hyfata.zero.modules.mailbox.handler.MailboxHandler
import kr.hyfata.zero.modules.mailbox.handler.MailboxInventoryHandler
import kr.hyfata.zero.modules.mailbox.listener.MailboxCommand
import kr.hyfata.zero.modules.mailbox.listener.MailboxEventListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class ZeroMailbox(var plugin: JavaPlugin) {
    var inventoryHandler: MailboxInventoryHandler = MailboxInventoryHandler(plugin)
    var handler: MailboxHandler = MailboxHandler()

    init {
        registerListeners()
        plugin.logger.info("Zero Mailbox has been enabled.")
    }

    private fun registerListeners() {
        val command = MailboxCommand(this)
        plugin.getCommand("우편함")?.setExecutor(command)
        plugin.getCommand("우편함")?.tabCompleter = command
        Bukkit.getServer().pluginManager.registerEvents(MailboxEventListener(this), plugin)
    }

    fun onDisable() {
        inventoryHandler.closeInventoryAllPlayers()
    }
}
