package me.paladin.barsurasp.utils

import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

fun Element.splitString(): List<String> {
    var string = ""

    this.childNodes().forEach {
        string += if (it is TextNode)
            it.wholeText.trim()
        else
            "|"
    }

    if (string.endsWith("|"))
        string = string.substring(0, string.length - 1)

    return string.split("|")
}

fun Element.parseLastUpdate(): String {
    if (!text().contains("[0-9]".toRegex()))
        return ""

    val parts = text().split(" ")
    val dateParts = parts[2].split("-")
    val timeParts = parts[3].split(":")

    return pretyLastUpdate(dateParts, timeParts)
}