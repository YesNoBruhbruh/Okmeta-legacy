package com.maanraj514.party

import org.bukkit.entity.Player
import java.util.UUID

class PartyManager {

    private val parties = mutableMapOf<UUID, Party>()

    fun createParty(player: Player) {
        val partyUUID = UUID.randomUUID()
        parties[partyUUID] = Party(partyUUID, player.uniqueId)
    }

    fun getParty(partyUUID: UUID): Party? {
        return parties[partyUUID]
    }

    fun isPlayerInParty(player: Player): Boolean {
        for (party in parties.values){
            if (party.hasPlayer(player)){
                return true
            }
        }
        return false
    }

    fun getPlayerParty(player: Player): Party? {
        for (party in parties.values){
            if (party.hasPlayer(player)){
                return party
            }
        }
        return null
    }

    fun getParties(): MutableMap<UUID, Party> {
        return parties
    }
}