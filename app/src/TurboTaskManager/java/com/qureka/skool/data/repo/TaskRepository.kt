package com.qureka.skool.data.repo

import com.qureka.skool.data.datasource.TaskDataSource
import com.qureka.skool.data.entity.Todo

class TaskRepository(private val tds: TaskDataSource) {
    suspend fun loadTasks(): List<Todo> = tds.loadTasks()

    suspend fun search(queryWord: String): List<Todo> = tds.search(queryWord)

    suspend fun createTask(
        taskTitle: String,
        taskDescription: String,
        taskColor: Int,
        taskTime: Long
    ) =
        tds.createTask(taskTitle, taskDescription, taskColor, taskTime)

    suspend fun updateTask(
        taskId: Int,
        taskTitle: String,
        taskColor: Int,
        taskTime: Long,
        taskDescription: String
    ) =
        tds.updateTask(taskId, taskTitle, taskColor, taskTime, taskDescription)

    suspend fun deleteTask(task: Todo) = tds.deleteTask(task)
}