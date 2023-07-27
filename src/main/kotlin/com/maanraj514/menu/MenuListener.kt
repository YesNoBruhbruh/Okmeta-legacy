package com.maanraj514.menu

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

/**
 * Registers the events from the menus.
 */
class MenuListener : Listener {

    @EventHandler
    fun onMenuClick(event: InventoryClickEvent) {
        if (event.clickedInventory != null) {
            val holder = event.clickedInventory!!.holder

            if (holder is Menu) {

                if (event.currentItem == null) {
                    return
                }
                if (holder.cancelAllClicks()) {
                    event.isCancelled = true
                }

                holder.handleMenu(event)
            }
        }
    }
}