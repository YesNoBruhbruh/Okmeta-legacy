package com.maanraj514.test

import com.maanraj514.menu.MenuManager
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerJoinListener : Listener {

    @EventHandler
    fun onJoin(event: PlayerJoinEvent){
        MenuManager.openMenu(JoinMenu::class.java, event.player)
    }
}