package kr.hyfata.zero.modules.jangsa;

import kr.hyfata.zero.modules.jangsa.listener.JangsaCommand;
import kr.hyfata.zero.modules.jangsa.listener.JangsaEventListener;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class ZeroJangsa {
    JavaPlugin plugin;
    public ZeroJangsa(JavaPlugin plugin) {
        this.plugin = plugin;
        registerListeners();
        plugin.getLogger().info("Zero Jangsa has been enabled.");
    }

    public void registerListeners() {
        JangsaCommand myCmd = new JangsaCommand(this);
        PluginCommand plCmd = plugin.getCommand("장사");
        if (plCmd != null) {
            plCmd.setExecutor(myCmd);
            plCmd.setTabCompleter(myCmd);
        }

        Bukkit.getServer().getPluginManager().registerEvents(new JangsaEventListener(this), plugin);
    }
}
