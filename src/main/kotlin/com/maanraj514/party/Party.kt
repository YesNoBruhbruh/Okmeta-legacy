package com.maanraj514.party

import com.maanraj514.Okmeta
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitTask
import java.util.*

class Party(private val partyUUID: UUID, private var owner: UUID) {

    private val players = mutableMapOf<UUID, PartyRank>()

    private val onlinePlayers = mutableListOf<UUID>()
    private val offlinePlayers = mutableListOf<UUID>()

    private val inviteTasks = mutableMapOf<UUID, BukkitTask>()
    private val removeTasks = mutableMapOf<UUID, BukkitTask>()

    fun addPlayer(player: Player, rank: PartyRank){
        players[player.uniqueId] = rank
        onlinePlayers.add(player.uniqueId)
    }

    fun acceptInvite(player: Player, partyRank: PartyRank){
        if (inviteTasks.containsKey(player.uniqueId)){
            inviteTasks[player.uniqueId]?.cancel()
            inviteTasks.remove(player.uniqueId)
        }
        addPlayer(player, partyRank)
    }

    fun removePlayer(player: Player, time: Long) {
        removeTasks[player.uniqueId] = Bukkit.getScheduler().runTaskLater(
            Okmeta.INSTANCE,
            Runnable {
                removePlayer(player)
            }, time)
    }

    fun removePlayer(offlinePlayer: OfflinePlayer) {
        players.remove(offlinePlayer.uniqueId)
        onlinePlayers.remove(offlinePlayer.uniqueId)
        offlinePlayers.remove(offlinePlayer.uniqueId)
    }

    fun invite(invited: Player, timeToExpire: Long) {
        inviteTasks[invited.uniqueId] = Bukkit.getScheduler().runTaskLater(
            Okmeta.INSTANCE,
            Runnable {
                inviteTasks.remove(invited.uniqueId)
        }, timeToExpire)
    }

    fun rejoin(player: Player) {
        if (removeTasks.containsKey(player.uniqueId)){
            removeTasks[player.uniqueId]?.cancel()
            removeTasks.remove(player.uniqueId)
        }
    }

    /**
     * Transfers the party to a new owner.
     *
     * @param newOwner the new owner of the party.
     * @param newOwnerRank the new rank of the new owner of the party. It is
     * recommended you set this to the strongest PartyRank.
     * @param oldOwnerRank the new rank of the OLD owner of the party.
     */
    fun transfer(newOwner: Player, newOwnerRank: PartyRank, oldOwnerRank: PartyRank) {
        players[owner] = oldOwnerRank
        players[newOwner.uniqueId] = newOwnerRank
        owner = newOwner.uniqueId
    }

    /**
     * Promote the player to a rank.
     *
     * @param player the player to promote.
     * @param rankToPromote the rank to promote the player to.
     */
    fun promote(player: Player, rankToPromote: PartyRank) {
        val rank = players[player.uniqueId]

        val rankStrength = rank?.getStrength()
        val rankToPromoteStrength = rankToPromote.getStrength()

        if (rankStrength != null) {
            if (rankStrength <= rankToPromoteStrength) {
                players[player.uniqueId] = rankToPromote
            }
        }
    }

    /**
     * Demote the player to a rank.
     *
     * @param player the player to demote.
     * @param rankToDemote the rank to demote the player to.
     */
    fun demote(player: Player, rankToDemote: PartyRank){
        val rank = players[player.uniqueId]

        val rankStrength = rank?.getStrength()
        val rankToDemoteStrength = rankToDemote.getStrength()

        if (rankStrength != null) {
            if (rankStrength >= rankToDemoteStrength){
                players[player.uniqueId] = rankToDemote
            }
        }
    }

    /**
     * Kicks all offlinePlayers from the party.
     */
    fun kickOffline() {
        for (uuid in offlinePlayers) {
            removePlayer(Bukkit.getOfflinePlayer(uuid))
        }
    }

    /**
     * Kicks a specified offlinePlayer from the party.
     */
    fun kickOffline(offlinePlayer: OfflinePlayer) {
        removePlayer(offlinePlayer)
    }

    /**
     * Disband the party.
     */
    fun disband() {
        players.clear()
        onlinePlayers.clear()
        offlinePlayers.clear()
        if (inviteTasks.isNotEmpty()){
            for (task in inviteTasks){
                task.value.cancel()
            }
            inviteTasks.clear()
        }
        if (removeTasks.isNotEmpty()){
            for (task in removeTasks){
                task.value.cancel()
            }
            removeTasks.clear()
        }
    }

    fun hasRank(player: Player, partyRank: PartyRank) : Boolean {
        return players[player.uniqueId] == partyRank
    }
}