package com.example.quotes

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.sql.Date
import java.sql.RowId

@Entity(tableName = "favourites2")

data class DataEntity (
    @PrimaryKey
    @ColumnInfo(name = "id") var id: String = "" ,
    @ColumnInfo(name = "quote") var quote: String = "" ,
    @ColumnInfo(name = "author") var author: String = ""
)

