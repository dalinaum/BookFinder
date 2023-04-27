package io.github.dalinaum.bookfinder.screen.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Error(
    message: String,
    onConfirmButtonClicked: () -> Unit = {}
) {
    var shouldShowDialog by remember {
        mutableStateOf(true)
    }
    if (shouldShowDialog) {
        AlertDialog(
            onDismissRequest = {
            },
            text = {
                Text(
                    text = "에러가 발생했습니다.\n다시 시도해주세요.\n\n$message",
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
            },
            title = {
                Text("에러")
            },
            confirmButton = {
                Button(
                    onClick = {
                        shouldShowDialog = false
                        onConfirmButtonClicked.invoke()
                    }
                ) {
                    Text(
                        text = "확인"
                    )
                }
            }
        )
    }
}