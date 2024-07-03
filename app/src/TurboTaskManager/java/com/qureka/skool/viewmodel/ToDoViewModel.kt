package com.qureka.skool.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.colorspace.ColorSpace
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
class ToDoViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {

    private val _textSearch = MutableLiveData("")
    private val _taskList = MutableLiveData<List<Todo>>()
    private val _todo = MutableLiveData<Todo>()
    val taskList: LiveData<List<Todo>> get() = _taskList
    val todo: LiveData<Todo> get() = _todo

    init {
        loadTasks()
    }

    fun setEditTodo(todo: Todo) {
        _todo.value = todo
    }

    fun deleteTodo(todo: Todo) {
        CoroutineScope(Dispatchers.Main).launch {
            taskRepository.deleteTask(todo)
            loadTasks()
        }
    }

    fun loadTasks() {
        CoroutineScope(Dispatchers.Main).launch {
            _taskList.value = taskRepository.loadTasks()
        }
    }

    fun search() {
        CoroutineScope(Dispatchers.Main).launch {
            _taskList.value = taskRepository.search(textSearch.value ?: "")
        }
    }

    val textSearch: LiveData<String>
        get() = _textSearch

    fun updateSearchText(text: String) {
        _textSearch.value = text
    }
}
