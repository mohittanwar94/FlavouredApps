package com.qureka.skool.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.qureka.skool.data.entity.Todo
import com.qureka.skool.data.repo.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class AddEditViewModel @Inject constructor(private val taskRepository: TaskRepository) :
    ViewModel() {

    private val _textDescription = MutableLiveData("")
    private val _textTitle = MutableLiveData("")
    private val _isEdit = MutableLiveData(false)
    private val _editTodo = MutableLiveData<Todo>()

    val textDescription: LiveData<String>
        get() = _textDescription

    val textTitle: LiveData<String>
        get() = _textTitle

    val isEdit: LiveData<Boolean>
        get() = _isEdit

    fun addTodo() {
        println("textDescription.value==============${textDescription.value}")
        CoroutineScope(Dispatchers.Main).launch {
            taskRepository.createTask(
                textTitle.value ?: "N/A",
                textDescription.value ?: "N/A",
                randomColor(),
                System.currentTimeMillis()
            )
        }
    }

    private fun randomColor(): Int {
        val red = Random.nextInt(150, 250)
        val green = Random.nextInt(150, 250)
        val blue = Random.nextInt(150, 250)
        return Color(red, green, blue).toArgb()
    }

    fun updateTodo() {
        _editTodo.value?.let { toDo ->
            CoroutineScope(Dispatchers.Main).launch {
                taskRepository.updateTask(
                    toDo.taskId,
                    textTitle.value ?: "",
                    toDo.taskColor,
                    toDo.taskTime,
                    textDescription.value ?: ""
                )
            }
        }

    }

    fun updateTitleText(text: String) {
        _textTitle.value = text
    }

    fun updateDescriptionText(text: String) {
        _textDescription.value = text
    }

    fun updateEditStatus(isEdit: Boolean) {
        _isEdit.value = isEdit
    }

    fun setEditTodo(todo: Todo?) {
        todo?.let {
            _editTodo.value = it
            updateTitleText(it.taskTitle)
            updateDescriptionText(it.taskDescription)
        }
    }
}
