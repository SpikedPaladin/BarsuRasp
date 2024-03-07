package me.paladin.barsurasp.models

data class Item(
    val title: String,
    val subtitle: String
) {
    fun isGroup() = title.contains("[0-9]".toRegex())

    fun toPref(): String = "$title:$subtitle"

    companion object {
        operator fun invoke(pref: String): Item {
            val split = pref.split(":", limit = 2)
            return Item(split.first(), split.getOrNull(1) ?: "Неизвестно")
        }
    }
}