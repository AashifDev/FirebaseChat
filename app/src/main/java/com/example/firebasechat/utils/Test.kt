package com.example.firebasechat.utils

data class Item(val id: String, val name: String)

fun main() {
    val items = arrayOf(Item("fxpLZbngM7g8sCztMqDurOpgNEn2", "Item 1"), Item("fxpLZbngM7g8sCztMqDurOpgNEn3", "Item 2"), Item("fxpLZbngM7g8sCztMqDurOpgNEn4", "Item 3"))

    val desiredId = "fxpLZbngM7g8sCztMqDurOpgNEn2"
    val desiredItem = items.find { it.id == desiredId }

    desiredItem?.let {
        println("Item found: ${it.name}")
    } ?: run {
        println("Item not found.")
    }
}