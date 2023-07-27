package com.maanraj514.party.ranks

import com.maanraj514.party.PartyRank
import org.bukkit.ChatColor

class ModeratorRank : PartyRank {
    override fun getName(): String {
        return "&dMODERATOR"
    }

    override fun getPrefix(): String {
        return "&d[${getName()}&d]&d: "
    }

    override fun getColor(): ChatColor {
        return ChatColor.LIGHT_PURPLE
    }

    override fun getStrength(): Double {
        return 2.0
    }
}