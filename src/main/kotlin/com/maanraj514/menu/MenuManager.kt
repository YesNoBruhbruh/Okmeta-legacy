package com.maanraj514.menu

import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

/**
 * This class handles opening menus and playerMenuUtility.
 */
object MenuManager {

    private val playerMenuUtilityMap = mutableMapOf<Player, PlayerMenuUtility>()

    fun openMenu(menuClass: Class<out Menu?>, player: Player) {
        try {
            menuClass.getConstructor(PlayerMenuUtility::class.java).newInstance(getPlayerMenuUtility(player))!!
                .open()
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: InstantiationException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        }
    }

    fun getPlayerMenuUtility(player: Player): PlayerMenuUtility? {
        return if (playerMenuUtilityMap.containsKey(player)) {
            playerMenuUtilityMap[player]
        } else {
            val playerMenuUtility = PlayerMenuUtility(player)
            playerMenuUtilityMap[player] = playerMenuUtility
            playerMenuUtility
        }
    }
}