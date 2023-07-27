package com.maanraj514.party

import org.bukkit.ChatColor

/**
 * Interface for PartyRank.
 */
interface PartyRank {

    /*
    * The name of the PartyRank.
    */
    fun getName() : String
    /*
    * The prefix of the PartyRank.
    */
    fun getPrefix() : String
    /*
    * The color of the people with PartyRank messages.
    * example, if you have member your chat messages will be gray.
    */
    fun getColor() : ChatColor
    /*
    * The strength of the PartyRank.
    */
    fun getStrength() : Double
}