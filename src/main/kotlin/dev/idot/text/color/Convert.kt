/**
 * "Codes" ("Color Codes" or "Hex Codes") are minecraft-specific coded colors
 * "Colors" ("Hex Colors") are standard hex colors, named [NamedColor], or [Color] objects
 * I did my best, terminology is hard, anyways...
 */

@file:Suppress("NOTHING_TO_INLINE", "unused")

package dev.idot.text.color

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
inline fun <T : Iterable<String>> T.stripColors(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.stripColors(delimiter) }
inline fun Array<String>.stripColors(delimiter: CustomDelimiter = codePrefix) =
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

// Bukkit
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

fun String.componentBukkitColors(delimiter: CustomDelimiter = codePrefix): String =
    replace(bukkitHexRegex(delimiter)) { match ->
        val hex = match.value
        when (hex.length) {
            8 -> StringBuilder(7).append("<color:#").apply {
                for (c in hex.drop(3)) {
                    if (c == delimiter.value) continue
                    append(c).append(c)
                }
                append(">")
            }
            14 -> hex.replace("[${delimiter.value}x]".toRegex(), "").let { "<color:#$it>" }
            else -> hex
        }
    }
inline fun <T : Iterable<String>> T.componentBukkitColors(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.componentBukkitColors(delimiter) }
inline fun Array<String>.componentBukkitColors(delimiter: CustomDelimiter = codePrefix) =
    map { it.componentBukkitColors(delimiter) }.toTypedArray()


// Color Codes
fun String.convertColorCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(colorCodeRegex(delimiter), "$SECTION$1")
inline fun <T : Iterable<String>> T.convertColorCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.convertColorCodes(delimiter) }
inline fun Array<String>.convertColorCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.convertColorCodes(delimiter) }.toTypedArray()

fun String.componentColorCodes(delimiter: CustomDelimiter = codePrefix): String =
    colorCodeRegex(delimiter).replace(this) { match ->
        CodedColor[match.value[1]]?.let { "<${it.name.lowercase()}>" } ?: match.value
    }
inline fun <T : Iterable<String>> T.componentColorCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.componentColorCodes(delimiter) }
inline fun Array<String>.componentColorCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.componentColorCodes(delimiter) }.toTypedArray()


// Format Codes
fun String.convertFormatCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(formatCodeRegex(delimiter), "$SECTION$1")
inline fun <T : Iterable<String>> T.convertFormatCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.convertFormatCodes(delimiter) }
inline fun Array<String>.convertFormatCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.convertFormatCodes(delimiter) }.toTypedArray()

fun String.componentFormatCodes(delimiter: CustomDelimiter = codePrefix): String =
    formatCodeRegex(delimiter).replace(this) { match ->
        when (match.groupValues[1]) {
            "k" -> "obf"
            "l" -> "b"
            "m" -> "st"
            "n" -> "u"
            "o" -> "i"
            "r" -> "reset"
            else -> match.value
        }.let { "<$it>" }
    }
inline fun <T : Iterable<String>> T.componentFormatCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.componentFormatCodes(delimiter) }
inline fun Array<String>.componentFormatCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.componentFormatCodes(delimiter) }.toTypedArray()


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

fun String.componentHexCodes(delimiter: CustomDelimiter = codePrefix): String =
    replace(hexCodeRegex(delimiter)) {
        it.groupValues[1].fromStrictHexCode()?.toString()?.let { "<color:#$it>" } ?: it.value
    }
inline fun <T : Iterable<String>> T.componentHexCodes(delimiter: CustomDelimiter = codePrefix): List<String> =
    map { it.componentHexCodes(delimiter) }
inline fun Array<String>.componentHexCodes(delimiter: CustomDelimiter = codePrefix) =
    map { it.componentHexCodes(delimiter) }.toTypedArray()


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

fun String.componentNamedColors(): String =
    replace(namedColorRegex) { it.groupValues[1].fromHexOrNamed()?.let { "<color:#$it>"} ?: it.value }
inline fun <T : Iterable<String>> T.componentNamedColors(): List<String> =
    map { it.componentNamedColors() }
inline fun Array<String>.componentNamedColors() =
    map { it.componentNamedColors() }.toTypedArray()

private fun String.expandCmiGradientSeparators(): String =
    replace(namedColorSeparatorRegex) { match ->
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
    }

/**
 * @return the [String] with all [NamedColor] gradient codes ("{#color1>}{#color2<>}{#color3<}" etc.) converted
 * to mojang color codes ("§x§R§R§G§G§B§B")
 */
fun String.convertCmiGradients(): String {
    return expandCmiGradientSeparators().replace(namedColorGradientRegex) { match ->
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
 * @return the [String] with all [NamedColor] gradient codes ("{#color1>}{#color2<>}{#color3<}" etc.) converted
 * to minimessage format ("<gradient:#color1:#color2></gradient>")
 */
fun String.componentCmiGradients(): String =
    expandCmiGradientSeparators().replace(namedColorGradientRegex) { match ->
        val (start, text, end) = match.destructured
        val s = start.fromHexOrNamed() ?: return@replace match.value
        val e = end.fromHexOrNamed() ?: return@replace match.value
        "<gradient:#$s:#$e>$text</gradient>"
    }
inline fun <T : Iterable<String>> T.componentCmiGradients(): List<String> =
    map { it.componentCmiGradients() }
inline fun Array<String>.componentCmiGradients() =
    map { it.componentCmiGradients() }.toTypedArray()


fun String.componentColors(): String = mojangColorRegex.replace(this) { match ->
    match.value.drop(3).replace(SECTION.toString(), "").let { "<color:#$it>" }
}.componentCmiGradients().componentNamedColors()
    .componentHexCodes().componentFormatCodes().componentColorCodes()
inline fun <T : Iterable<String>> T.componentColors(): List<String> =
    map { it.componentColors() }
inline fun Array<String>.componentColors() =
    map { it.componentColors() }.toTypedArray()

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

object ColorUtil {
    @JvmStatic fun stripColors(input: String) = input.stripColors()
    @JvmStatic fun stripColors(input: String, delimiter: CustomDelimiter) = input.stripColors(delimiter)
    @JvmStatic fun <T : Iterable<String>> stripColors(input: T): List<String> = input.stripColors()
    @JvmStatic fun <T : Iterable<String>> stripColors(input: T, delimiter: CustomDelimiter): List<String> = input.stripColors(delimiter)
    @JvmStatic fun stripColors(input: Array<String>): Array<String> = input.stripColors()
    @JvmStatic fun stripColors(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.stripColors(delimiter)

    @JvmStatic fun stripFormatCodes(input: String) = input.stripFormatCodes()
    @JvmStatic fun stripFormatCodes(input: String, delimiter: CustomDelimiter) = input.stripFormatCodes(delimiter)
    @JvmStatic fun <T : Iterable<String>> stripFormatCodes(input: T): List<String> = input.stripFormatCodes()
    @JvmStatic fun <T : Iterable<String>> stripFormatCodes(input: T, delimiter: CustomDelimiter): List<String> = input.stripFormatCodes(delimiter)
    @JvmStatic fun stripFormatCodes(input: Array<String>): Array<String> = input.stripFormatCodes()
    @JvmStatic fun stripFormatCodes(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.stripFormatCodes(delimiter)

    @JvmStatic fun stripBukkitCodes(input: String) = input.stripBukkitCodes()
    @JvmStatic fun stripBukkitCodes(input: String, delimiter: CustomDelimiter) = input.stripBukkitCodes(delimiter)
    @JvmStatic fun <T : Iterable<String>> stripBukkitCodes(input: T): List<String> = input.stripBukkitCodes()
    @JvmStatic fun <T : Iterable<String>> stripBukkitCodes(input: T, delimiter: CustomDelimiter): List<String> = input.stripBukkitCodes(delimiter)
    @JvmStatic fun stripBukkitCodes(input: Array<String>): Array<String> = input.stripBukkitCodes()
    @JvmStatic fun stripBukkitCodes(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.stripBukkitCodes(delimiter)

    @JvmStatic fun stripNamedColors(input: String): String = input.stripNamedColors()
    @JvmStatic fun <T : Iterable<String>> stripNamedColors(input: T): List<String> = input.stripNamedColors()
    @JvmStatic fun stripNamedColors(input: Array<String>): Array<String> = input.stripNamedColors()

    @JvmStatic fun stripHexCodes(input: String) = input.stripHexCodes()
    @JvmStatic fun stripHexCodes(input: String, delimiter: CustomDelimiter) = input.stripHexCodes(delimiter)
    @JvmStatic fun <T : Iterable<String>> stripHexCodes(input: T): List<String> = input.stripHexCodes()
    @JvmStatic fun <T : Iterable<String>> stripHexCodes(input: T, delimiter: CustomDelimiter): List<String> = input.stripHexCodes(delimiter)
    @JvmStatic fun stripHexCodes(input: Array<String>): Array<String> = input.stripHexCodes()
    @JvmStatic fun stripHexCodes(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.stripHexCodes(delimiter)

    @JvmStatic fun stripBukkitHexCodes(input: String) = input.stripBukkitHexCodes()
    @JvmStatic fun stripBukkitHexCodes(input: String, delimiter: CustomDelimiter) = input.stripBukkitHexCodes(delimiter)
    @JvmStatic fun <T : Iterable<String>> stripBukkitHexCodes(input: T): List<String> = input.stripBukkitHexCodes()
    @JvmStatic fun <T : Iterable<String>> stripBukkitHexCodes(input: T, delimiter: CustomDelimiter): List<String> = input.stripBukkitHexCodes(delimiter)
    @JvmStatic fun stripBukkitHexCodes(input: Array<String>): Array<String> = input.stripBukkitHexCodes()
    @JvmStatic fun stripBukkitHexCodes(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.stripBukkitHexCodes(delimiter)

    @JvmStatic fun stripColorCodes(input: String) = input.stripColorCodes()
    @JvmStatic fun stripColorCodes(input: String, delimiter: CustomDelimiter) = input.stripColorCodes(delimiter)
    @JvmStatic fun <T : Iterable<String>> stripColorCodes(input: T): List<String> = input.stripColorCodes()
    @JvmStatic fun <T : Iterable<String>> stripColorCodes(input: T, delimiter: CustomDelimiter): List<String> = input.stripColorCodes(delimiter)
    @JvmStatic fun stripColorCodes(input: Array<String>): Array<String> = input.stripColorCodes()
    @JvmStatic fun stripColorCodes(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.stripColorCodes(delimiter)

    @JvmStatic fun stripMojangCodes(input: String): String = input.stripMojangCodes()
    @JvmStatic fun <T : Iterable<String>> stripMojangCodes(input: T): List<String> = input.stripMojangCodes()
    @JvmStatic fun stripMojangCodes(input: Array<String>): Array<String> = input.stripMojangCodes()


    @JvmStatic fun convertColorsAndFormat(input: String): String = input.convertColorsAndFormat()
    @JvmStatic fun convertColorsAndFormat(input: String, delimiter: CustomDelimiter): String = input.convertColorsAndFormat(delimiter)
    @JvmStatic fun <T : Iterable<String>> convertColorsAndFormat(input: T): List<String> = input.convertColorsAndFormat()
    @JvmStatic fun <T : Iterable<String>> convertColorsAndFormat(input: T, delimiter: CustomDelimiter): List<String> = input.convertColorsAndFormat(delimiter)
    @JvmStatic fun convertColorsAndFormat(input: Array<String>): Array<String> = input.convertColorsAndFormat()
    @JvmStatic fun convertColorsAndFormat(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.convertColorsAndFormat(delimiter)

    @JvmStatic fun convertBukkitColors(input: String): String = input.convertBukkitColors()
    @JvmStatic fun convertBukkitColors(input: String, delimiter: CustomDelimiter): String = input.convertBukkitColors(delimiter)
    @JvmStatic fun <T : Iterable<String>> convertBukkitColors(input: T): List<String> = input.convertBukkitColors()
    @JvmStatic fun <T : Iterable<String>> convertBukkitColors(input: T, delimiter: CustomDelimiter): List<String> = input.convertBukkitColors(delimiter)
    @JvmStatic fun convertBukkitColors(input: Array<String>): Array<String> = input.convertBukkitColors()
    @JvmStatic fun convertBukkitColors(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.convertBukkitColors(delimiter)

    @JvmStatic fun convertColorCodes(input: String): String = input.convertColorCodes()
    @JvmStatic fun convertColorCodes(input: String, delimiter: CustomDelimiter): String = input.convertColorCodes(delimiter)
    @JvmStatic fun <T : Iterable<String>> convertColorCodes(input: T): List<String> = input.convertColorCodes()
    @JvmStatic fun <T : Iterable<String>> convertColorCodes(input: T, delimiter: CustomDelimiter): List<String> = input.convertColorCodes(delimiter)
    @JvmStatic fun convertColorCodes(input: Array<String>): Array<String> = input.convertColorCodes()
    @JvmStatic fun convertColorCodes(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.convertColorCodes(delimiter)

    @JvmStatic fun convertFormatCodes(input: String): String = input.convertFormatCodes()
    @JvmStatic fun convertFormatCodes(input: String, delimiter: CustomDelimiter): String = input.convertFormatCodes(delimiter)
    @JvmStatic fun <T : Iterable<String>> convertFormatCodes(input: T): List<String> = input.convertFormatCodes()
    @JvmStatic fun <T : Iterable<String>> convertFormatCodes(input: T, delimiter: CustomDelimiter): List<String> = input.convertFormatCodes(delimiter)
    @JvmStatic fun convertFormatCodes(input: Array<String>): Array<String> = input.convertFormatCodes()
    @JvmStatic fun convertFormatCodes(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.convertFormatCodes(delimiter)

    @JvmStatic fun convertHexCodes(input: String): String = input.convertHexCodes()
    @JvmStatic fun convertHexCodes(input: String, delimiter: CustomDelimiter): String = input.convertHexCodes(delimiter)
    @JvmStatic fun <T : Iterable<String>> convertHexCodes(input: T): List<String> = input.convertHexCodes()
    @JvmStatic fun <T : Iterable<String>> convertHexCodes(input: T, delimiter: CustomDelimiter): List<String> = input.convertHexCodes(delimiter)
    @JvmStatic fun convertHexCodes(input: Array<String>): Array<String> = input.convertHexCodes()
    @JvmStatic fun convertHexCodes(input: Array<String>, delimiter: CustomDelimiter): Array<String> = input.convertHexCodes(delimiter)

    @JvmStatic fun convertNamedColors(input: String): String = input.convertNamedColors()
    @JvmStatic fun <T : Iterable<String>> convertNamedColors(input: T): List<String> = input.convertNamedColors()
    @JvmStatic fun convertNamedColors(input: Array<String>): Array<String> = input.convertNamedColors()

    @JvmStatic fun convertCmiGradients(input: String): String = input.convertCmiGradients()
    @JvmStatic fun <T : Iterable<String>> convertCmiGradients(input: T): List<String> = input.convertCmiGradients()
    @JvmStatic fun convertCmiGradients(input: Array<String>): Array<String> = input.convertCmiGradients()

    @JvmStatic fun gradient(input: String, start: Color, end: Color): String =
        input.gradient(start, end)
    @JvmStatic fun gradient(input: String, start: Color, end: Color, formatChar: CustomDelimiter): String =
        input.gradient(start, end, formatChar)

    @JvmStatic fun minifyColors(input: String): String = input.minifyColors()
    @JvmStatic fun <T : Iterable<String>> minifyColors(input: T): List<String> = input.minifyColors()
    @JvmStatic fun minifyColors(input: Array<String>): Array<String> = input.minifyColors()

    @JvmStatic fun convertColors(input: String): String = input.convertColors()
    @JvmStatic fun convertColors(input: String, minify: Boolean): String = input.convertColors(minify)
    @JvmStatic fun convertColors(input: String, minify: Boolean, delimiter: CustomDelimiter): String =
        input.convertColors(minify, delimiter)
    @JvmStatic fun <T : Iterable<String>> convertColors(input: T): List<String> =
        input.convertColors()
    @JvmStatic fun <T : Iterable<String>> convertColors(input: T, minify: Boolean): List<String> =
        input.convertColors(minify)
    @JvmStatic fun <T : Iterable<String>> convertColors(input: T, minify: Boolean, delimiter: CustomDelimiter): List<String> =
        input.convertColors(minify, delimiter)
    @JvmStatic fun convertColors(input: Array<String>) =
        input.convertColors()
    @JvmStatic fun convertColors(input: Array<String>, minify: Boolean) =
        input.convertColors(minify)
    @JvmStatic fun convertColors(input: Array<String>, minify: Boolean, delimiter: CustomDelimiter) =
        input.convertColors(minify, delimiter)
}