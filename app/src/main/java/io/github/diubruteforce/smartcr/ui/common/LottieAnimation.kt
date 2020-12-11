package io.github.diubruteforce.smartcr.ui.common

import android.widget.ImageView
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

@Composable
fun LottieAnimation(
    modifier: Modifier = Modifier,
    @RawRes rawRes: Int
){
    AndroidView(viewBlock = { context ->
        LottieAnimationView(context).apply {
            repeatCount = LottieDrawable.INFINITE
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }, modifier = modifier) { lottieView ->
        lottieView.setAnimation(rawRes)
        lottieView.playAnimation()
    }
}