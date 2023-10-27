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