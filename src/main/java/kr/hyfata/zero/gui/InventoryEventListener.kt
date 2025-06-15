package kr.hyfata.zero.gui

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent

class InventoryEventListener : Listener {
    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        for (menu in inventories) {
            if (menu.contains(e.inventory)) {
                menu.inventoryClickEvent(e)
            }
        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        for (menu in inventories) {
            if (menu.contains(e.inventory)) {
                menu.inventoryCloseEvent(e)
            }
        }
    }

    companion object {
        private val inventories = ArrayList<InventoryGUI>()

        fun registerInventory(inventory: InventoryGUI?) {
            inventories.add(inventory!!)
        }
    }
}
