package com.maanraj514.command

import org.bukkit.command.CommandMap
import org.bukkit.plugin.java.JavaPlugin
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * This manages the commands, createCoreCommand etc.
 */
class CommandManager {

    /**
     * Creates a new core command
     * with the following params.
     *
     * @param plugin the main plugin instance.
     * @param commandName the name of the subCommand.
     * @param commandDescription the description of the subCommand.
     * @param commandUsage how you use the command.
     * @param commandList how the message should be sent.
     * @param aliases alternate names for the command.
     * @param subcommands the subCommands registered under the core command.
     * @throws NoSuchFieldException if the field doesn't exist.
     * @throws IllegalAccessException not allowed to access the instance.
     */
    @SafeVarargs
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun createCoreCommand(
        plugin: JavaPlugin,
        commandName: String,
        commandDescription: String,
        commandUsage: String,
        commandList: CommandList?,
        aliases: List<String?>?,
        vararg subcommands: Class<out SubCommand>
    ) {
        val commands = ArrayList<SubCommand?>()

        Arrays.stream(subcommands).map { subcommand ->
            try {
                val constructor: Constructor<out SubCommand?> = subcommand.getConstructor()
                return@map constructor.newInstance()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
            null
        }.forEach(commands::add)

        val commandField = plugin.server.javaClass.getDeclaredField("commandMap")
        commandField.isAccessible = true
        val commandMap = commandField[plugin.server] as CommandMap
        commandMap.register(
            commandName,
            CoreCommand(commandName, commandDescription, commandUsage, commandList, aliases, commands)
        )
    }

    /**
     * Creates a new core command
     * with the following params.
     *
     * @param plugin the main plugin instance.
     * @param commandName the name of the subCommand.
     * @param commandDescription the description of the subCommand.
     * @param commandUsage how you use the command.
     * @param commandList how the message should be sent.
     * @param subCommands the subCommands registered under the core command.
     * @throws NoSuchFieldException if the field doesn't exist.
     * @throws IllegalAccessException not allowed to access the instance.
     */
    @SafeVarargs
    @Throws(NoSuchFieldException::class, IllegalAccessException::class)
    fun createCoreCommand(
        plugin: JavaPlugin,
        commandName: String,
        commandDescription: String,
        commandUsage: String,
        commandList: CommandList?,
        vararg subCommands: Class<out SubCommand>
    ) {
        createCoreCommand(plugin, commandName, commandDescription, commandUsage, commandList, listOf(""), *subCommands)
    }
}