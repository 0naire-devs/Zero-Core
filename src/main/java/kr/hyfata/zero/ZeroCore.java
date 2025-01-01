package kr.hyfata.zero;

import kr.hyfata.zero.util.ConfigUtil;
import org.bukkit.plugin.java.JavaPlugin;

public final class ZeroCore extends JavaPlugin {
    public static final ZeroConfig configModules = new ZeroConfig();
    public static final ZeroModules modules = new ZeroModules();

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
        configModules.init(this);
        modules.init(this);
    }
}
