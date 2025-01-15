/**
 * "Codes" ("Color Codes" or "Hex Codes") are minecraft-specific coded colors
 * "Colors" ("Hex Colors") are standard hex colors, named [NamedColor], or [Color] objects
 * I did my best, terminology is hard, anyways...
 */

@file:Suppress("NOTHING_TO_INLINE", "unused")

package dev.idot.text.color

import dev.idot.text.color.Color
import dev.idot.text.color.Color.Companion.fromHexOrNamed
import dev.idot.text.color.Color.Companion.fromMojangColor
import dev.idot.text.color.Color.Companion.fromStrictHexCode
import kotlin.text.RegexOption.IGNORE_CASE

const val SECTION = '\u00a7'

@JvmInline value class CustomDelimiter(val value: Char) {
    init {
        require(!value.isLetterOrDigit() || !value.isWhitespace() || value != SECTION)
        { "'$value' cannot be used as a custom delimiter character." }
    }
    override fun toString(): String = value.toString()
}

/**
 * @return the custom delimiter for color codes in this class (default: '&')
 */
var codePrefix = CustomDelimiter('&')

val mojangCodeRegex = Regex("&x(?:&[0-9a-f]){6}|&[0-9a-fk-or]".replace('&', SECTION), IGNORE_CASE)
val mojangColorRegex = Regex("&x(?:&[0-9a-f]){6}|&[0-9a-f]".replace('&', SECTION), IGNORE_CASE)

const val namedColorPattern = "[0-9a-z]{3,}?"
val namedColorRegex = Regex("\\{#($namedColorPattern)}", IGNORE_CASE)
val namedColorSeparatorRegex = Regex("\\{#($namedColorPattern)<>}", IGNORE_CASE)
val namedColorGradientRegex = Regex("\\{#($namedColorPattern)>}(.*?)\\{#($namedColorPattern)<}", IGNORE_CASE)
val namedColorGradientListRegex = Regex("\\{#(?:$namedColorPattern,)*?$namedColorPattern,?}", IGNORE_CASE)

/**
 * @param delimiter for color code (default: [codePrefix])
 * @return the [Regex] for color codes ("&C")
 */
fun colorCodeRegex(delimiter: CustomDelimiter = codePrefix): Regex =
    Regex("[$SECTION$delimiter]([0-9a-f])", IGNORE_CASE)

/**
 * @param delimiter for format code (default: [codePrefix])
 * @return the [Regex] for format codes ("&k-o" and "&r")
 */
fun formatCodeRegex(delimiter: CustomDelimiter = codePrefix): Regex =
    Regex("[$SECTION$delimiter]([k-or])", IGNORE_CASE)

/**
 * @param delimiter for color code (default: [codePrefix])
 * @return the [Regex] for bukkit hex codes ("&x&R&R&G&G&B&B" and "&x&R&G&B")
 */
fun bukkitHexRegex(delimiter: CustomDelimiter = codePrefix) =
    Regex("&x(?:(?:&[0-9a-f]){3}){1,2}".replace('&', delimiter.value), IGNORE_CASE)

/**
 * @param delimiter for color code (default: [codePrefix]). If null, no delimiter is used
 * @return the [Regex] for hex codes ("&amp;#RRGGBB" and "&amp;#RGB")
 */
inline fun hexCodeRegex(delimiter: CustomDelimiter? = codePrefix): Regex =
    Regex("${delimiter ?: ""}#${if (delimiter != null) "" else "?"}((?:[0-9a-f]{3}){1,2})", IGNORE_CASE)

/**
 * Does not remove gradient codes; run [convertCmiGradients] first
 *
 * @param delimiter for color code (default: [codePrefix])
 * @return the [String] without any color codes ("&C") and hex codes ("&#RRGGBB" and "&#RGB")
 */
fun String.stripColors(delimiter: CustomDelimiter = codePrefix): String =
    stripNamedColors()
    .stripHexCodes(delimiter)
    .stripBukkitHexCodes(delimiter)
    .stripColorCodes(delimiter)
    .stripFormatCodes(delimiter)
    .stripMojangCodes()
inline fun <T : Iterable<String>> T.strip(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.stripColors(delimiter) }
inline fun Array<String>.strip(delimiter: CustomDelimiter = codePrefix) =
    map { it.stripColors(delimiter) }.toTypedArray()

fun String.stripBukkitCodes(delimiter: CustomDelimiter = codePrefix) =
    stripBukkitHexCodes(delimiter).stripColorCodes(delimiter).stripFormatCodes(delimiter)
inline fun <T : Iterable<String>> T.stripBukkitCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.stripBukkitCodes(delimiter) }
inline fun Array<String>.stripBukkitCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.stripBukkitCodes(delimiter) }.toTypedArray()

/**
 * @param delimiter for color code (default: [codePrefix])
 * @return the string without any format codes ("&k-o" and "&r")
 */
fun String.stripFormatCodes(delimiter: CustomDelimiter = codePrefix): String =
    formatCodeRegex(delimiter).replace(this, "")
inline fun <T : Iterable<String>> T.stripFormatCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.stripFormatCodes(delimiter) }
inline fun Array<String>.stripFormatCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.stripFormatCodes(delimiter) }.toTypedArray()

fun String.stripNamedColors(): String =
    replace(namedColorRegex) { if (it.groupValues[1].fromHexOrNamed() != null) "" else it.value }
inline fun <T : Iterable<String>> T.stripNamedColors(): List<String> =
    map { it.stripNamedColors() }
inline fun Array<String>.stripNamedColors() =
    map { it.stripNamedColors() }.toTypedArray()

fun String.stripHexCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(hexCodeRegex(delimiter), "")
inline fun <T : Iterable<String>> T.stripHexCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.stripHexCodes(delimiter) }
inline fun Array<String>.stripHexCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.stripHexCodes(delimiter) }.toTypedArray()

fun String.stripBukkitHexCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(bukkitHexRegex(delimiter), "")
inline fun <T : Iterable<String>> T.stripBukkitHexCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.stripBukkitHexCodes(delimiter) }
inline fun Array<String>.stripBukkitHexCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.stripBukkitHexCodes(delimiter) }.toTypedArray()

fun String.stripColorCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(colorCodeRegex(delimiter), "")
inline fun <T : Iterable<String>> T.stripColorCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.stripColorCodes(delimiter) }
inline fun Array<String>.stripColorCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.stripColorCodes(delimiter) }.toTypedArray()

fun String.stripMojangCodes(): String =
    replace(mojangCodeRegex, "")
inline fun <T : Iterable<String>> T.stripMojangCodes(): List<String> =
    map { it.stripMojangCodes() }
inline fun Array<String>.stripMojangCodes() =
    map { it.stripMojangCodes() }.toTypedArray()

/**
 * @param delimiter for color code (default: [codePrefix])
 * @return the [String] with all color codes ("&C") converted to mojang color codes ("§C")
 */
fun String.convertColorsAndFormat(delimiter: CustomDelimiter = codePrefix): String =
    convertBukkitColors(delimiter).convertColorCodes(delimiter).convertFormatCodes(delimiter)
inline fun <T : Iterable<String>> T.convertColorsAndFormat(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.convertColorsAndFormat(delimiter) }
inline fun Array<String>.convertColorsAndFormat(delimiter: CustomDelimiter = codePrefix) =
    map { it.convertColorsAndFormat(delimiter) }.toTypedArray()

fun String.convertBukkitColors(delimiter: CustomDelimiter = codePrefix): String =
    replace(bukkitHexRegex(delimiter)) { match ->
    val hex = match.value
    when (hex.length) {
        8 -> StringBuilder(14).append(SECTION).append("x").apply {
            for (c in hex.drop(3)) {
                if (c == delimiter.value) continue
                append(SECTION).append(c).append(SECTION).append(c)
            }
        }
        14 -> hex.replace(delimiter.value, SECTION)
        else -> hex
    }
}
inline fun <T : Iterable<String>> T.convertBukkitColors(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.convertBukkitColors(delimiter) }
inline fun Array<String>.convertBukkitColors(delimiter: CustomDelimiter = codePrefix) =
    map { it.convertBukkitColors(delimiter) }.toTypedArray()

fun String.convertColorCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(colorCodeRegex(delimiter), "$SECTION$1")
inline fun <T : Iterable<String>> T.convertColorCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.convertColorCodes(delimiter) }
inline fun Array<String>.convertColorCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.convertColorCodes(delimiter) }.toTypedArray()

fun String.convertFormatCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(formatCodeRegex(delimiter), "$SECTION$1")
inline fun <T : Iterable<String>> T.convertFormatCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.convertFormatCodes(delimiter) }
inline fun Array<String>.convertFormatCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.convertFormatCodes(delimiter) }.toTypedArray()

/**
 * @param delimiter for color code (default: [codePrefix])
 * @return the [String] with all hex codes ("&amp;#RRGGBB" and "&amp;#RGB") converted
 * to mojang hex codes ("§x§R§R§G§G§B§B")
 */
fun String.convertHexCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(hexCodeRegex(delimiter)) { it.groupValues[1].fromStrictHexCode()?.hexMojang() ?: it.value }
inline fun <T : Iterable<String>> T.convertHexCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.convertHexCodes(delimiter) }
inline fun Array<String>.convertHexCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.convertHexCodes(delimiter) }.toTypedArray()

/**
 * @return the [String] with all [NamedColor] codes ("{#RRGGBB}", "{#RGB}" or "{#COLORNAME}") converted
 * to minified mojang color codes ("&C" or "§x§R§R§G§G§B§B")
 */
fun String.convertNamedColors(): String =
    replace(namedColorRegex) { it.groupValues[1].fromHexOrNamed()?.hexMojang() ?: it.value }
inline fun <T : Iterable<String>> T.convertNamedColors(): List<String> =
    map { it.convertNamedColors() }
inline fun Array<String>.convertNamedColors() =
    map { it.convertNamedColors() }.toTypedArray()

/**
 * @return the [String] with all [NamedColor] gradient codes ("{#color1>}{#color2<>}{#color3<}" etc.) converted
 * to mojang color codes ("§x§R§R§G§G§B§B")
 */
fun String.convertCmiGradients(): String {
    return replace(namedColorSeparatorRegex) { match ->
        val hexCode = match.groupValues[1].fromHexOrNamed() ?: return@replace match.value
        val format = BooleanArray(5)
        val matches = formatCodeRegex().findAll(substring(0, match.range.first)).toList()
        for (i in matches.indices.reversed()) { // reversed for proper sorting
            val f = matches[i].value[1].lowercaseChar()
            if (f == 'r') break
            format[f - 'k'] = true
        }

        StringBuilder(22 + format.size * 2).append("{#$hexCode<}{#$hexCode>}").apply {
            for (i in format.indices) {
                if (format[i]) append(SECTION).append('k' + i)
            }
        }
    }.replace(namedColorGradientRegex) { match ->
        val (start, text, end) = match.destructured
        text.gradient(
            start.fromHexOrNamed() ?: return@replace match.value,
            end.fromHexOrNamed() ?: return@replace match.value
        )
    }
}
inline fun <T : Iterable<String>> T.convertCmiGradients(): List<String> =
    map { it.convertCmiGradients() }
inline fun Array<String>.convertCmiGradients() =
    map { it.convertCmiGradients() }.toTypedArray()

/**
 * @param start the starting [Color]
 * @param end the ending [Color]
 * @param formatChar for color code (default: [codePrefix])
 * @return the [String] with a gradient from [start] to [end]
 */
fun String.gradient(start: Color, end: Color, formatChar: CustomDelimiter = codePrefix): String {
    if (isEmpty()) return end.hexMojang() + this
    if (start == end) return start.hexMojang() + this
    if (length < 2) return start.hexMojang() + this + end.hexMojang()

    val strippedLength = stripFormatCodes().lastIndex
    val factor = 1.0 / strippedLength

    val formatRegex = formatCodeRegex()
    val result = StringBuilder(length * 14 + (length - strippedLength + 1) * 2)
    var format = ""
    var textIndex = 0
    var gradientIndex = 0
    while (textIndex < length) {
        val char = this[textIndex]
        if ((char == SECTION || char == formatChar.value) && textIndex < length - 1) {
            val potentialFormat = substring(textIndex, textIndex + 2)
            if (formatRegex.matches(potentialFormat)) {
                format = if (potentialFormat[1] == 'r') "" else format + potentialFormat
                textIndex += 2
                continue
            }
        }

        //if (!char.isWhitespace()) {
            result.append(start.interpolate(end, gradientIndex * factor).hexMojang()/* .hexMinify() */).append(format)
        //}
        result.append(char)
        textIndex++
        gradientIndex++
    }
    return result.toString()
}

/*fun String.gradientList(vararg colors: Color?, formatChar: CustomDelimiter = codePrefix): String {
    if (colors.isEmpty()) return this

    val strippedText = stripFormatCodes()
    val indices = colors.withIndex().filter { it.value != null }.map { it.index }
    if (indices.isEmpty()) return this

    val result = StringBuilder()
    var currentIndex = 0

    for (i in 0 until indices.lastIndex) {
        val startIdx = indices[i]
        val endIdx = indices[i + 1]
        val segmentLength = strippedText.length * (endIdx - startIdx) / (colors.size - 1)

        val segmentText = strippedText.substring(currentIndex, (currentIndex + segmentLength).coerceAtMost(strippedText.length))
        val startColor = colors[startIdx] ?: continue
        val endColor = colors[endIdx] ?: continue

        result.append(segmentText.gradient(startColor, endColor, formatChar))
        currentIndex += segmentLength
    }

    // Append any remaining characters with the last color
    if (currentIndex < strippedText.length) {
        val remainingText = strippedText.substring(currentIndex)
        result.append(remainingText.gradient(colors[indices.last()]!!, colors[indices.last()]!!, formatChar))
    }

    return result.toString()
}*/

/**
 * As this does not replace [NamedColor] gradient codes, use String[convertCmiGradients] first
 * @return the following [String]:
 *
 * - shortens color codes, if possible ("§x§a§a§0§0§a§a" -> "§5")
 *
 * - removes unnecessary mojang color codes ("§C" and "§x§R§R§G§G§B§B")
 *
 * - removes unnecessary mojang format codes ("§F") and sorts leading codes alphabetically
 */
fun String.minifyColors(): String {
    val matches = mojangCodeRegex.findAll(this)
    val result = StringBuilder(length).append(substring(0,
        matches.firstOrNull()?.range?.first ?: return this
    ))

    var lastColor = ""
    val lastFormats = BooleanArray(5)
    val sift = mutableListOf<String>()

    val iterator = matches.iterator()
    for ((idx, match) in iterator.withIndex()) {
        val text = substring(match.range.last + 1..<(matches.elementAtOrNull(idx + 1)?.range?.first ?: length))
        val m = match.value
        sift.add(m)

        // whitespace handling
        if (iterator.hasNext()) {
            if (text.isEmpty()) continue
            if (text.isBlank() && !(m.contains(SECTION + "m") || m.contains(SECTION + "n"))) {
                result.append(text)
                sift.clear()
                continue
            }
        }

        val newFormats = BooleanArray(5) // me trying to optimize lol
        val newColor = buildString {
            for (s in sift.indices.reversed()) { // reversing the sift allows alphabetical sorting
                val c = sift[s][1].lowercaseChar()
                if (c in 'k'..'o') {
                    newFormats[c - 'k'] = true
                    continue // this is stupidly important
                } else if (c in "0123456789abcdefr") { // r is considered a color in this case
                    sift[s].let { if (lastColor != it) append(it) }
                } else if (c == 'x') {
                    sift[s].fromMojangColor()!!.hexMinify().let { if (lastColor != it) append(it) }
                }
                break
            }
        }
        sift.clear()

        if (newColor.isEmpty()) {
            for (index in newFormats.indices) {
                if (newFormats[index] && !lastFormats[index]) {
                    lastFormats[index] = true
                    result.append(SECTION).append('k' + index)
                }
            }
        } else {
            lastColor = newColor
            newFormats.copyInto(lastFormats)
            result.append(newColor)
            for (index in lastFormats.indices) {
                if (lastFormats[index]) result.append(SECTION).append('k' + index)
            }
        }

        result.append(text)
    }
    return result.toString()
}
inline fun <T : Iterable<String>> T.minifyColors(): List<String> =
    map { it.minifyColors() }
inline fun Array<String>.minifyColors(): Array<String> =
    map { it.minifyColors() }.toTypedArray()

/**
 * @param delimiter for color code (default: [codePrefix])
 * @return the [String] with all possible color codes converted to mojang color codes ("§C", "§x§R§R§G§G§B§B")
 */
fun String.convertColors(minify: Boolean = false, delimiter: CustomDelimiter = codePrefix) =
    convertCmiGradients()
    .convertNamedColors()
    .convertHexCodes(delimiter)
    .convertColorsAndFormat(delimiter)
    .let { if (minify) it.minifyColors() else it }
inline fun <T : Iterable<String>> T.convertColors(minify: Boolean = false, delimiter: CustomDelimiter = codePrefix): List<String> {
    return map { it.convertColors(minify, delimiter) }
}
inline fun Array<String>.convertColors(minify: Boolean = false, delimiter: CustomDelimiter = codePrefix): Array<String> {
    return map { it.convertColors(minify, delimiter) }.toTypedArray()
}
