package com.maanraj514.utils

import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull

/*
* This class is used for players.
*/
class PlayerUtil {

    /**
     * Resets the player to default.
     *
     * @param player the player reset.
     */
    fun reset(@NotNull player: Player) {
        for (effect in player.activePotionEffects){
            player.removePotionEffect(effect.type)
        }

        player.walkSpeed = 0.2f
        player.allowFlight = false
        player.isFlying = false
        player.openInventory.close()
        player.inventory.clear()
        player.inventory.setArmorContents(null)
        player.maxHealth = 20.0
        player.health = 20.0
        player.foodLevel = 20
        player.saturation = 20f
        player.exp = 0f
        player.level = 0
        player.fireTicks = 0
        player.gameMode = GameMode.SURVIVAL

        if (player.vehicle != null) player.leaveVehicle()
        if (player.passengers.contains(player)) player.passengers.remove(player)
    }

    /**
     * Checks if a player's inventory
     * is full.
     *
     * @param player the player checked.
     * @return true if inventory is full, false if not.
     */
    fun isInventoryFull(@NotNull player: Player): Boolean {
        return player.inventory.firstEmpty() == -1
    }
}