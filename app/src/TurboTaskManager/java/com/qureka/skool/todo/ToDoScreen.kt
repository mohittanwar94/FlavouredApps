package com.qureka.skool.todo

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.qureka.skool.R
import com.qureka.skool.theme.Color_Black
import com.qureka.skool.theme.DetailScreenTypography
import com.qureka.skool.theme.White
import com.qureka.skool.todo.composables.ToDoListItem
import com.qureka.skool.utils.OnRecyclerViewClick
import com.qureka.skool.viewmodel.ToDoViewModel

enum class ButtonActions {
    ADD_BUTTON,
    SEARCH,
}

enum class Keyboard {
    Opened, Closed
}

@Composable
fun keyboardState(): State<Keyboard> {
    val keyboardState = remember { mutableStateOf(Keyboard.Closed) }
    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (keypadHeight > screenHeight * 0.15) {
                Keyboard.Opened
            } else {
                Keyboard.Closed
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}

@Composable
fun ToDoListScreen(toDoViewModel: ToDoViewModel, onClick: OnRecyclerViewClick) {
    val state = rememberLazyListState()
    val taskList = toDoViewModel.taskList.observeAsState()
    val textSearch = toDoViewModel.textSearch.observeAsState()
    val isKeyboardOpen by keyboardState() // Keyboard.Opened or Keyboard.Closed
    if (isKeyboardOpen == Keyboard.Closed)
        toDoViewModel.search()
    val onClickAction: (action: ButtonActions) -> Unit = {
        when (it) {
            ButtonActions.SEARCH -> {
                toDoViewModel.search()
            }

            ButtonActions.ADD_BUTTON -> {
                onClick.onClick(3, 3)
            }
        }
    }
    val textEmpty = if (toDoViewModel.textSearch.value?.isEmpty() == true)
        stringResource(id = R.string.create_notes) else stringResource(id = R.string.no_result_found)

    Box(
        modifier = Modifier.fillMaxSize(1f),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.dashboard_bg),
            contentDescription = "background_image",
            contentScale = ContentScale.Crop
        )
        AnimatedVisibility(visible = taskList.value?.size == 0) {
            Text(
                text = textEmpty,
                style = DetailScreenTypography.titleSmall,
            )
        }
        Scaffold(containerColor = Color.Transparent, floatingActionButton = {
            AddFloatingActionButton(onClickAction)
        }) { paddingValues ->
            Column(modifier = Modifier.padding(paddingValues)) {
                Image(
                    modifier = Modifier
                        .clickable(onClick = {
                            onClick.onClick(1, 1)
                        })
                        .padding(vertical = 16.dp, horizontal = 17.dp)
                        .width(38.dp)
                        .height(38.dp),
                    painter = painterResource(id = R.drawable.iv_back),
                    contentDescription = "iv_back",
                    contentScale = ContentScale.FillBounds
                )
                SearchBox(textSearch, toDoViewModel, onClickAction)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                    modifier = Modifier
                        .fillMaxSize(1f)
                        .padding(start = 26.dp, end = 26.dp, top = 16.dp, bottom = 10.dp)
                ) {
                    AnimatedVisibility(visible = (taskList.value?.size ?: 0) > 0) {
                        Column {
                            LazyColumn(
                                reverseLayout = false,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.Top,
                                state = state
                            ) {
                                items(taskList.value!!.toList()) { item ->
                                    ToDoListItem(item, onClick, toDoViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    textSearch: State<String?>,
    toDoViewModel: ToDoViewModel,
    onClickAction: (action: ButtonActions) -> Unit
) {
    val buttonInteractionSource = remember { MutableInteractionSource() }
    BasicTextField(value = textSearch.value ?: "",
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, top = 38.dp)
            .fillMaxWidth(1f)
            .height(50.dp)
            .padding(horizontal = 15.dp, vertical = 0.dp)
            .background(color = White, shape = RoundedCornerShape(30.dp))
            .indicatorLine(
                enabled = true,
                isError = false,
                interactionSource = buttonInteractionSource,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                focusedIndicatorLineThickness = Dp(1f),
                unfocusedIndicatorLineThickness = Dp(1f)
            ),
        onValueChange = { toDoViewModel.updateSearchText(it) },
        singleLine = true,
        textStyle = DetailScreenTypography.labelMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onClickAction.invoke(ButtonActions.SEARCH) }
        ),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    Modifier
                        .weight(1f)
                        .padding(all = 14.dp)
                ) {

                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Search",
                        tint = Color_Black
                    )
                    Box(modifier = Modifier.padding(start = 5.dp)) {
                        if (textSearch.value?.isEmpty() == true) {
                            Text(
                                text = stringResource(id = R.string.search),
                                style = DetailScreenTypography.labelSmall
                            )
                        }
                        innerTextField()
                    }

                }
            }
        }
    )
}

/*@Composable
fun SearchBox(
    textSearch: State<String?>,
    toDoViewModel: ToDoViewModel,
    onClickAction: (action: ButtonActions) -> Unit
) {
    TextField(
        value = textSearch.value ?: "",
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 38.dp)
            .fillMaxWidth(1f)
            .height(50.dp)
            .padding(horizontal = 15.dp, vertical = 0.dp)
            .background(color = White, shape = RoundedCornerShape(30.dp)),
        onValueChange = { toDoViewModel.updateSearchText(it) },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            errorContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
        ),
        placeholder = {
            Text(
                text = stringResource(id = R.string.search),
                style = DetailScreenTypography.labelSmall
            )
        },
        singleLine = true,
        textStyle = DetailScreenTypography.labelMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Search
        ),
        leadingIcon = {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                tint = Color_Black
            )
        },
        keyboardActions = KeyboardActions(
            onSearch = { onClickAction.invoke(ButtonActions.SEARCH) }
        )
    )
}*/

@Composable
fun AddFloatingActionButton(onClickAction: (action: ButtonActions) -> Unit) {
    Image(
        modifier = Modifier
            .padding(all = 16.dp)
            .size(48.dp)
            .shadow(
                elevation = 15.dp,
                spotColor = Color_Black,
                ambientColor = Color_Black,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable {
                onClickAction.invoke(ButtonActions.ADD_BUTTON)
            },
        painter = painterResource(id = R.drawable.craete_icon),
        contentDescription = "Add",
        contentScale = ContentScale.FillBounds
    )
}
