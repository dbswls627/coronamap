package com.jo.coronamap

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jo.coronamap.dataModel.Corona


@Database(entities = [Corona::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun DataDao(): DataDao
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "Data.db")
                        .fallbackToDestructiveMigration().allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }
    }
}

