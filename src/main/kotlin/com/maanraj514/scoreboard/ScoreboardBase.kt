package com.maanraj514.scoreboard

import com.maanraj514.scoreboard.ScoreboardReflection.PacketConstructor
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.lang.invoke.MethodHandle
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.reflect.Field
import java.lang.reflect.Method
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
abstract class ScoreboardBase<T> protected constructor(player: Player) {
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
    private val lines: MutableList<T> = ArrayList()

    /**
     * Get the scoreboard title.
     *
     * @return the scoreboard title
     */
    var title = emptyLine()
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
        id = "fb-" + Integer.toHexString(ThreadLocalRandom.current().nextInt())
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
    open fun updateTitle(title: T) {
        if (this.title == Objects.requireNonNull(title, "title")) {
            return
        }
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
    fun getLines(): List<T> {
        return ArrayList(lines)
    }

    /**
     * Get the specified scoreboard line.
     *
     * @param line the line number
     * @return the line
     * @throws IndexOutOfBoundsException if the line is higher than `size`
     */
    fun getLine(line: Int): T {
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
    fun updateLine(line: Int, text: T) {
        checkLineNumber(line, false, true)
        try {
            if (line < size()) {
                lines[line] = text
                sendLineChange(getScoreByLine(line))
                return
            }
            val newLines: MutableList<T> = ArrayList(lines)
            if (line > size()) {
                for (i in size() until line) {
                    newLines.add(emptyLine())
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
        val newLines: MutableList<T> = lines.toMutableList()
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
    open fun updateLines(vararg lines: T) {
        updateLines(listOf(*lines))
    }

    /**
     * Update the lines of the scoreboard
     *
     * @param lines the new scoreboard lines
     * @throws IllegalArgumentException if one line is longer than 30 chars on 1.12 or lower
     * @throws IllegalStateException    if [.delete] was call before
     */
    @Synchronized
    fun updateLines(lines: Collection<T>) {
        Objects.requireNonNull(lines, "lines")
        checkLineNumber(lines.size, false, true)
        val oldLines: MutableList<T> = lines.toMutableList()
        this.lines.clear()
        this.lines.addAll(lines)
        val linesSize = this.lines.size
        try {
            if (oldLines.size != linesSize) {
                val oldLinesCopy: List<T> = ArrayList(oldLines)
                if (oldLines.size > linesSize) {
                    for (i in oldLinesCopy.size downTo linesSize + 1) {
                        sendTeamPacket(i - 1, TeamMode.REMOVE)
                        sendScorePacket(i - 1, ScoreboardAction.REMOVE)
                        oldLines.removeAt(0)
                    }
                } else {
                    for (i in oldLinesCopy.size until linesSize) {
                        sendScorePacket(i, ScoreboardAction.CHANGE)
                        sendTeamPacket(i, TeamMode.CREATE, null, null)
                    }
                }
            }
            for (i in 0 until linesSize) {
                if (getLineByScore(oldLines, i) != getLineByScore(i)) {
                    sendLineChange(i)
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
     * After this, all uses of [.updateLines] and [.updateTitle] will throw an [IllegalStateException]
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

    @Throws(Throwable::class)
    protected abstract fun sendLineChange(score: Int)
    @Throws(Throwable::class)
    protected abstract fun toMinecraftComponent(value: T?): Any?
    protected abstract fun emptyLine(): T
    private fun checkLineNumber(line: Int, checkInRange: Boolean, checkMax: Boolean) {
        require(line >= 0) { "Line number must be positive" }
        require(!(checkInRange && line >= lines.size)) { "Line number must be under " + lines.size }
        require(!(checkMax && line >= COLOR_CODES.size - 1)) { "Line number is too high: $line" }
    }

    protected fun getScoreByLine(line: Int): Int {
        return lines.size - line - 1
    }

    protected fun getLineByScore(score: Int): T? {
        return getLineByScore(lines, score)
    }

    protected fun getLineByScore(lines: List<T>, score: Int): T? {
        return if (score < lines.size) lines[lines.size - score - 1] else null
    }

    @Throws(Throwable::class)
    protected fun sendObjectivePacket(mode: ObjectiveMode) {
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
    protected fun sendDisplayObjectivePacket() {
        val packet = PACKET_SB_DISPLAY_OBJ!!.invoke()
        setField(packet, Int::class.javaPrimitiveType, 1) // Position (1: sidebar)
        setField(packet, String::class.java, id) // Score Name
        sendPacket(packet)
    }

    @Throws(Throwable::class)
    protected fun sendScorePacket(score: Int, action: ScoreboardAction) {
        val packet = PACKET_SB_SCORE!!.invoke()
        setField(packet, String::class.java, COLOR_CODES[score], 0) // Player Name
        if (VersionType.V1_8.isHigherOrEqual) {
            val enumAction = if (action == ScoreboardAction.REMOVE) ENUM_SB_ACTION_REMOVE else ENUM_SB_ACTION_CHANGE
            setField(packet, ENUM_SB_ACTION, enumAction)
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
    protected fun sendTeamPacket(score: Int, mode: TeamMode, prefix: T? = null, suffix: T? = null) {
        if (mode == TeamMode.ADD_PLAYERS || mode == TeamMode.REMOVE_PLAYERS) {
            throw UnsupportedOperationException()
        }
        val packet = PACKET_SB_TEAM!!.invoke()
        setField(packet, String::class.java, "$id:$score") // Team name
        setField(
            packet,
            Int::class.javaPrimitiveType,
            mode.ordinal,
            if (VERSION_TYPE == VersionType.V1_8) 1 else 0
        ) // Update mode
        if (mode == TeamMode.REMOVE) {
            sendPacket(packet)
            return
        }
        if (VersionType.V1_17.isHigherOrEqual) {
            val team = PACKET_SB_SERIALIZABLE_TEAM!!.invoke()
            // Since the packet is initialized with null values, we need to change more things.
            setComponentField(team, null, 0) // Display name
            setField(team, CHAT_FORMAT_ENUM, RESET_FORMATTING) // Color
            setComponentField(team, prefix, 1) // Prefix
            setComponentField(team, suffix, 2) // Suffix
            setField(team, String::class.java, "always", 0) // Visibility
            setField(team, String::class.java, "always", 1) // Collisions
            setField(packet, Optional::class.java, Optional.of(team))
        } else {
            setComponentField(packet, prefix, 2) // Prefix
            setComponentField(packet, suffix, 3) // Suffix
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
        sendPacket(packet)
    }

    @Throws(Throwable::class)
    private fun sendPacket(packet: Any) {
        check(!isDeleted) { "This FastBoard is deleted" }
        if (player.isOnline) {
            val entityPlayer = PLAYER_GET_HANDLE!!.invoke(player)
            val playerConnection = PLAYER_CONNECTION!!.invoke(entityPlayer)
            SEND_PACKET!!.invoke(playerConnection, packet)
        }
    }

    @Throws(ReflectiveOperationException::class)
    private fun setField(`object`: Any, fieldType: Class<*>?, value: Any?) {
        setField(`object`, fieldType, value, 0)
    }

    @Throws(ReflectiveOperationException::class)
    private fun setField(packet: Any, fieldType: Class<*>?, value: Any?, count: Int) {
        var i = 0
        for (field in PACKETS[packet.javaClass]!!) {
            if (field.type == fieldType && count == i++) {
                field[packet] = value
            }
        }
    }

    @Throws(Throwable::class)
    private fun setComponentField(packet: Any, value: T?, count: Int) {
        if (!VersionType.V1_13.isHigherOrEqual) {
            setField(packet, String::class.java, value ?: "", count)
            return
        }
        var i = 0
        for (field in PACKETS[packet.javaClass]!!) {
            if ((field.type == String::class.java || field.type == CHAT_COMPONENT_CLASS) && count == i++) {
                field[packet] = toMinecraftComponent(value)
            }
        }
    }

    enum class ObjectiveMode {
        CREATE, REMOVE, UPDATE
    }

    enum class TeamMode {
        CREATE, REMOVE, UPDATE, ADD_PLAYERS, REMOVE_PLAYERS
    }

    enum class ScoreboardAction {
        CHANGE, REMOVE
    }

    internal enum class VersionType {
        V1_7, V1_8, V1_13, V1_17;

        val isHigherOrEqual: Boolean
            get() = VERSION_TYPE!!.ordinal >= ordinal
    }

    companion object {
        private val PACKETS: MutableMap<Class<*>, Array<Field>> = HashMap(8)
        @JvmStatic
        protected val COLOR_CODES = ChatColor.values().map { it.toString() }.toTypedArray()
        private var VERSION_TYPE: VersionType? = null

        // Packets and components
        private var CHAT_COMPONENT_CLASS: Class<*>? = null
        private var CHAT_FORMAT_ENUM: Class<*>? = null
        private var RESET_FORMATTING: Any? = null
        private var PLAYER_CONNECTION: MethodHandle? = null
        private var SEND_PACKET: MethodHandle? = null
        private var PLAYER_GET_HANDLE: MethodHandle? = null

        // Scoreboard packets
        private var PACKET_SB_OBJ: PacketConstructor? = null
        private var PACKET_SB_DISPLAY_OBJ: PacketConstructor? = null
        private var PACKET_SB_SCORE: PacketConstructor? = null
        private var PACKET_SB_TEAM: PacketConstructor? = null
        private var PACKET_SB_SERIALIZABLE_TEAM: PacketConstructor? = null

        // Scoreboard enums
        private var ENUM_SB_HEALTH_DISPLAY: Class<*>? = null
        private var ENUM_SB_ACTION: Class<*>? = null
        private var ENUM_SB_HEALTH_DISPLAY_INTEGER: Any? = null
        private var ENUM_SB_ACTION_CHANGE: Any? = null
        private var ENUM_SB_ACTION_REMOVE: Any? = null

        init {
            try {
                val lookup = MethodHandles.lookup()
                VERSION_TYPE = if (ScoreboardReflection.isRepackaged()) {
                    VersionType.V1_17
                } else if (ScoreboardReflection.nmsOptionalClass(null, "ScoreboardServer\$Action").isPresent) {
                    VersionType.V1_13
                } else if (ScoreboardReflection.nmsOptionalClass(
                        null,
                        "IScoreboardCriteria\$EnumScoreboardHealthDisplay"
                    ).isPresent
                ) {
                    VersionType.V1_8
                } else {
                    VersionType.V1_7
                }
                val gameProtocolPackage = "network.protocol.game"
                val craftPlayerClass = ScoreboardReflection.obcClass("entity.CraftPlayer")
                val entityPlayerClass = ScoreboardReflection.nmsClass("server.level", "EntityPlayer")
                val playerConnectionClass = ScoreboardReflection.nmsClass("server.network", "PlayerConnection")
                val packetClass = ScoreboardReflection.nmsClass("network.protocol", "Packet")
                val packetSbObjClass =
                    ScoreboardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardObjective")
                val packetSbDisplayObjClass =
                    ScoreboardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardDisplayObjective")
                val packetSbScoreClass =
                    ScoreboardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardScore")
                val packetSbTeamClass =
                    ScoreboardReflection.nmsClass(gameProtocolPackage, "PacketPlayOutScoreboardTeam")
                val sbTeamClass =
                    if (VersionType.V1_17.isHigherOrEqual) ScoreboardReflection.innerClass(packetSbTeamClass) { innerClass: Class<*> -> !innerClass.isEnum } else null
                val playerConnectionField = Arrays.stream(entityPlayerClass.fields)
                    .filter { field: Field -> field.type.isAssignableFrom(playerConnectionClass) }
                    .findFirst().orElseThrow { NoSuchFieldException() }
                val sendPacketMethod = Arrays.stream(playerConnectionClass.methods)
                    .filter { m: Method -> m.parameterCount == 1 && m.parameterTypes[0] == packetClass }
                    .findFirst().orElseThrow { NoSuchMethodException() }
                CHAT_COMPONENT_CLASS = ScoreboardReflection.nmsClass("network.chat", "IChatBaseComponent")
                CHAT_FORMAT_ENUM = ScoreboardReflection.nmsClass(null, "EnumChatFormat")
                RESET_FORMATTING = ScoreboardReflection.enumValueOf(CHAT_FORMAT_ENUM, "RESET", 21)
                PLAYER_GET_HANDLE =
                    lookup.findVirtual(craftPlayerClass, "getHandle", MethodType.methodType(entityPlayerClass))
                PLAYER_CONNECTION = lookup.unreflectGetter(playerConnectionField)
                SEND_PACKET = lookup.unreflect(sendPacketMethod)
                PACKET_SB_OBJ = ScoreboardReflection.findPacketConstructor(packetSbObjClass, lookup)
                PACKET_SB_DISPLAY_OBJ = ScoreboardReflection.findPacketConstructor(packetSbDisplayObjClass, lookup)
                PACKET_SB_SCORE = ScoreboardReflection.findPacketConstructor(packetSbScoreClass, lookup)
                PACKET_SB_TEAM = ScoreboardReflection.findPacketConstructor(packetSbTeamClass, lookup)
                PACKET_SB_SERIALIZABLE_TEAM =
                    if (sbTeamClass == null) null else ScoreboardReflection.findPacketConstructor(sbTeamClass, lookup)
                for (clazz in listOf(
                    packetSbObjClass,
                    packetSbDisplayObjClass,
                    packetSbScoreClass,
                    packetSbTeamClass,
                    sbTeamClass
                )) {
                    if (clazz == null) {
                        continue
                    }
                    val fields = clazz.declaredFields.filter {
                        !Modifier.isStatic(it.modifiers) }.toTypedArray()
                    for (field in fields) {
                        field.isAccessible = true
                    }
                    PACKETS[clazz] = fields
                }
                if (VersionType.V1_8.isHigherOrEqual) {
                    val enumSbActionClass =
                        if (VersionType.V1_13.isHigherOrEqual) "ScoreboardServer\$Action" else "PacketPlayOutScoreboardScore\$EnumScoreboardAction"
                    ENUM_SB_HEALTH_DISPLAY = ScoreboardReflection.nmsClass(
                        "world.scores.criteria",
                        "IScoreboardCriteria\$EnumScoreboardHealthDisplay"
                    )
                    ENUM_SB_ACTION = ScoreboardReflection.nmsClass("server", enumSbActionClass)
                    ENUM_SB_HEALTH_DISPLAY_INTEGER =
                        ScoreboardReflection.enumValueOf(ENUM_SB_HEALTH_DISPLAY, "INTEGER", 0)
                    ENUM_SB_ACTION_CHANGE = ScoreboardReflection.enumValueOf(ENUM_SB_ACTION, "CHANGE", 0)
                    ENUM_SB_ACTION_REMOVE = ScoreboardReflection.enumValueOf(ENUM_SB_ACTION, "REMOVE", 1)
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