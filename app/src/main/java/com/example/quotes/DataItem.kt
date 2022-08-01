package com.example.quotes

data class DataItem (
    var _id: String = "",
    var author: String = "",
    val authorSlug: String = "",
    var content: String = "",
    val dateAdded: String = "",
    val dateModified: String = "",
    val length: Int = 0,
    val tags: List<String> = listOf()
)