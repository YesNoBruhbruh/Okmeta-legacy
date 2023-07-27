package com.maanraj514.command

import com.maanraj514.utils.toColor
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import java.util.*

/**
 * This is the class for CoreCommand.
 */
/**
 * Creates a new instance of
 * CoreCommand.
 *
 * @param name the name of the command.
 * @param description the description of the command.
 * @param usageMessage how to use the command.
 * @param commandList how the commandList should be sent.
 * @param aliases alternative names for the command.
 * @param subCommands the subCommands list.
 */
class CoreCommand(name: String?, description: String?, usageMessage: String?, private val commandList: CommandList?, aliases: List<String?>?, private val subCommands: ArrayList<SubCommand?>) : Command(name!!, description!!, usageMessage!!, aliases!!) {

    /**
     * This executes the command.
     *
     * @param sender who executed the command.
     * @param s this does nothing.
     * @param args the arguments for the command.
     * @return true.
     */
    override fun execute(sender: CommandSender, s: String, args: Array<String>): Boolean {
        if (args.isNotEmpty()) {
            for (subCommand in subCommands) {
                if (args[0].equals(subCommand!!.name, ignoreCase = true) || subCommand.aliases != null && subCommand.aliases!!.contains(args[0])) {
                    if (subCommand.permission != null) {
                        if (sender.hasPermission(subCommand.permission!!)) {
                            subCommand.perform(sender, args)
                        }
                    }
                }
            }
        } else {
            if (commandList == null) {
                sender.sendMessage("&e--------------------".toColor())

                for (subCommand in subCommands) {
                    sender.sendMessage((subCommand!!.syntax + " &b- " + subCommand.description).toColor())
                }

                sender.sendMessage("&e--------------------".toColor())
            } else {
                commandList.displayCommandList(sender, subCommands)
            }
        }
        return true
    }

    /**
     * Tab completer for the
     * core command.
     *
     * @param sender the sender of the command.
     * @param alias alternative name of the command.
     * @param args arguments of the command.
     * @return arrayList of the subCommands.
     */
    override fun tabComplete(sender: CommandSender, alias: String, args: Array<String>): List<String> {
        if (args.size == 1) { // /lepvp <subCommand> <args>
            val subCommandsArguments = ArrayList<String>()

            for (subCommand in subCommands) {
                subCommandsArguments.add(subCommand!!.name!!)
            }

            return subCommandsArguments
        } else if (args.size >= 2) {
            for (subCommand in subCommands) {
                if (args[0].equals(subCommand!!.name, ignoreCase = true)) {

                    return subCommand.getSubCommandArguments(sender as Player, args)
                }
            }
        }
        return emptyList()
    }
}