package com.maanraj514.command

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
 * This is the parent class of subCommands.
 */
abstract class SubCommand {
    /*
     * The name of the command.
     */
    abstract val name: String

    /*
     * The aliases of the command.
     */
    abstract val aliases: List<String?>

    /*
     * The description of the command.
     */
    abstract val description: String

    /*
     * The syntax of the command.
     */
    abstract val syntax: String

    /*
     * The permission to use the command.
     */
    abstract val permission: String

    /**
     * The perform method of the command.
     *
     * @param sender who performs the command.
     * @param args the arguments for the command.
     */
    abstract fun perform(sender: CommandSender?, args: Array<String>)

    /**
     * Gets the subCommandArguments.
     *
     * @param player who we get the args from.
     * @param args the arguments of the subCommand.
     * @return subCommandArguments.
     */
    abstract fun getSubCommandArguments(player: Player?, args: Array<String>): List<String>
}