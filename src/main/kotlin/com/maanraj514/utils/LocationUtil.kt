package com.maanraj514.utils

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.configuration.ConfigurationSection

/**
 * This class is used for ease of use of locations.
 */
class LocationUtil {

    companion object {

        /**
         * Save a location to a section
         * in config.
         *
         * @param location the location to be saved.
         * @param section the configurationSection for the location to be put in.
         */
        fun saveLocation(location: Location, section: ConfigurationSection){
            section.set("worldName", location.world.name)
            section.set("x", location.x)
            section.set("y", location.y)
            section.set("z", location.z)
            section.set("yaw", location.yaw)
            section.set("pitch", location.pitch)
        }

        /**
         * Gets location from a configurationSection.
         *
         * @param section the configurationSection to read the location off of.
         * @return new location from section.
         */
        fun readLocation(section: ConfigurationSection) : Location {
            return Location(
                Bukkit.getWorld(section.getString("worldName")!!),
                section.getDouble("x"),
                section.getDouble("y"),
                section.getDouble("z"),
                section.getDouble("yaw").toFloat(),
                section.getDouble("pitch").toFloat()
            )
        }

        /**
         * Converts a Location to a String.
         *
         * @param location the location to be converted.
         * @param includeExtra if true, includes yaw and pitch if false, not.
         * @return the location in string.
         */
        fun locationToString(location: Location, includeExtra: Boolean): String {
            val world = location.world.name
            val x = location.x
            val y = location.y
            val z = location.z
            val yaw = location.yaw
            val pitch = location.pitch

            return if (includeExtra) "$world:$x:$y:$z:$yaw:$pitch" else "$world:$x:$y:$z"
        }

        /**
         * Converts a String to a Location.
         *
         * @param location the location to be created
         * @return the string in location.
         */
        fun stringToLocation(location: String): Location? {

            if (location.trim { it <= ' ' } == "") {
                return null
            }

            val parts = location.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            // this means location without yaw or pitch.

            val world: World?
            val x: Double
            val y: Double
            val z: Double
            if (parts.size == 4) {

                world = Bukkit.getWorld(parts[0])
                x = parts[1].toDouble()
                y = parts[2].toDouble()
                z = parts[3].toDouble()
                return Location(world, x, y, z)

            } else if (parts.size == 6) {
                // this means location with yaw and pitch.

                world = Bukkit.getWorld(parts[0])
                x = parts[1].toDouble()
                y = parts[2].toDouble()
                z = parts[3].toDouble()
                val yaw = parts[4].toFloat()
                val pitch = parts[5].toFloat()
                return Location(world, x, y, z, yaw, pitch)
            }
            return null
        }

        /**
         * Gets all the locations
         * inside the invisible
         * cuboid.
         *
         * @param location1 the first location.
         * @param location2 the second location.
         * @return all the locations in the invisible cuboid.
         */
        fun locationsFromTwoPoints(location1: Location, location2: Location): List<Location> {
            val locations: MutableList<Location> = ArrayList()

            val topBlockX = location1.blockX.coerceAtLeast(location2.blockX)
            val bottomBlockX = location1.blockX.coerceAtMost(location2.blockX)
            val topBlockY = location1.blockY.coerceAtLeast(location2.blockY)
            val bottomBlockY = location1.blockY.coerceAtMost(location2.blockY)
            val topBlockZ = location1.blockZ.coerceAtLeast(location2.blockZ)
            val bottomBlockZ = location1.blockZ.coerceAtMost(location2.blockZ)

            for (x in bottomBlockX..topBlockX) {
                for (z in bottomBlockZ..topBlockZ) {
                    for (y in bottomBlockY..topBlockY) {
                        locations.add(location1.world.getBlockAt(x, y, z).location)
                    }
                }
            }
            return locations
        }
    }
}