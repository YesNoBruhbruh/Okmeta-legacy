package com.maanraj514.utils

import java.text.DecimalFormat

/**
 * Represents a number utility class used to handle numbers, parse them, etc.
 */
object NumberUtil {
    /*
     * Format a number to a fancy format.
     */
    private val FANCY_FORMAT = DecimalFormat("###,###,###,###,###.##")

    /**
     * Parse a number to a fancy format.
     *
     * @param number Number to parse
     * @return Parsed number
     */
    fun decimalFormat(number: Int): String {
        return FANCY_FORMAT.format(number.toLong())
    }

    /**
     * Parse a number to a fancy format.
     *
     * @param number Number to parse
     * @return Parsed number
     */
    fun decimalFormat(number: Long): String {
        return FANCY_FORMAT.format(number)
    }

    /**
     * Parse a number to a fancy format.
     *
     * @param number Number to parse
     * @return Parsed number
     */
    fun decimalFormat(number: Double): String {
        return FANCY_FORMAT.format(number)
    }

    /**
     * Parse a number to a fancy format.
     *
     * @param number Number to parse
     * @return Parsed number
     */
    fun decimalFormat(number: Float): String {
        return FANCY_FORMAT.format(number.toDouble())
    }

    /**
     * Parse a number to a fancy format.
     *
     * @param number Number to parse
     * @return Parsed number
     */
    fun decimalFormat(number: Short): String {
        return FANCY_FORMAT.format(number.toLong())
    }

    /**
     * Parse a number to a fancy format.
     *
     * @param number Number to parse
     * @return Parsed number
     */
    fun decimalFormat(number: Byte): String {
        return FANCY_FORMAT.format(number.toLong())
    }

    /**
     * This method will check if the provided text is a [Integer].
     *
     * @param text Text to check
     * @return If the text is a [Integer]
     */
    fun isInteger(text: String): Boolean {
        return try {
            text.toInt()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * This method will get an [Integer] from the provided text.
     *
     * @param text Text to get the [Integer] from
     * @return [Integer] from the text
     * @throws NullPointerException If the text is not an [Integer]
     */
    fun getInteger(text: String): Int? {
        return if (!isInteger(text)) null else text.toInt()
    }

    /**
     * This method will check if the provided text is a [Long].
     *
     * @param text Text to check
     * @return If the text is a [Long]
     */
    fun isLong(text: String): Boolean {
        return try {
            text.toLong()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * This method will get an [Long] from the provided text.
     *
     * @param text Text to get the [Long] from
     * @return [Long] from the text
     * @throws NullPointerException If the text is not an [Long]
     */
    fun getLong(text: String): Long? {
        return if (!isLong(text)) null else text.toLong()
    }

    /**
     * This method will check if the provided text is a [Double].
     *
     * @param text Text to check
     * @return If the text is a [Double]
     */
    fun isDouble(text: String): Boolean {
        return try {
            text.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * This method will get an [Double] from the provided text.
     *
     * @param text Text to get the [Double] from
     * @return [Double] from the text
     * @throws NullPointerException If the text is not an [Double]
     */
    fun getDouble(text: String): Double? {
        return if (!isDouble(text)) null else text.toDouble()
    }

    /**
     * This method will check if the provided text is a [Float].
     *
     * @param text Text to check
     * @return If the text is a [Float]
     */
    fun isFloat(text: String): Boolean {
        return try {
            text.toFloat()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * This method will get an [Float] from the provided text.
     *
     * @param text Text to get the [Float] from
     * @return [Float] from the text
     * @throws NullPointerException If the text is not an [Float]
     */
    fun getFloat(text: String): Float? {
        return if (!isFloat(text)) null else text.toFloat()
    }

    /**
     * This method will check if the provided text is a [Short].
     *
     * @param text Text to check
     * @return If the text is a [Short]
     */
    fun isShort(text: String): Boolean {
        return try {
            text.toShort()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    /**
     * This method will get an [Short] from the provided text.
     *
     * @param text Text to get the [Short] from
     * @return [Short] from the text
     * @throws NullPointerException If the text is not an [Short]
     */
    fun getShort(text: String): Short? {
        return if (!isShort(text)) null else text.toShort()
    }

    /**
     * This method will convert a number to a fancy version of
     * the provided number such as 1st, 2nd, 3rd, 4th, etc.
     *
     * @param number Number to convert
     * @return Fancy version of the number
     */
    fun fancy(number: Int): String {
        return if (number % 100 >= 11 && number % 100 <= 13) {
            decimalFormat(number) + "th"
        } else when (number % 10) {
            1 -> decimalFormat(number) + "st"
            2 -> decimalFormat(number) + "nd"
            3 -> decimalFormat(number) + "rd"
            else -> decimalFormat(number) + "th"
        }
    }

    /**
     * This method will convert a number to a fancy version of
     * the provided number such as 1st, 2nd, 3rd, 4th, etc.
     *
     * @param number Number to convert
     * @return Fancy version of the number
     */
    fun fancy(number: Long): String {
        return if (number % 100 >= 11 && number % 100 <= 13) {
            decimalFormat(number) + "th"
        } else when ((number % 10).toInt()) {
            1 -> decimalFormat(number) + "st"
            2 -> decimalFormat(number) + "nd"
            3 -> decimalFormat(number) + "rd"
            else -> decimalFormat(number) + "th"
        }
    }

    /**
     * This method will convert a number to a fancy version of
     * the provided number such as 1st, 2nd, 3rd, 4th, etc.
     *
     * @param number Number to convert
     * @return Fancy version of the number
     */
    fun fancy(number: Double): String {
        return if (number % 100 >= 11 && number % 100 <= 13) {
            decimalFormat(number) + "th"
        } else when ((number % 10).toInt()) {
            1 -> decimalFormat(number) + "st"
            2 -> decimalFormat(number) + "nd"
            3 -> decimalFormat(number) + "rd"
            else -> decimalFormat(number) + "th"
        }
    }

    /**
     * This method will convert a number to a fancy version of
     * the provided number such as 1st, 2nd, 3rd, 4th, etc.
     *
     * @param number Number to convert
     * @return Fancy version of the number
     */
    fun fancy(number: Float): String {
        return if (number % 100 >= 11 && number % 100 <= 13) {
            number.toString() + "th"
        } else when ((number % 10).toInt()) {
            1 -> decimalFormat(number) + "st"
            2 -> decimalFormat(number) + "nd"
            3 -> decimalFormat(number) + "rd"
            else -> decimalFormat(number) + "th"
        }
    }

    /**
     * This method will convert a number to a fancy version of
     * the provided number such as 1st, 2nd, 3rd, 4th, etc.
     *
     * @param number Number to convert
     * @return Fancy version of the number
     */
    fun fancy(number: Short): String {
        return if (number % 100 >= 11 && number % 100 <= 13) {
            decimalFormat(number) + "th"
        } else when (number % 10) {
            1 -> decimalFormat(number) + "st"
            2 -> decimalFormat(number) + "nd"
            3 -> decimalFormat(number) + "rd"
            else -> decimalFormat(number) + "th"
        }
    }

    /**
     * This method will convert a number to a fancy version of
     * the provided number such as 1st, 2nd, 3rd, 4th, etc.
     *
     * @param number Number to convert
     * @return Fancy version of the number
     */
    fun fancy(number: Byte): String {
        return if (number % 100 >= 11 && number % 100 <= 13) {
            decimalFormat(number) + "th"
        } else when (number % 10) {
            1 -> decimalFormat(number) + "st"
            2 -> decimalFormat(number) + "nd"
            3 -> decimalFormat(number) + "rd"
            else -> decimalFormat(number) + "th"
        }
    }
}