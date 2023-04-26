package utils


fun StringBuilder.mdHeader(level: Int = 1, text: String) {
    val prefix = "#".repeat(level)
    append("$prefix $text\n\n")
}

fun StringBuilder.mdParagraph(text: String) {
    append("$text\n\n")
}

fun StringBuilder.mdCodeBlock(text: String, language: String? = null) {
    if (language != null) {
        append("```$language\n")
        append("$text\n")
        append("```\n\n")
    } else {
        append("```\n")
        append("$text\n")
        append("```\n\n")
    }
}

fun StringBuilder.mdUnorderedList(vararg items: String) {
    items.forEach { append("* $it\n") }
    append("\n")
}

fun StringBuilder.mdOrderedList(vararg items: String) {
    items.forEachIndexed { index, item -> append("${index + 1}. $item\n") }
    append("\n")
}

fun StringBuilder.mdTable(headers: List<String>, vararg rows: List<String>) {
    val columnCount = headers.size
    val maxLengths = IntArray(columnCount) { 0 }

    // Calculate max length of each column
    headers.forEachIndexed { i, header ->
        maxLengths[i] = header.length
    }
    rows.forEach { row ->
        row.forEachIndexed { i, cell ->
            maxLengths[i] = maxLengths[i].coerceAtLeast(cell.length)
        }
    }

    // Print header
    headers.forEachIndexed { i, header ->
        append("| ${header.padEnd(maxLengths[i])} ")
    }
    append("|\n")
    headers.forEachIndexed { i, _ ->
        append("| ${"-".repeat(maxLengths[i])} ")
    }
    append("|\n")

    // Print rows
    rows.forEach { row ->
        row.forEachIndexed { i, cell ->
            append("| ${cell.padEnd(maxLengths[i])} ")
        }
        append("|\n")
    }
    append("\n")
}
