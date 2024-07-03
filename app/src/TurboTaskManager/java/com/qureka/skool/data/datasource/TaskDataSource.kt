package com.qureka.skool.data.datasource

import com.qureka.skool.data.entity.Todo
import com.qureka.skool.room.TaskDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskDataSource(var taskDao: TaskDao) {
    suspend fun loadTasks(): List<Todo> =
        withContext(Dispatchers.IO) {
            return@withContext taskDao.loadTasks()
        }

    suspend fun search(queryWord: String): List<Todo> =
        withContext(Dispatchers.IO) {
            return@withContext taskDao.search(queryWord)
        }

    suspend fun createTask(
        taskTitle: String,
        taskDescription: String,
        taskColor: Int,
        taskTime: Long
    ) {
        val newTask = Todo(0, taskTitle, taskColor, taskTime, taskDescription)
        taskDao.createTask(newTask)
    }

    suspend fun updateTask(
        taskId: Int,
        taskTitle: String,
        taskColor: Int,
        taskTime: Long,
        taskDescription: String
    ) {
        val updatedTask = Todo(taskId, taskTitle, taskColor, taskTime, taskDescription)
        taskDao.updateTask(updatedTask)
    }

    suspend fun deleteTask(
        task: Todo,
    ) {
        taskDao.delete(task)
    }
}