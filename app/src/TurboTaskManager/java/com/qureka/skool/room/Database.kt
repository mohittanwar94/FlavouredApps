package com.qureka.skool.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.qureka.skool.data.entity.Todo

@Database(entities = [Todo::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun getTaskDao(): TaskDao
}