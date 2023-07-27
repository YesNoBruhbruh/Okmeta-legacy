package com.maanraj514.party.ranks

import com.maanraj514.party.PartyRank
import org.bukkit.ChatColor

class LeaderRank : PartyRank {
    override fun getName(): String {
        return "&eLEADER"
    }

    override fun getPrefix(): String {
        return "&e[${getName()}&e]&e: "
    }

    override fun getColor(): ChatColor {
        return ChatColor.GOLD
    }

    override fun getStrength(): Double {
        return 3.0
    }
}