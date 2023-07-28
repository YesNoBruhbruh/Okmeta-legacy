package com.maanraj514

import com.maanraj514.menu.MenuListener
import com.maanraj514.utils.toColor
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandMap
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException




/*
 * Main class of the entire core.
 */
abstract class Okmeta : JavaPlugin() {
    /*
     * This gets called when server starts.
     */
    abstract fun enable()

    /*
     * This gets called when server stops
     */
    abstract fun disable()

    companion object {
        lateinit var INSTANCE: JavaPlugin
    }

    override fun onLoad() {
        INSTANCE = this
    }

    override fun onEnable() {
        registerListeners(MenuListener())

        enable()

        Bukkit.getConsoleSender().sendMessage("&e[&eOkmeta&e]&e: &eThe Core has started!".toColor())
    }

    override fun onDisable() {
        disable()

        Bukkit.getConsoleSender().sendMessage("&c[&cOkmeta&c]&c: &cThe Core has stopped!".toColor())
    }

    fun registerListeners(vararg listeners: Listener?) {
        val pm = Bukkit.getPluginManager()

        for (listener in listeners) {
            pm.registerEvents(listener!!, this)
        }
    }

    fun registerCommands(command: Command) {
        try {
            val bukkitCommandMap: Field = Bukkit.getServer().javaClass.getDeclaredField("commandMap")
            bukkitCommandMap.isAccessible = true
            val commandMap = bukkitCommandMap.get(Bukkit.getServer()) as CommandMap

            commandMap.register(command.name, command)
        } catch (e: IllegalAccessException) {
            logger.severe("Could not retrieve the command map. (Illegal Access, Invocation Target, No Such Method)")
            return
        } catch (e: InvocationTargetException) {
            logger.severe("Could not retrieve the command map. (Illegal Access, Invocation Target, No Such Method)")
            return
        } catch (e: NoSuchMethodException) {
            logger.severe("Could not retrieve the command map. (Illegal Access, Invocation Target, No Such Method)")
            return
        }
    }
}