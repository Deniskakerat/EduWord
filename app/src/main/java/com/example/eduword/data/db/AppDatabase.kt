package com.example.eduword.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.eduword.data.dao.WordDao
import com.example.eduword.data.entity.WordEntity

@Database(
    entities = [WordEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun wordDao(): WordDao
}
