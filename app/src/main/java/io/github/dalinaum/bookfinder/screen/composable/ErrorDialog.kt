package io.github.dalinaum.bookfinder.screen.composable

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ErrorDialog(
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
                Column {
                    Text(
                        text = "에러가 발생했습니다.\n다시 시도해주세요.",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(
                        modifier = Modifier.size(8.dp)
                    )
                    Text(
                        text = message,
                        fontSize = 16.sp
                    )
                }
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