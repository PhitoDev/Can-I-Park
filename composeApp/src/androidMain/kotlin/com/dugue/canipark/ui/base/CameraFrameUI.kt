package com.dugue.canipark.ui.base

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun CameraFrameUIPreview() {
    CameraFrameUI()
}

@Composable
fun CameraFrameUI(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize().padding(16.dp).background(color = Color.Transparent)
    ) {
        RightAngle(modifier = Modifier.size(100.dp).rotate(270f).align(Alignment.TopStart))
        RightAngle(modifier = Modifier.size(100.dp).rotate(0f).align(Alignment.TopEnd))
        RightAngle(modifier = Modifier.size(100.dp).rotate(180f).align(Alignment.BottomStart))
        RightAngle(modifier = Modifier.size(100.dp).rotate(90f).align(Alignment.BottomEnd))

    }
}

@Composable
private fun RightAngle(modifier: Modifier = Modifier) {
    // Create a canvas that draws a border with a right angle
    Canvas(modifier = modifier.fillMaxSize()) {
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(size.width, 0f)
            lineTo(size.width, size.height)
        }
        drawPath(path, color = Color(0xFF625B71), style = Stroke(width = 45f))
    }
}