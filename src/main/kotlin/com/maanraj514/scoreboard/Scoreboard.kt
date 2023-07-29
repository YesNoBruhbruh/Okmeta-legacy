package com.maanraj514.scoreboard

import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.util.*

/**
 * Lightweight packet-based scoreboard API for Bukkit plugins.
 * It can be safely used asynchronously as everything is at packet level.
 *
 *
 * The project is on [GitHub](https://github.com/MrMicky-FR/FastBoard).
 *
 * @author MrMicky
 * @version 1.2.0
 */
class Scoreboard(player: Player?) : ScoreboardBase<String?>(player!!) {
    /**
     * {@inheritDoc}
     */
    override fun updateTitle(title: String?) {
        Objects.requireNonNull(title, "title")
        if (title != null) {
            require(!(!VersionType.V1_13.isHigherOrEqual && title.length > 32)) { "Title is longer than 32 chars" }
        }
        super.updateTitle(title)
    }

    /**
     * {@inheritDoc}
     */
    override fun updateLines(vararg lines: String?) {
        if (!VersionType.V1_13.isHigherOrEqual) {
            var lineCount = 0
            for (s in lines) {
                require(!(s != null && s.length > 30)) { "Line $lineCount is longer than 30 chars" }
                lineCount++
            }
        }
        super.updateLines(*lines)
    }

    @Throws(Throwable::class)
    override fun sendLineChange(score: Int) {
        val maxLength = if (hasLinesMaxLength()) 16 else 1024
        val line = getLineByScore(score)
        var prefix: String
        var suffix = ""
        if (line.isNullOrEmpty()) {
            prefix = COLOR_CODES[score] + ChatColor.RESET
        } else if (line.length <= maxLength) {
            prefix = line
        } else {
            // Prevent splitting color codes
            val index = if (line[maxLength - 1] == ChatColor.COLOR_CHAR) maxLength - 1 else maxLength
            prefix = line.substring(0, index)
            val suffixTmp = line.substring(index)
            var chatColor: ChatColor? = null
            if (suffixTmp.length >= 2 && suffixTmp[0] == ChatColor.COLOR_CHAR) {
                chatColor = ChatColor.getByChar(suffixTmp[1])
            }
            val color = ChatColor.getLastColors(prefix)
            val addColor = chatColor == null || chatColor.isFormat
            suffix = (if (addColor) (if (color.isEmpty()) ChatColor.RESET.toString() else color) else "") + suffixTmp
        }
        if (prefix.length > maxLength || suffix.length > maxLength) {
            // Something went wrong, just cut to prevent client crash/kick
            prefix = prefix.substring(0, maxLength)
            suffix = suffix.substring(0, maxLength)
        }
        sendTeamPacket(score, TeamMode.UPDATE, prefix, suffix)
    }

    @Throws(Throwable::class)
    override fun toMinecraftComponent(line: String?): Any? {
        return if (line.isNullOrEmpty()) {
            EMPTY_MESSAGE
        } else java.lang.reflect.Array.get(
            MESSAGE_FROM_STRING!!.invoke(
                line
            ), 0
        )
    }

    override fun emptyLine(): String {
        return ""
    }

    /**
     * Return if the player has a prefix/suffix characters limit.
     * By default, it returns true only in 1.12 or lower.
     * This method can be overridden to fix compatibility with some versions support plugin.
     *
     * @return max length
     */
    protected fun hasLinesMaxLength(): Boolean {
        return !VersionType.V1_13.isHigherOrEqual
    }

    companion object {
        private var MESSAGE_FROM_STRING: MethodHandle? = null
        private var EMPTY_MESSAGE: Any? = null

        init {
            try {
                val lookup = MethodHandles.lookup()
                val craftChatMessageClass = ScoreboardReflection.obcClass("util.CraftChatMessage")
                MESSAGE_FROM_STRING =
                    lookup.unreflect(craftChatMessageClass.getMethod("fromString", String::class.java))
                EMPTY_MESSAGE = java.lang.reflect.Array.get(MESSAGE_FROM_STRING?.invoke(""), 0)
            } catch (t: Throwable) {
                throw ExceptionInInitializerError(t)
            }
        }
    }
}