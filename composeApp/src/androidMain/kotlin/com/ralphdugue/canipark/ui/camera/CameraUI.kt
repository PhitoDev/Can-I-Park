package com.ralphdugue.canipark.ui.camera

import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.ralphdugue.canipark.R

@Composable
@Preview
fun CameraUIPreview() {
    CameraUI()
}

@Composable
fun CameraScreen(
    cameraState: CameraState,
    onCameraReady: (view: PreviewView) -> Unit = {},
    onPictureTaken: () -> Unit = {},
    onDismiss: () -> Unit = {}
) {
    when (cameraState) {
        is CameraState.Error -> MessageDialog(
            message = cameraState.message,
            isPositive = false,
            onDismiss = onDismiss
        )
        CameraState.Idle -> CameraUI(
            onCameraReady = onCameraReady,
            onPictureTaken = onPictureTaken
        )
        CameraState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.height(100.dp).width(100.dp),
                    color = MaterialTheme.colors.primary,
                    strokeWidth = 15.dp
                )
            }
        }
        is CameraState.ParkingNotAllowed -> MessageDialog(
            message = cameraState.message,
            isPositive = false,
            onDismiss = onDismiss
        )
        is CameraState.ParkingAllowed -> MessageDialog(
            message = cameraState.message,
            isPositive = true,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun CameraUI(
    onCameraReady: (view: PreviewView) -> Unit = {},
    onPictureTaken: () -> Unit = {}
) {
    Box {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                PreviewView(context)
            },
            update = { view ->
                onCameraReady(view)
            }
        )
        BottomAppBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .alpha(0.5f)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { // Column to center the button
                IconButton(
                    onClick = onPictureTaken,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .background(
                            color = MaterialTheme.colors.onPrimary,
                            shape = RoundedCornerShape(25)
                        )
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.camera),
                        contentDescription = "Take picture",
                        tint = MaterialTheme.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun MessageDialog(
    message: String,
    isPositive: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    imageVector = if (isPositive) ImageVector.vectorResource(id = R.drawable.ic_check) else ImageVector.vectorResource(id = R.drawable.ic_error),
                    contentDescription = "Result",
                    modifier = Modifier.size(96.dp),
                    colorFilter = ColorFilter.tint(
                        if (isPositive) MaterialTheme.colors.primary else MaterialTheme.colors.error
                    )
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.body1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onDismiss
                ) {
                    Text("OK")
                }
            }
        }
    }
}