package com.maanraj514.party.ranks

import com.maanraj514.party.PartyRank
import org.bukkit.ChatColor

class MemberRank : PartyRank {

    override fun getName(): String {
        return "&7MEMBER"
    }

    override fun getPrefix(): String {
        return "&7[${getName()}&7]&7: "
    }

    override fun getColor(): ChatColor {
        return ChatColor.GRAY
    }

    override fun getStrength(): Double {
        return 1.0
    }
}