package com.qureka.skool.todo.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.qureka.skool.R
import com.qureka.skool.data.entity.Todo
import com.qureka.skool.theme.Color_bebb49
import com.qureka.skool.theme.DetailScreenTypography
import com.qureka.skool.theme.White
import com.qureka.skool.utils.OnRecyclerViewClick
import com.qureka.skool.viewmodel.ToDoViewModel


@Composable
fun ToDoListItem(item: Todo, action: OnRecyclerViewClick, toDoViewModel: ToDoViewModel) {
    Card(
        modifier = Modifier
            .padding(bottom = 7.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                toDoViewModel.setEditTodo(item)
                action.onClick(4, 3)
            },
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(containerColor = Color(item.taskColor))
    ) {
        Row {
            Column(
                modifier = Modifier
                    .weight(4f)
                    .fillMaxWidth()
                    .padding(start = 14.dp, top = 8.dp, end = 11.dp, bottom = 10.dp),
            ) {
                Text(
                    item.taskTitle,
                    style = DetailScreenTypography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    item.taskDescription,
                    style = DetailScreenTypography.titleMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = 14.dp, top = 8.dp, end = 10.dp, bottom = 10.dp),
            ) {
                Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                Image(
                    modifier = Modifier
                        .size(20.dp)
                        .background(color = White, shape = CircleShape)
                        .border(
                            width = 0.7.dp,
                            color = Color_bebb49,
                            shape = CircleShape
                        )
                        .padding(all = 5.dp)
                        .clickable {
                            toDoViewModel.deleteTodo(item)
                        },
                    contentScale = ContentScale.Crop,
                    contentDescription = "Edit",
                    painter = painterResource(id = R.drawable.bin),

                    )
            }
        }
    }
}
