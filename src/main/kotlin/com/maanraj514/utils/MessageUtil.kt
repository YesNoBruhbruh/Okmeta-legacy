package com.maanraj514.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import net.md_5.bungee.api.ChatColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.time.Duration
import java.util.UUID
import java.util.regex.Pattern

fun String.toColor() : String{
    val pattern = Pattern.compile("#[a-fA-F0-9]{6}")

    var text = this
    var matcher = pattern.matcher(text)
    while (matcher.find()) {
        val color = text.substring(matcher.start(), matcher.end())
        text = text.replace(color, ChatColor.of(color).toString())
        matcher = pattern.matcher(text)
    }
    return ChatColor.translateAlternateColorCodes('&', text)
}

/**
 * Used for sending certain messages such as sendTitle() and sendActionBar().
 */
object MessageUtil {

    /**
     * Send a title to the player.
     *
     * @param title title to send
     * @param subTitle subtitle to send
     * @param fadeIn how long to fade in
     * @param stay how long it stays
     * @param fadeOut how long it fades out
     * @param players the players to send it to
     */
    fun sendTitle(title: String, subTitle: String, fadeIn: Long, stay: Long, fadeOut: Long, vararg players: Player) {
        val times = Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))
        val title1 = Title.title(Component.text(title), Component.text(subTitle), times)

        for (player in players) {
            player.showTitle(title1)
        }
    }

    /**
     * Send an action bar to the players.
     *
     * @param message the message in the action bar
     * @param players the players to send it to
     */
    fun sendActionbar(message: String, vararg players: Player) {
        for (player in players) {
            player.sendActionBar(Component.text(message))
        }
    }

    /**
     * Send an action bar to the player.
     *
     * @param message the message in the action bar
     * @param player the player to send it to
     */
    fun sendActionbar(message: String, player: Player) {
        player.sendActionBar(Component.text(message))
    }

    /**
     * Sends a message with lines.
     *
     * @param message the message inside the lines.
     * @param player the player to be sent the message.
     */
    fun sendMessageWithLines(message: String, player: Player){
        player.sendMessage("--------------------")
        player.sendMessage(message.toColor())
        player.sendMessage("--------------------")
    }

    /**
     * Sends a message with lines.
     *
     * @param message the message inside the lines.
     * @param player the player to be sent the message.
     * @param line1 the line1.
     * @param line2 the line2.
     */
    fun sendMessageWithLines(message: String, player: Player, line1: String, line2: String){
        player.sendMessage(line1.toColor())
        player.sendMessage(message.toColor())
        player.sendMessage(line2.toColor())
    }

    /**
     * Broadcast a message to
     * the entire server.
     *
     * @param message the message broadcasted.
     */
    fun broadcast(message: String) {
        Bukkit.broadcastMessage(message.toColor())
    }

    /**
     * Broadcast a message to
     * multiple players.
     * (Player)
     *
     * @param message the message broadcasted to the players.
     * @param players the players who receive the broadcast.
     */
    fun broadcast(message: String, vararg players: Player) {
        for (player in players) {
            player.sendMessage(message.toColor())
        }
    }

    /**
     * Broadcast a message to
     * multiple players.
     * (UUID)
     *
     * @param message the message broadcasted to the players.
     * @param players the players who receive the broadcast.
     */
    fun broadcast(message: String, players: List<UUID>) {
        for (player in players) {
            if (Bukkit.getPlayer(player) != null){
                Bukkit.getPlayer(player)?.sendMessage(message.toColor())
            }
        }
    }
}