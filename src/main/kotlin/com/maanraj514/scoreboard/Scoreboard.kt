package com.maanraj514.scoreboard

import com.maanraj514.nms.FastReflection
import com.maanraj514.nms.FastReflection.PacketConstructor
import com.maanraj514.nms.FastReflection.PacketConstructor.invoke
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.*
import java.util.concurrent.ThreadLocalRandom

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
class Scoreboard(player: Player) {
    /**
     * Get the player who has the scoreboard.
     *
     * @return current player for this FastBoard
     */
    val player: Player

    /**
     * Get the scoreboard id.
     *
     * @return the id
     */
    val id: String
    private val lines: MutableList<String> = ArrayList()

    /**
     * Get the scoreboard title.
     *
     * @return the scoreboard title
     */
    var title = ChatColor.RESET.toString()
        private set

    /**
     * Get if the scoreboard is deleted.
     *
     * @return true if the scoreboard is deleted
     */
    var isDeleted = false
        private set

    /**
     * Creates a new FastBoard.
     *
     * @param player the owner of the scoreboard
     */
    init {
        this.player = Objects.requireNonNull(player, "player")
        id = "sb-" + Integer.toHexString(ThreadLocalRandom.current().nextInt())
        try {
            sendObjectivePacket(ObjectiveMode.CREATE)
            sendDisplayObjectivePacket()
        } catch (t: Throwable) {
            throw RuntimeException("Unable to create scoreboard", t)
        }
    }

    /**
     * Update the scoreboard title.
     *
     * @param title the new scoreboard title
     * @throws IllegalArgumentException if the title is longer than 32 chars on 1.12 or lower
     * @throws IllegalStateException    if [.delete] was call before
     */
    fun updateTitle(title: String) {
        if (this.title == Objects.requireNonNull(title, "title")) {
            return
        }
        require(!(!VersionType.V1_13.isHigherOrEqual && title.length > 32)) { "Title is longer than 32 chars" }
        this.title = title
        try {
            sendObjectivePacket(ObjectiveMode.UPDATE)
        } catch (t: Throwable) {
            throw RuntimeException("Unable to update scoreboard title", t)
        }
    }

    /**
     * Get the scoreboard lines.
     *
     * @return the scoreboard lines
     */
    fun getLines(): List<String> {
        return ArrayList(lines)
    }

    /**
     * Get the specified scoreboard line.
     *
     * @param line the line number
     * @return the line
     * @throws IndexOutOfBoundsException if the line is higher than `size`
     */
    fun getLine(line: Int): String {
        checkLineNumber(line, true, false)
        return lines[line]
    }

    /**
     * Update a single scoreboard line.
     *
     * @param line the line number
     * @param text the new line text
     * @throws IndexOutOfBoundsException if the line is higher than [size() + 1][.size]
     */
    @Synchronized
    fun updateLine(line: Int, text: String) {
        checkLineNumber(line, false, true)
        try {
            if (line < size()) {
                lines[line] = text
                sendTeamPacket(getScoreByLine(line), TeamMode.UPDATE)
                return
            }
            val newLines: MutableList<String> = ArrayList(lines)
            if (line > size()) {
                for (i in size() until line) {
                    newLines.add("")
                }
            }
            newLines.add(text)
            updateLines(newLines)
        } catch (t: Throwable) {
            throw RuntimeException("Unable to update scoreboard lines", t)
        }
    }

    /**
     * Remove a scoreboard line.
     *
     * @param line the line number
     */
    @Synchronized
    fun removeLine(line: Int) {
        checkLineNumber(line, false, false)
        if (line >= size()) {
            return
        }
        val newLines: List<String> = ArrayList(lines)
        newLines.removeAt(line)
        updateLines(newLines)
    }

    /**
     * Update all the scoreboard lines.
     *
     * @param lines the new lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
     * @throws IllegalStateException    if [.delete] was call before
     */
    fun updateLines(vararg lines: String?) {
        updateLines(Arrays.asList(*lines))
    }

    /**
     * Update the lines of the scoreboard
     *
     * @param lines the new scoreboard lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
     * @throws IllegalStateException    if [.delete] was call before
     */
    @Synchronized
    fun updateLines(lines: Collection<String>) {
        Objects.requireNonNull(lines, "lines")
        checkLineNumber(lines.size, false, true)
        if (!VersionType.V1_13.isHigherOrEqual) {
            var lineCount = 0
            for (s in lines) {
                require(!(s != null && s.length > 30)) { "Line $lineCount is longer than 30 chars" }
                lineCount++
            }
        }
        val oldLines: List<String> = ArrayList(this.lines)
        this.lines.clear()
        this.lines.addAll(lines)
        val linesSize = this.lines.size
        try {
            if (oldLines.size != linesSize) {
                val oldLinesCopy: List<String> = ArrayList(oldLines)
                if (oldLines.size > linesSize) {
                    for (i in oldLinesCopy.size downTo linesSize + 1) {
                        sendTeamPacket(i - 1, TeamMode.REMOVE)
                        sendScorePacket(i - 1, ScoreboardAction.REMOVE)
                        oldLines.removeAt(0)
                    }
                } else {
                    for (i in oldLinesCopy.size until linesSize) {
                        sendScorePacket(i, ScoreboardAction.CHANGE)
                        sendTeamPacket(i, TeamMode.CREATE)
                        oldLines.add(oldLines.size - i, getLineByScore(i))
                    }
                }
            }
            for (i in 0 until linesSize) {
                if (getLineByScore(oldLines, i) != getLineByScore(i)) {
                    sendTeamPacket(i, TeamMode.UPDATE)
                }
            }
        } catch (t: Throwable) {
            throw RuntimeException("Unable to update scoreboard lines", t)
        }
    }

    /**
     * Get the scoreboard size (the number of lines).
     *
     * @return the size
     */
    fun size(): Int {
        return lines.size
    }

    /**
     * Delete this FastBoard, and will remove the scoreboard for the associated player if he is online.
     * After this, all uses of [.updateLines] and [.updateTitle] will throws an [IllegalStateException]
     *
     * @throws IllegalStateException if this was already call before
     */
    fun delete() {
        try {
            for (i in lines.indices) {
                sendTeamPacket(i, TeamMode.REMOVE)
            }
            sendObjectivePacket(ObjectiveMode.REMOVE)
        } catch (t: Throwable) {
            throw RuntimeException("Unable to delete scoreboard", t)
        }
        isDeleted = true
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

    private fun checkLineNumber(line: Int, checkInRange: Boolean, checkMax: Boolean) {
        require(line >= 0) { "Line number must be positive" }
        require(!(checkInRange && line >= lines.size)) { "Line number must be under " + lines.size }
        require(!(checkMax && line >= COLOR_CODES.size - 1)) { "Line number is too high: $line" }
    }

    private fun getScoreByLine(line: Int): Int {
        return lines.size - line - 1
    }

    private fun getLineByScore(score: Int): String {
        return getLineByScore(lines, score)
    }

    private fun getLineByScore(lines: List<String>, score: Int): String {
        return lines[lines.size - score - 1]
    }

    @Throws(Throwable::class)
    private fun sendObjectivePacket(mode: ObjectiveMode) {
        val packet = PACKET_SB_OBJ!!.invoke()
        setField(packet, String::class.java, id)
        setField(packet, Int::class.javaPrimitiveType, mode.ordinal)
        if (mode != ObjectiveMode.REMOVE) {
            setComponentField(packet, title, 1)
            if (VersionType.V1_8.isHigherOrEqual) {
                setField(packet, ENUM_SB_HEALTH_DISPLAY, ENUM_SB_HEALTH_DISPLAY_INTEGER)
            }
        } else if (VERSION_TYPE == VersionType.V1_7) {
            setField(packet, String::class.java, "", 1)
        }
        sendPacket(packet)
    }

    @Throws(Throwable::class)
    private fun sendDisplayObjectivePacket() {
        val packet = PACKET_SB_DISPLAY_OBJ!!.invoke()
        setField(packet, Int::class.javaPrimitiveType, 1) // Position (1: sidebar)
        setField(packet, String::class.java, id) // Score Name
        sendPacket(packet)
    }

    @Throws(Throwable::class)
    private fun sendScorePacket(score: Int, action: ScoreboardAction) {
        val packet = PACKET_SB_SCORE!!.invoke()
        setField(packet, String::class.java, COLOR_CODES[score], 0) // Player Name
        if (VersionType.V1_8.isHigherOrEqual) {
            setField(
                packet,
                ENUM_SB_ACTION,
                if (action == ScoreboardAction.REMOVE) ENUM_SB_ACTION_REMOVE else ENUM_SB_ACTION_CHANGE
            )
        } else {
            setField(packet, Int::class.javaPrimitiveType, action.ordinal, 1) // Action
        }
        if (action == ScoreboardAction.CHANGE) {
            setField(packet, String::class.java, id, 1) // Objective Name
            setField(packet, Int::class.javaPrimitiveType, score) // Score
        }
        sendPacket(packet)
    }

    @Throws(Throwable::class)
    private fun sendTeamPacket(score: Int, mode: TeamMode) {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw UnsupportedOperationException()
        }
        val maxLength = if (hasLinesMaxLength()) 16 else 1024
        val packet = PACKET_SB_TEAM!!.invoke()
        setField(packet, String::class.java, id + ':' + score) // Team name
        setField(
            packet,
            Int::class.javaPrimitiveType,
            mode.ordinal,
            if (VERSION_TYPE == VersionType.V1_8) 1 else 0
        ) // Update mode
        if (mode == TeamMode.CREATE || mode == TeamMode.UPDATE) {
            val line = getLineByScore(score)
            var prefix: String
            var suffix: String? = null
            if (line == null || line.isEmpty()) {
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
                suffix =
                    (if (addColor) (if (color.isEmpty()) ChatColor.RESET.toString() else color) else "") + suffixTmp
            }
            if (prefix.length > maxLength || suffix != null && suffix.length > maxLength) {
                // Something went wrong, just cut to prevent client crash/kick
                prefix = prefix.substring(0, maxLength)
                suffix = suffix?.substring(0, maxLength)
            }
            if (VersionType.V1_17.isHigherOrEqual) {
                val team = PACKET_SB_SERIALIZABLE_TEAM!!.invoke()
                // Since the packet is initialized with null values, we need to change more things.
                setComponentField(team, "", 0) // Display name
                setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING) // Color
                setComponentField(team, prefix, 1) // Prefix
                setComponentField(team, suffix ?: "", 2) // Suffix
                setField(team, String::class.java, "always", 0) // Visibility
                setField(team, String::class.java, "always", 1) // Collisions
                setField(packet, Optional::class.java, Optional.of(team!!))
            } else {
                setComponentField(packet, prefix, 2) // Prefix
                setComponentField(packet, suffix ?: "", 3) // Suffix
                setField(packet, String::class.java, "always", 4) // Visibility for 1.8+
                setField(packet, String::class.java, "always", 5) // Collisions for 1.9+
            }
            if (mode == TeamMode.CREATE) {
                setField(
                    packet, MutableCollection::class.java, listOf(
                        COLOR_CODES[score]
                    )
                ) // Players in the team
            }
        }
        sendPacket(packet)
    }

    @Throws(Throwable::class)
    private fun sendPacket(packet: Any?) {
        check(!isDeleted) { "This FastBoard is deleted" }
        if (player.isOnline) {
            val entityPlayer = PLAYER_GET_HANDLE!!.invoke(player)
            val playerConnection = PLAYER_CONNECTION!!.invoke(entityPlayer)
            SEND_PACKET!!.invoke(playerConnection, packet)
        }
    }

    @Throws(ReflectiveOperationException::class)
    private fun setField(`object`: Any?, fieldType: Class<*>?, value: Any?) {
        setField(`object`, fieldType, value, 0)
    }

    @Throws(ReflectiveOperationException::class)
    private fun setField(packet: Any?, fieldType: Class<*>?, value: Any?, count: Int) {
        var i = 0
        for (field in PACKETS[packet!!.javaClass]!!) {
            if (field.type == fieldType && count == i++) {
                field[packet] = value
            }
        }
    }

    @Throws(Throwable::class)
    private fun setComponentField(packet: Any?, value: String, count: Int) {
        if (!VersionType.V1_13.isHigherOrEqual) {
            setField(packet, String::class.java, value, count)
            return
        }
        var i = 0
        for (field in PACKETS[packet!!.javaClass]!!) {
            if ((field.type == String::class.java || field.type == CHAT_COMPONENT_CLASS) && count == i++) {
                field[packet] = if (value.isEmpty()) EMPTY_MESSAGE else java.lang.reflect.Array.get(
                    MESSAGE_FROM_STRING!!.invoke(value), 0
                )
            }
        }
    }

    internal enum class ObjectiveMode {
        CREATE, REMOVE, UPDATE
    }

    internal enum class TeamMode {
        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
    }

    internal enum class ScoreboardAction {
        CHANGE, REMOVE
    }

    internal enum class VersionType {
        V1_7, V1_8, V1_13, V1_17;

        val isHigherOrEqual: Boolean
            get() = VERSION_TYPE!!.ordinal >= ordinal
    }

    companion object {
        private val PACKETS: MutableMap<Class<*>, Array<Field>> = HashMap(8)
        private val COLOR_CODES = Arrays.stream<ChatColor>(ChatColor.values())
            .map<String> { obj: ChatColor -> obj.toString() }
            .toArray<String> { _Dummy_.__Array__() }
        private val VERSION_TYPE: VersionType? = null

        // Packets and components
        private val CHAT_COMPONENT_CLASS: Class<*>? = null
        private val CHAT_FORMAT_ENUM: Class<*>? = null
        private val EMPTY_MESSAGE: Any? = null
        private val RESET_FORMATTING: Any? = null
        private val MESSAGE_FROM_STRING: MethodHandle? = null
        private val PLAYER_CONNECTION: MethodHandle? = null
        private val SEND_PACKET: MethodHandle? = null
        private val PLAYER_GET_HANDLE: MethodHandle? = null

        // Scoreboard packets
        private val PACKET_SB_OBJ: PacketConstructor? = null
        private val PACKET_SB_DISPLAY_OBJ: PacketConstructor? = null
        private val PACKET_SB_SCORE: PacketConstructor? = null
        private val PACKET_SB_TEAM: PacketConstructor? = null
        private val PACKET_SB_SERIALIZABLE_TEAM: PacketConstructor? = null

        // Scoreboard enums
        private val ENUM_SB_HEALTH_DISPLAY: Class<*>? = null
        private val ENUM_SB_ACTION: Class<*>? = null
        private val ENUM_SB_HEALTH_DISPLAY_INTEGER: Any? = null
        private val ENUM_SB_ACTION_CHANGE: Any? = null
        private val ENUM_SB_ACTION_REMOVE: Any? = null

        init {
            try {
                val lookup = MethodHandles.lookup()
                if (FastReflection.isRepackaged()) {
                    VERSION_TYPE = VersionType.V1_17
                } else if (FastReflection.nmsOptionalClass(null, "ScoreboardServer\$Action").isPresent) {
                    VERSION_TYPE = VersionType.V1_13
                } else if (FastReflection.nmsOptionalClass(
                        null,
                        "IScoreboardCriteria\$EnumScoreboardHealthDisplay"
                    ).isPresent
                ) {
                    VERSION_TYPE = VersionType.V1_8
                } else {
                    VERSION_TYPE = VersionType.V1_7
                }
                val gameProtocolPackage = "network.protocol.game"
                val craftPlayerClass = FastReflection.obcClass("entity.CraftPlayer")
                val craftChatMessageClass = FastReflection.obcClass("util.CraftChatMessage")
                val entityPlayerClass = FastReflection.nmsClass("server.level", "EntityPlayer")
                val playerConnectionClass = FastReflection.nmsClass("server.network", "PlayerConnection")
                val packetClass = FastReflection.nmsClass("network.protocol", "Packet")
                val packetSbObjClass = FastReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective")
                val packetSbDisplayObjClass =
                    FastReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective")
                val packetSbScoreClass = FastReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardScore")
                val packetSbTeamClass = FastReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam")
                val sbTeamClass =
                    if (VersionType.V1_17.isHigherOrEqual) FastReflection.innerClass(packetSbTeamClass) { innerClass -> !innerClass!!.isEnum } else null
                val playerConnectionField = Arrays.stream(entityPlayerClass.fields)
                    .filter { field: Field -> field.type.isAssignableFrom(playerConnectionClass) }
                    .findFirst().orElseThrow { NoSuchFieldException() }
                MESSAGE_FROM_STRING =
                    lookup.unreflect(craftChatMessageClass.getMethod("fromString", String::class.java))
                CHAT_COMPONENT_CLASS = FastReflection.nmsClass("network.chat", "IChatBaseComponent")
                CHAT_FORMAT_ENUM = FastReflection.nmsClass(null, "EnumChatFormat")
                EMPTY_MESSAGE = java.lang.reflect.Array.get(MESSAGE_FROM_STRING.invoke(""), 0)
                RESET_FORMATTING = FastReflection.enumValueOf(CHAT_FORMAT_ENUM, "RESET", 21)
                PLAYER_GET_HANDLE =
                    lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass))
                PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField)
                SEND_PACKET = lookup.findVirtual(
                    playerConnectionClass,
                    "sendPacket",
                    MethodType.methodType(Void.TYPE, packetClass)
                )
                PACKET_SB_OBJ = FastReflection.findPacketConstructor(packetSbObjClass, lookup)
                PACKET_SB_DISPLAY_OBJ = FastReflection.findPacketConstructor(packetSbDisplayObjClass, lookup)
                PACKET_SB_SCORE = FastReflection.findPacketConstructor(packetSbScoreClass, lookup)
                PACKET_SB_TEAM = FastReflection.findPacketConstructor(packetSbTeamClass, lookup)
                PACKET_SB_SERIALIZABLE_TEAM =
                    if (sbTeamClass == null) null else FastReflection.findPacketConstructor(sbTeamClass, lookup)
                for (clazz in Arrays.asList<Class<out Any>?>(
                    packetSbObjClass,
                    packetSbDisplayObjClass,
                    packetSbScoreClass,
                    packetSbTeamClass,
                    sbTeamClass
                )) {
                    if (clazz == null) {
                        continue
                    }
                    val fields = Arrays.stream<Field>(clazz.declaredFields)
                        .filter { field: Field -> !Modifier.isStatic(field.modifiers) }
                        .toArray<Field> { _Dummy_.__Array__() }
                    for (field in fields) {
                        field.isAccessible = true
                    }
                    PACKETS[clazz] = fields
                }
                if (VersionType.V1_8.isHigherOrEqual) {
                    val enumSbActionClass =
                        if (VersionType.V1_13.isHigherOrEqual) "ScoreboardServer\$Action" else "PacketPlayOutScoreboardScore\$EnumScoreboardAction"
                    ENUM_SB_HEALTH_DISPLAY = FastReflection.nmsClass(
                        "world.scores.criteria",
                        "IScoreboardCriteria\$EnumScoreboardHealthDisplay"
                    )
                    ENUM_SB_ACTION = FastReflection.nmsClass("server", enumSbActionClass)
                    ENUM_SB_HEALTH_DISPLAY_INTEGER = FastReflection.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0)
                    ENUM_SB_ACTION_CHANGE = FastReflection.enumValueOf(ENUM_SB_ACTION, "CHANGE", 0)
                    ENUM_SB_ACTION_REMOVE = FastReflection.enumValueOf(ENUM_SB_ACTION, "REMOVE", 1)
                } else {
                    ENUM_SB_HEALTH_DISPLAY = null
                    ENUM_SB_ACTION = null
                    ENUM_SB_HEALTH_DISPLAY_INTEGER = null
                    ENUM_SB_ACTION_CHANGE = null
                    ENUM_SB_ACTION_REMOVE = null
                }
            } catch (t: Throwable) {
                throw ExceptionInInitializerError(t)
            }
        }
    }
}