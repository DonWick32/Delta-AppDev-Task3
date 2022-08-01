package com.example.quotes

import androidx.room.*
import androidx.room.Dao

@Dao
interface Dao {
    @Query("SELECT * FROM favourites2")
    fun getQuotesInfo(): List<DataEntity>?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertQuote(data: DataEntity?)

    @Delete
    fun deleteQuote(data: DataEntity?)
}