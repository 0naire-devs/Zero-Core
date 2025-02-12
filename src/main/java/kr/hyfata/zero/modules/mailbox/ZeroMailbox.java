package kr.hyfata.zero.modules.mailbox;

import kr.hyfata.zero.modules.mailbox.handler.MailboxHandler;
import kr.hyfata.zero.modules.mailbox.handler.MailboxInventoryHandler;
import kr.hyfata.zero.modules.mailbox.listener.MailboxCommand;
import kr.hyfata.zero.modules.mailbox.listener.MailboxEventListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ZeroMailbox {
    JavaPlugin plugin;
    MailboxInventoryHandler inventoryHandler;
    MailboxHandler handler = new MailboxHandler();

    public ZeroMailbox(JavaPlugin plugin) {
        this.plugin = plugin;
        inventoryHandler = new MailboxInventoryHandler(plugin);
        registerListeners();
        plugin.getLogger().info("Zero Mailbox has been enabled.");
    }

    private void registerListeners() {
        MailboxCommand command = new MailboxCommand(this);
        Objects.requireNonNull(plugin.getCommand("우편함")).setExecutor(command);
        Objects.requireNonNull(plugin.getCommand("우편함")).setTabCompleter(command);
        getServer().getPluginManager().registerEvents(new MailboxEventListener(this), plugin);
    }

    public void onDisable() {
        inventoryHandler.closeInventoryAllPlayers();
    }

    public MailboxInventoryHandler getInventoryHandler() {
        return inventoryHandler;
    }

    public MailboxHandler getHandler() {
        return handler;
    }
}
