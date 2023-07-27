package com.maanraj514.utils

import org.bukkit.entity.Player
import java.util.*

/**
 * This class is for handling cooldowns for the players.
 */
object CoolDownUtil {
    /*
     * The coolDownMap for storing the
     * player's set coolDown.
     */
    private val coolDownMap = HashMap<UUID, Long>()

    /**
     * This sets the coolDown for the player
     * and the seconds as the cooldown time.
     *
     * @param player the player with the set coolDown.
     * @param seconds the time the coolDown will be.
     */
    fun setCoolDown(player: Player, seconds: Int) {
        val delay = System.currentTimeMillis() + seconds * 1000L
        coolDownMap[player.uniqueId] = delay
    }

    /**
     * This checks if the player still has
     * a cooldown.
     *
     * @param player the player checked.
     */
    fun checkCoolDown(player: Player): Boolean {
        return !coolDownMap.containsKey(player.uniqueId) || coolDownMap[player.uniqueId]!! <= System.currentTimeMillis()
    }

    /**
     * This checks the time left
     * on the player's coolDown.
     *
     * @param player the player checked
     */
    fun timeLeft(player: Player): Long {
        return (coolDownMap[player.uniqueId]!! - System.currentTimeMillis()) / 1000
    }
}