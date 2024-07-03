package com.qureka.skool.stopwatch

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.qureka.skool.R
import com.qureka.skool.stopwatch.composables.LapTimeItem
import com.qureka.skool.theme.Color_45000000
import com.qureka.skool.theme.Color_fedc77
import com.qureka.skool.theme.DetailScreenTypography
import com.qureka.skool.utils.OnRecyclerViewClick

enum class ButtonActions {
    TOGGLE_PLAY,
    RESET_BUTTON,
    LAPSE_BUTTON,
}

data class LapseTime(
    val hour: Int = 0,
    val minute: Int = 0,
    val second: Int = 0,
    val millisecond: Int = 0,
    val angle: Float = 0F
)

@Composable
fun StopWatchScreen(timerViewModel: StopWatchViewModel, onClick: OnRecyclerViewClick) {
    val state = rememberLazyListState()
    val lapseList = timerViewModel.lapseList.observeAsState()
    val context = LocalContext.current

    val tempState = timerViewModel.state.observeAsState()

    val isPlaying = tempState.value == StopWatchState.STARTED
    val isPause = tempState.value == StopWatchState.PAUSED

    val onClickAction: (action: ButtonActions) -> Unit = {
        when (it) {
            ButtonActions.TOGGLE_PLAY -> {
                val command = when (isPlaying) {
                    true -> StopWatchCommand.PAUSE_SERVICE
                    false -> StopWatchCommand.START_SERVICE
                }

                ServiceHelper.triggerForegroundService(context, command)
            }

            ButtonActions.RESET_BUTTON -> {
                ServiceHelper.triggerForegroundService(context, StopWatchCommand.CANCEL_SERVICE)
            }

            ButtonActions.LAPSE_BUTTON -> {
                timerViewModel.lapse()
            }
        }
    }
    Box {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(R.drawable.dashboard_bg),
            contentDescription = "background_image",
            contentScale = ContentScale.FillHeight
        )
        Column(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                modifier = Modifier
                    .clickable(onClick = {
                        onClickAction.invoke(ButtonActions.RESET_BUTTON)
                        onClick.onClick(1, 1)
                    })
                    .padding(vertical = 16.dp, horizontal = 17.dp)
                    .width(35.dp)
                    .height(35.dp),
                painter = painterResource(id = R.drawable.iv_back),
                contentDescription = "iv_back",
                contentScale = ContentScale.FillBounds
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .offset(y = (-30).dp)
            ) {
                StopWatchCanvas()
                AnimatedVisibility(visible = lapseList.value!!.size > 0) {
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 24.dp, 0.dp)
                            .background(
                                color = Color_45000000,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .border(
                                0.7.dp,
                                color = Color_fedc77,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(all = 10.dp)
                    ) {
                        LapHeader()
                        LazyColumn(
                            reverseLayout = true,
                            modifier = Modifier
                                .height(150.dp)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.Top,
                            state = state
                        ) {
                            itemsIndexed(lapseList.value!!.toList()) { index, item ->
                                LapTimeItem(item, index)
                            }
                        }
                    }
                }
                if (lapseList.value!!.size != 0) {
                    Spacer(modifier = Modifier.padding(top = 30.dp))
                } else {
                    Spacer(modifier = Modifier.padding(top = 196.dp))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(35.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isPause.not()) {
                        val paintPayPauseLap = if (isPlaying) {
                            painterResource(id = R.drawable.lap_enable)
                        } else {
                            painterResource(id = R.drawable.lap_disable)
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clickable(onClick = {
                                    if (isPlaying)
                                        onClickAction.invoke(ButtonActions.LAPSE_BUTTON)
                                })
                                .width(118.dp)
                                .height(40.dp)
                                .paint(
                                    painter = paintPayPauseLap,
                                    contentScale = ContentScale.FillBounds
                                ),
                        ) {
                            Text(
                                text =
                                "Lap", style = DetailScreenTypography.bodyLarge
                            )
                        }
                        val paintPayPause = if (isPlaying) {
                            painterResource(id = R.drawable.stop)
                        } else {
                            painterResource(id = R.drawable.start)
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clickable(onClick = {
                                    onClick.onClick(2, 2)
                                    onClickAction.invoke(
                                        ButtonActions.TOGGLE_PLAY
                                    )
                                })
                                .width(118.dp)
                                .height(40.dp)
                                .paint(
                                    painter = paintPayPause,
                                    contentScale = ContentScale.FillBounds
                                ),
                        ) {
                            Text(
                                text = if (isPlaying) {
                                    "Stop"
                                } else {
                                    "Start"
                                }, style = DetailScreenTypography.bodyLarge
                            )
                        }
                    }
                    if (isPause) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clickable(onClick = {
                                    onClickAction.invoke(ButtonActions.RESET_BUTTON)
                                })
                                .width(118.dp)
                                .height(40.dp)
                                .paint(
                                    painter = painterResource(id = R.drawable.reset),
                                    contentScale = ContentScale.FillBounds
                                ),
                        ) {
                            Text(
                                text =
                                "Reset", style = DetailScreenTypography.bodyLarge
                            )
                        }

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .clickable(onClick = {
                                    onClickAction.invoke(ButtonActions.TOGGLE_PLAY)
                                })
                                .width(118.dp)
                                .height(40.dp)
                                .paint(
                                    painter = painterResource(id = R.drawable.resume),
                                    contentScale = ContentScale.FillBounds
                                ),
                        ) {
                            Text(
                                text =
                                "Resume", style = DetailScreenTypography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FloatingActionButton(isPlaying: Boolean, onClickAction: (action: ButtonActions) -> Unit) {
    FloatingActionButton(
        modifier = Modifier.padding(bottom = 60.dp),
        onClick = {
            onClickAction(ButtonActions.TOGGLE_PLAY)
        },
        containerColor = FloatingActionButtonDefaults.containerColor
    ) {
        Icon(if (isPlaying) Icons.Filled.Call else Icons.Filled.PlayArrow, "play")
    }
}

@SuppressLint("UnrememberedMutableInteractionSource")
@Composable
fun ShareButton() {
    FloatingActionButton(
        modifier = Modifier.padding(bottom = 60.dp),
        onClick = {
        },
        //  backgroundColor = Color.White,
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        interactionSource = MutableInteractionSource()
    ) {
        Icon(Icons.Filled.Share, "play")
    }
}

@Composable
fun LapHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                text = "Lap",
                style = DetailScreenTypography.labelMedium,
                textAlign = TextAlign.Start
            )
            Text(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp),
                text = "Lap Time",
                style = DetailScreenTypography.labelMedium,
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        HorizontalDivider(color = Color_fedc77, thickness = 0.7.dp)
    }
}
