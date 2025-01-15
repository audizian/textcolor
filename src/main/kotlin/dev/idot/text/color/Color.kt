//@file:Suppress("unused")

package dev.idot.text.color

import dev.idot.text.color.CodedColor.Companion.nearestCode
import kotlin.math.roundToInt

data class Color(val value: Int) {
    val rgb: Int = value.coerceIn(0, 0xFFFFFF)
    override fun toString(): String = "%06X".format(rgb)

    fun red():    Int = rgb shr 16 and 0xFF
    fun green():  Int = rgb shr 8 and 0xFF
    fun blue():   Int = rgb and 0xFF

    constructor(red: Int, green: Int, blue: Int) : this(
        (red.coerceIn(0, 255) shl 16) or
        (green.coerceIn(0, 255) shl 8) or
        blue.coerceIn(0, 255)
    )

    @Suppress("NOTHING_TO_INLINE")
    inline fun distance(rgb: Int): Int = distance(Color(rgb))

    fun distance(color: Color): Int =
        (color.red() - red()).let { it * it } +
        (color.green() - green()).let { it * it } +
        (color.blue() - blue()).let { it * it }

    fun hexMojang(): String {
        val result = StringBuilder(14).append(SECTION).append("x")
        for (c in toString()) result.append(SECTION).append(c)
        return result.toString().lowercase()
    }

    fun hexBukkit(): String {
        val result = StringBuilder(14).append("&x")
        for (c in toString()) result.append("&").append(c)
        return result.toString().lowercase()
    }

    fun hexMinify(): String = nearestCode().let {
        if (it.rgb == rgb) "$SECTION${it.code}" else hexMojang()
    }

    fun interpolate(color: Color, factor: Double): Color = factor.coerceIn(0.0, 1.0).let {
        Color(
            (red()  * (1 - it) + color.red()    * it).roundToInt(),
            (green()* (1 - it) + color.green()  * it).roundToInt(),
            (blue() * (1 - it) + color.blue()   * it).roundToInt()
        )
    }

    fun interpolateToList(color: Color, steps: Int): List<Color> =
        (0..steps).map { interpolate(color, it.toDouble() / steps) }

    companion object {
        operator fun get(value: String): Color? = value.fromMojangColor() ?: value.fromStrictHexCode() ?: NamedColor[value]

        fun String.fromHexOrNamed(): Color? = fromStrictHexCode() ?: NamedColor[this]

        fun String.fromMojangColor(): Color? = mojangColorRegex.find(this)?.value?.run {
            when (length) {
                2 -> CodedColor[last()]?.invoke()
                14 -> Color(substring(2).replace(SECTION.toString(), "").toInt(16))
                else -> null
            }
        }

        private fun String.triToHex(): String {
            val result = StringBuilder(6)
            for (c in this) result.append(c).append(c)
            return result.toString()
        }

        /**
         * @return The [Color] object or null, if invalid.
         */
        fun String.fromStrictHexCode(): Color? {
            return when (length) {
                3 -> triToHex()
                4 -> substring(1).triToHex()
                6 -> this
                7 -> substring(1)
                else -> return null
            }.runCatching { Color(toInt(16)) }.getOrNull()
        }
    }
}
