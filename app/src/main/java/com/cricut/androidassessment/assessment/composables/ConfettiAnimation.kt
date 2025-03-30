package com.cricut.androidassessment.assessment.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import kotlin.random.Random

@Composable
fun ConfettiAnimation(
    modifier: Modifier = Modifier
) {
    val confettiItems = remember {
        List(100) {
            Triple(
                Random.nextFloat() * 360f,
                Random.nextFloat(),
                Random.nextFloat()
            )
        }
    }

    var startAnimation by remember { mutableStateOf(false) }

    val animationValue by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000, easing = LinearEasing),
        label = "confetti_animation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
    }

    Canvas(modifier = modifier) {
        confettiItems.forEach { (rotation, x, y) ->
            val alpha = 1f - (y * animationValue)
            if (alpha > 0) {
                rotate(rotation) {
                    drawRect(
                        color = listOf(
                            Color.Red, Color.Green, Color.Blue,
                            Color.Yellow, Color.Magenta, Color.Cyan
                        ).random().copy(alpha = alpha),
                        topLeft = androidx.compose.ui.geometry.Offset(
                            x * size.width,
                            y * size.height - (animationValue * size.height * 0.8f)
                        ),
                        size = androidx.compose.ui.geometry.Size(8f, 16f)
                    )
                }
            }
        }
    }
}