package com.maanraj514.menu

import org.bukkit.entity.Player
import java.util.*

/**
 * This class is used for easy access to players
 * who open the menus.
 */
class PlayerMenuUtility(var owner: Player) {

    private val history = Stack<Menu>()
    private val dataMap: HashMap<String, Any> = HashMap()

    fun setData(identifier: String?, data: Any?) {
        dataMap[identifier!!] = data!!
    }

    fun setData(identifier: Enum<*>, data: Any?) {
        dataMap[identifier.toString()] = data!!
    }

    /**
     * @param identifier The key for the data stored in the PMC
     * @return The retrieved value or null if not found
     */
    fun getData(identifier: String?): Any? {
        return dataMap[identifier]
    }

    fun getData(identifier: Enum<*>): Any? {
        return dataMap[identifier.toString()]
    }

    fun <T> getData(identifier: String?, classRef: Class<T>): T? {
        val obj = dataMap[identifier]
        return if (obj == null) {
            null
        } else {
            classRef.cast(obj)
        }
    }

    fun <T> getData(identifier: Enum<*>, classRef: Class<T>): T? {
        val obj = dataMap[identifier.toString()]
        return if (obj == null) {
            null
        } else {
            classRef.cast(obj)
        }
    }

    fun lastMenu(): Menu {
        history.pop()
        return history.pop()
    }

    fun pushMenu(menu: Menu) {
        history.push(menu)
    }
}