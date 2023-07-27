package com.maanraj514.command

import org.bukkit.command.CommandSender

/**
 * This interface adds a feature in case you want a custom message to be sent
 * when showing the subCommands.
 */
interface CommandList {
    /**
     * Can be overridden.
     *
     * @param sender who to send the message.
     * @param subCommandList the commandList.
     */
    fun displayCommandList(sender: CommandSender, subCommandList: List<SubCommand?>)
}