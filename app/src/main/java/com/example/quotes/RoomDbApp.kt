package com.example.quotes

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [DataEntity::class], version = 3, exportSchema = false)
abstract class RoomDbApp: RoomDatabase() {
    abstract fun dao(): Dao?
    companion object {
        private var INSTANCE: RoomDbApp? = null
        /*
        fun getAppDatabase(context: Context): RoomDbApp? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder<RoomDbApp>(
                    context.applicationContext, RoomDbApp::class.java, "AppDB")
                    .allowMainThreadQueries().fallbackToDestructiveMigration()
                    .build()
            }
            return INSTANCE
        }*/
        fun getAppDatabase(context: Context): RoomDbApp {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RoomDbApp::class.java,
                    "AppDB"
                ).allowMainThreadQueries().fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance//this return instance
            }
        }
    }
}