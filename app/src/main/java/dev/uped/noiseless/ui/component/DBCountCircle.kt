package dev.uped.noiseless.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.uped.noiseless.ui.theme.Green500
import dev.uped.noiseless.ui.theme.Red500
import dev.uped.noiseless.ui.theme.Red700
import dev.uped.noiseless.ui.theme.Yellow500

fun getColorForLoudness(dB: Double): Color {
    return when {
        dB <= 40 -> Green500
        dB <= 70 -> Yellow500
        dB <= 90 -> Red500
        else -> Red700
    }
}

@Composable
fun DBCountCircle(dB: Double, isActive: Boolean) {
    val color by animateColorAsState(targetValue = getColorForLoudness(dB))
    val infiniteTransition = rememberInfiniteTransition()
    Box(modifier = Modifier.size(270.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(170.dp)
                .clip(
                    CircleShape
                )
                .background(color)
                .zIndex(1.0f),
            contentAlignment = Alignment.Center

        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    String.format("%.1f", dB),
                    color = MaterialTheme.colors.onPrimary,
                    style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    "dB",
                    color = MaterialTheme.colors.onPrimary.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.body1
                )
            }
        }
        if (isActive) {
            AnimatedCircleIndicator(
                infiniteTransition = infiniteTransition,
                color = color,
                minSize = 170.dp,
                maxSize = 270.dp,
                delay = 0,
                duration = 1000
            )
        }
    }
}

@Composable
fun AnimatedCircleIndicator(
    infiniteTransition: InfiniteTransition,
    color: Color,
    minSize: Dp,
    maxSize: Dp,
    delay: Int,
    duration: Int
) {

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.8F,
        targetValue = 0.4F,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        )
    )
    val size by infiniteTransition.animateValue(
        initialValue = minSize,
        targetValue = maxSize,
        typeConverter = Dp.VectorConverter,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, delayMillis = delay),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(
        modifier = Modifier
            .size(size)
            .clip(
                CircleShape
            )
            .background(color.copy(alpha = alpha))
    )
}