package kr.hyfata.zero;

import kr.hyfata.zero.gui.InventoryEventListener;
import kr.hyfata.zero.util.config.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZeroCore extends JavaPlugin {
    private static ZeroConfig configModules;
    private ZeroModules modules;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new InventoryEventListener(), this);
        ConfigUtil.init(this);
        initModules();
    }

    @Override
    public void onDisable() {
        configModules.save();
        modules.onDisable();
    }

    private void initModules() {
        configModules = new ZeroConfig(this);
        modules = new ZeroModules(this);
    }

    public static ZeroConfig getZeroConfig() {
        return configModules;
    }
}
