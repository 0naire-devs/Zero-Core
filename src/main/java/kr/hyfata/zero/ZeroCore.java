package kr.hyfata.zero;

import kr.hyfata.zero.util.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZeroCore extends JavaPlugin {
    public static ZeroConfig configModules;
    public static ZeroModules modules;

    @Override
    public void onEnable() {
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
}
