package com.example.quotes

data class Data(
    val count: Int = 0,
    val totalCount: Int = 0,
    val page: Int = 0,
    val totalPages: Int = 0,
    val lastItemIndex: Int = 0,
    var results: MutableList<DataItem> = mutableListOf<DataItem>()
)