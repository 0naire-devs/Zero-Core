package kr.hyfata.zero

import kr.hyfata.zero.gui.InventoryEventListener
import kr.hyfata.zero.util.config.ConfigUtil
import org.bukkit.plugin.java.JavaPlugin

class ZeroCore : JavaPlugin() {
    private var modules: ZeroModules? = null

    override fun onEnable() {
        server.pluginManager.registerEvents(InventoryEventListener(), this)
        ConfigUtil.init(this)
        initModules()
    }

    override fun onDisable() {
        configModules!!.save()
        modules!!.onDisable()
    }

    private fun initModules() {
        configModules = ZeroConfig(this)
        modules = ZeroModules(this)
    }

    companion object {
        private var configModules: ZeroConfig? = null
        val zeroConfig: ZeroConfig
            get() = configModules!!
    }
}
