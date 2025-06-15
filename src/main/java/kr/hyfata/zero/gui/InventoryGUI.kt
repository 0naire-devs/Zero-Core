package kr.hyfata.zero.gui

import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.inventory.Inventory

interface InventoryGUI {
    fun openInventory(p: Player)
    fun inventoryClickEvent(e: InventoryClickEvent)
    fun inventoryCloseEvent(e: InventoryCloseEvent)
    fun closeInventoryAllPlayers()
    fun contains(inventory: Inventory): Boolean
}