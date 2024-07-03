package com.qureka.skool.stopwatch.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.qureka.skool.stopwatch.formatTime
import com.qureka.skool.stopwatch.pad
import com.qureka.skool.theme.DetailScreenTypography
import kotlin.time.Duration

@Composable
fun LapTimeItem(item: Duration, index: Int) {
    val text = item.toComponents { hours, minutes, seconds, _ ->
        return@toComponents formatTime(
            hours = hours.toInt().pad(),
            minutes = minutes.pad(),
            seconds = seconds.pad()
        )
    }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 18.7.dp, start = 5.dp, end = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            (index + 1).toString().padStart(2, '0'),
            style = DetailScreenTypography.labelMedium,
            modifier = Modifier.alignByBaseline()
        )
        Text(
            text,
            style = DetailScreenTypography.labelMedium,
        )
    }
}

fun padZero(value: Int): String {
    return "$value".padStart(2, '0')
}
