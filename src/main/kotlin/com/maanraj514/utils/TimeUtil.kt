package com.maanraj514.utils

import com.google.common.base.Preconditions

/**
 * This class converts longs, ints into readable human time.
 * Or it can convert it the other way around.
 */
object TimeUtil {

    /**
     * Format a time into a human-readable format.
     * @param time Time to format
     * @param small Whether to use small format
     * @return Formatted time
     */
    fun format(time: Long, small: Boolean): String {
        Preconditions.checkArgument(time >= 0, "Time cannot be smaller than 0.")
        val toSec = (time / 1000).toInt() % 60
        val toMin = (time / (1000 * 60) % 60).toInt()
        val toHour = (time / (1000 * 60 * 60) % 24).toInt()
        val toDays = (time / (1000 * 60 * 60 * 24)).toInt()
        val dayNotZero = toDays != 0
        val hourNotZero = toHour != 0
        val minuteNotZero = toMin != 0
        val secondNotZero = toSec != 0
        val day = if (small) "d" else if (toDays == 1) "day" else "days"
        val hour = if (small) "h" else if (toHour == 1) "hour" else "hours"
        val minute = if (small) "m" else if (toMin == 1) "minute" else "minutes"
        val second = if (small) "s" else if (toSec == 1) "second" else "seconds"
        val dayFormat = if (dayNotZero) toDays.toString() + (if (small) "" else " ") + day + " " else ""
        val hourFormat = if (hourNotZero) toHour.toString() + (if (small) "" else " ") + hour + " " else ""
        val minuteFormat = if (minuteNotZero) toMin.toString() + (if (small) "" else " ") + minute + " " else ""
        val secondFormat = if (secondNotZero) toSec.toString() + (if (small) "" else " ") + second else ""
        return dayFormat + hourFormat + minuteFormat + secondFormat
    }

    /**
     * Format time from a string.
     * @param input Input string
     * @return Formatted time
     */
    fun fromString(input: String): Long {
        var builder = StringBuilder()
        var seconds = 0
        var minutes = 0
        var hours = 0
        var days = 0
        var weeks = 0
        for (c in input.toCharArray()) {
            if (Character.isDigit(c)) {
                builder.append(c)
            } else {
                when (c) {
                    's' -> {
                        if (builder.isNotEmpty()) {
                            seconds += builder.toString().toInt()
                            builder = StringBuilder()
                        }
                    }

                    'm' -> {
                        if (builder.isNotEmpty()) {
                            minutes += builder.toString().toInt()
                            builder = StringBuilder()
                        }
                    }

                    'h' -> {
                        if (builder.isNotEmpty()) {
                            hours += builder.toString().toInt()
                            builder = StringBuilder()
                        }
                    }

                    'd' -> {
                        if (builder.isNotEmpty()) {
                            days += builder.toString().toInt()
                            builder = StringBuilder()
                        }
                    }

                    'w' -> {
                        if (builder.isNotEmpty()) {
                            weeks += builder.toString().toInt()
                            builder = StringBuilder()
                        }
                    }

                    else -> throw IllegalArgumentException("Not a valid duration format.")
                }
            }
        }
        return 1000L * (seconds + minutes * 60L + hours * 60 * 60L + days * 24 * 60 * 60L + weeks * 7 * 24 * 60 * 60L)
    }
}