package com.maanraj514.nms

import com.maanraj514.nms.FastReflection.PacketConstructor
import org.bukkit.Bukkit
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.util.*
import java.util.function.Predicate

/**
 * Small reflection utility class to use CraftBukkit and NMS.
 *
 * @author MrMicky
 */
class FastReflection private constructor() {
    init {
        throw UnsupportedOperationException()
    }

    fun interface PacketConstructor {
        @Throws(Throwable::class)
        operator fun invoke(): Any?
    }

    companion object {
        const val OBC_PACKAGE = "org.bukkit.craftbukkit"
        val VERSION = Bukkit.getServer().javaClass.getPackage().name.substring(OBC_PACKAGE.length + 1)
        private const val NM_PACKAGE = "net.minecraft"
        const val NMS_PACKAGE = NM_PACKAGE + ".server"
        private val VOID_METHOD_TYPE = MethodType.methodType(Void.TYPE)
        val isRepackaged = optionalClass(NM_PACKAGE + ".network.protocol.Packet").isPresent

        @Volatile
        private var theUnsafe: Any? = null
        fun nmsClassName(post1_17package: String?, className: String): String {
            if (isRepackaged) {
                val classPackage = if (post1_17package == null) NM_PACKAGE else NM_PACKAGE + '.' + post1_17package
                return "$classPackage.$className"
            }
            return NMS_PACKAGE + '.' + VERSION + '.' + className
        }

        @Throws(ClassNotFoundException::class)
        fun nmsClass(post1_17package: String?, className: String): Class<*> {
            return Class.forName(nmsClassName(post1_17package, className))
        }

        fun nmsOptionalClass(post1_17package: String?, className: String): Optional<Class<*>> {
            return optionalClass(nmsClassName(post1_17package, className))
        }

        fun obcClassName(className: String): String {
            return OBC_PACKAGE + '.' + VERSION + '.' + className
        }

        @Throws(ClassNotFoundException::class)
        fun obcClass(className: String): Class<*> {
            return Class.forName(obcClassName(className))
        }

        fun obcOptionalClass(className: String): Optional<Class<*>> {
            return optionalClass(obcClassName(className))
        }

        fun optionalClass(className: String?): Optional<Class<*>> {
            return try {
                Optional.of(Class.forName(className))
            } catch (e: ClassNotFoundException) {
                Optional.empty()
            }
        }

        fun enumValueOf(enumClass: Class<*>, enumName: String?): Any {
            return java.lang.Enum.valueOf(enumClass.asSubclass(Enum::class.java), enumName)
        }

        fun enumValueOf(enumClass: Class<*>, enumName: String?, fallbackOrdinal: Int): Any {
            return try {
                enumValueOf(enumClass, enumName)
            } catch (e: IllegalArgumentException) {
                val constants: Array<out Any>? = enumClass.enumConstants
                if (constants != null) {
                    if (constants.size > fallbackOrdinal) {
                        return constants[fallbackOrdinal]
                    }
                }
                throw e
            }
        }

        @Throws(ClassNotFoundException::class)
        fun innerClass(parentClass: Class<*>, classPredicate: Predicate<Class<*>?>): Class<*>? {
            for (innerClass in parentClass.declaredClasses) {
                if (classPredicate.test(innerClass)) {
                    return innerClass
                }
            }
            throw ClassNotFoundException("No class in " + parentClass.canonicalName + " matches the predicate.")
        }

        @Throws(Exception::class)
        fun findPacketConstructor(packetClass: Class<*>?, lookup: MethodHandles.Lookup): PacketConstructor {
            try {
                val constructor = lookup.findConstructor(packetClass, VOID_METHOD_TYPE)
                return PacketConstructor { constructor.invoke() }
            } catch (e: NoSuchMethodException) {
                // try below with Unsafe
            } catch (_: IllegalAccessException) {
            }
            if (theUnsafe == null) {
                synchronized(FastReflection::class.java) {
                    if (theUnsafe == null) {
                        val unsafeClass = Class.forName("sun.misc.Unsafe")
                        val theUnsafeField = unsafeClass.getDeclaredField("theUnsafe")
                        theUnsafeField.isAccessible = true
                        theUnsafe = theUnsafeField[null]
                    }
                }
            }
            val allocateMethodType = MethodType.methodType(Any::class.java, Class::class.java)
            val allocateMethod = lookup.findVirtual(theUnsafe!!.javaClass, "allocateInstance", allocateMethodType)
            return PacketConstructor { allocateMethod.invoke(theUnsafe, packetClass) }
        }
    }
}