package com.qureka.skool.activity

import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.qureka.skool.R
import com.qureka.skool.common.BaseActivity
import com.qureka.skool.data.entity.Todo
import com.qureka.skool.theme.WordCheckTheme
import com.qureka.skool.todo.composables.EditNoteContent
import com.qureka.skool.utils.OnRecyclerViewClick
import com.qureka.skool.utils.Utils
import com.qureka.skool.viewmodel.AddEditViewModel


class AddEditNotesActivity : BaseActivity() {
    private val addEditViewModel by viewModels<AddEditViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WordCheckTheme {
                DetailUi()
            }
        }
        if (intent.getBooleanExtra(EDIT, false)) {
            addEditViewModel.updateEditStatus(true)
            addEditViewModel.setEditTodo(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra(ITEM, Todo::class.java)
                } else {
                    intent.getSerializableExtra(ITEM) as? Todo
                }
            )
        }
    }


    @Composable
    private fun DetailUi() {
        EditNoteContent(addEditViewModel, object : OnRecyclerViewClick {
            override fun onClick(position: Int, type: Int) {
                onBack()
            }
        })
    }

    private fun onBack() {
        when (addEditViewModel.isEdit.value) {
            true -> {
                if (addEditViewModel.textTitle.value?.isEmpty() == true) {
                    Utils.showToast(this, getString(R.string.please_add_some_text_to_title))
                } else {
                    addEditTask()
                }
            }

            false -> {
                if (addEditViewModel.textTitle.value?.isEmpty() == true &&
                    addEditViewModel.textDescription.value?.isEmpty() == true
                ) {
                    finish()
                } else if (addEditViewModel.textTitle.value?.isEmpty() == true) {
                    Utils.showToast(this, getString(R.string.please_add_some_text_to_title))
                } else {
                    addEditTask()
                }
            }

            else -> Unit
        }
    }

    private fun addEditTask() {
        if (addEditViewModel.isEdit.value == true)
            addEditViewModel.updateTodo()
        else
            addEditViewModel.addTodo()
        finish()
    }

    override fun onBackPressedCustom() {
        onBack()
    }
}