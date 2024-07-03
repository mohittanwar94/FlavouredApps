package com.qureka.skool.todo.composables

import android.graphics.Typeface
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.TypedValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.indicatorLine
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.qureka.skool.R
import com.qureka.skool.theme.AddEditNotesTypography
import com.qureka.skool.theme.Color_1c1c1c
import com.qureka.skool.theme.Color_Black
import com.qureka.skool.theme.Color_ffaaaa
import com.qureka.skool.utils.LineEditText
import com.qureka.skool.utils.OnRecyclerViewClick
import com.qureka.skool.viewmodel.AddEditViewModel


private const val blockCharacterSet = "~#^|$%&*!"

private val filter =
    InputFilter { source, start, end, dest, dstart, dend ->
        if (source != null && blockCharacterSet.contains(("" + source))) {
            return@InputFilter ""
        }
        null
    }

@Composable
fun EditNoteContent(toDoViewModel: AddEditViewModel, onClick: OnRecyclerViewClick) {
    val textDescription = toDoViewModel.textDescription.observeAsState().value ?: ""
    val textTitle = toDoViewModel.textTitle.observeAsState().value ?: ""
    Surface(
        modifier = Modifier.fillMaxSize(1f),
    ) {
        Box {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.dashboard_bg),
                contentDescription = "background_image",
                contentScale = ContentScale.Crop
            )
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
            Column(
                modifier = Modifier
                    .padding(start = 26.dp, end = 26.dp, top = 81.dp, bottom = 35.dp)
                    .background(color = Color_ffaaaa, shape = RoundedCornerShape(20.dp)),
                verticalArrangement = Arrangement.Top
            ) {

                Row(
                    modifier = Modifier
                        .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 26.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(id = R.string.title),
                        style = AddEditNotesTypography.titleLarge,
                    )
                    Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                    EditTextTitle(textTitle, toDoViewModel)
                }
                EditTextDescription(textDescription, toDoViewModel)
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditTextTitle(
    textSearch: String,
    toDoViewModel: AddEditViewModel,
) {
    val buttonInteractionSource = remember { MutableInteractionSource() }
    BasicTextField(value = textSearch,
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(bottom = 10.dp)
            .height(30.dp)
            .indicatorLine(
                enabled = true,
                isError = false,
                interactionSource = buttonInteractionSource,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color_1c1c1c,
                    disabledIndicatorColor = Color_1c1c1c,
                    unfocusedIndicatorColor = Color_1c1c1c
                ),
                focusedIndicatorLineThickness = Dp(1f),
                unfocusedIndicatorLineThickness = Dp(1f)
            ),
        onValueChange = { toDoViewModel.updateTitleText(it) },
        singleLine = true,
        cursorBrush = SolidColor(Color_Black),
        textStyle = AddEditNotesTypography.labelMedium,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(1f).padding(top = 10.dp)) {
                    if (textSearch.isEmpty()) {
                        Text(
                            stringResource(id = R.string.enter_text),
                            style = AddEditNotesTypography.labelSmall
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun EditTextDescription(
    textSearch: String,
    toDoViewModel: AddEditViewModel,
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 14.dp)
            .verticalScroll(rememberScrollState())
    ) {
        val lDisplayMetrics = LocalContext.current.resources.displayMetrics
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(1f),
            factory = { ctx ->
                LineEditText(ctx, null).apply {
                    setColor(Color_1c1c1c.value.toInt())
                    filters = arrayOf(filter)
                    setStroke(5f)
                    setHeightLine(70)
                    setPadding(0, 0, 0, 0)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        lineHeight = 70
                    }
                    // maxLines=100
                    setText(textSearch)
                    setBackgroundDrawable(null)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                    setTypeface(
                        Typeface.createFromAsset(
                            context.assets,
                            "fonts/poppins_medium.ttf"
                        )
                    )
                    setHint(ctx.getString(R.string.enter_text))
                    addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {

                        }

                        override fun onTextChanged(
                            s: CharSequence?,
                            start: Int,
                            before: Int,
                            count: Int
                        ) {
                            toDoViewModel.updateDescriptionText(s.toString())
                        }

                        override fun beforeTextChanged(
                            s: CharSequence?,
                            start: Int,
                            count: Int,
                            after: Int
                        ) {

                        }
                    })
                }
            })
    }
}