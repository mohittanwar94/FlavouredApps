package com.qureka.skool.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.qureka.skool.data.entity.Todo

@Dao
interface TaskDao {
    @Query("SELECT * FROM todo ORDER BY task_time ASC")
    suspend fun loadTasks(): List<Todo>

    @Insert
    suspend fun createTask(task: Todo)

    @Update
    suspend fun updateTask(task: Todo)

    @Delete
    suspend fun delete(task: Todo)

    @Query("SELECT * FROM todo WHERE task_title like '%' || :queryWord || '%'")
    suspend fun search(queryWord: String): List<Todo>
}