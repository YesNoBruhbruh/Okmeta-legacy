package com.maanraj514.menu

import org.bukkit.entity.Player
import java.util.*

/**
 * This class is used for easy access to players
 * who open the menus.
 */
class PlayerMenuUtility(var owner: Player) {

    private val history = Stack<Menu>()

    fun lastMenu(): Menu {
        history.pop()
        return history.pop()
    }

    fun pushMenu(menu: Menu) {
        history.push(menu)
    }
}