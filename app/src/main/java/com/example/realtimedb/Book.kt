package com.example.realtimedb

data class Book(
    var title: String? = null,
    var author: String? = null,
    var read: Boolean = false
) {
    constructor() : this(null, null, false)
}
