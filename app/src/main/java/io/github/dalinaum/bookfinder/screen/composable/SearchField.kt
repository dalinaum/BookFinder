package io.github.dalinaum.bookfinder.screen.composable

import android.view.KeyEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.unit.dp
import io.github.dalinaum.bookfinder.ui.theme.WColorLight
import io.github.dalinaum.bookfinder.ui.theme.WColorLight3

@Composable
fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    onSearchButtonClicked: () -> Unit
) {
    Surface(
        elevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .background(WColorLight)
                .padding(16.dp, 8.dp, 16.dp, 12.dp)
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChanged,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f)
                    .onKeyEvent {
                        if (it.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_ENTER) {
                            onSearchButtonClicked()
                            true
                        } else {
                            false
                        }
                    },

                label = {
                    Text(
                        text = "검색어를 입력해주세요."
                    )
                },
                singleLine = true,
                colors = TextFieldDefaults.textFieldColors(
                    backgroundColor = WColorLight3,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.DarkGray,
                    cursorColor = Color.Black
                )
            )
            IconButton(
                onClick = onSearchButtonClicked,
                enabled = query.isNotBlank(),
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "검색"
                )
            }
        }
    }
}