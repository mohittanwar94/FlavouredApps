package com.qureka.skool.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Entity(tableName = "todo")
data class Todo(@PrimaryKey(autoGenerate = true)
                @ColumnInfo(name = "task_id") var taskId: Int,
                @ColumnInfo(name = "task_title") var taskTitle: String,
                @ColumnInfo(name = "task_color") var taskColor: Int,
                @ColumnInfo(name = "task_time") var taskTime: Long,
                @ColumnInfo(name = "task_description") var taskDescription: String) : Serializable {
}