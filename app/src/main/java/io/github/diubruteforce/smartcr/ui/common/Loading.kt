package io.github.diubruteforce.smartcr.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.diubruteforce.smartcr.R

@Composable
fun Loading(){
    Dialog(onDismissRequest = { }) {
        Card {
            LottieAnimation(
                modifier = Modifier.size(100.dp),
                rawRes = R.raw.rolling_pencil
            )
        }
    }
}