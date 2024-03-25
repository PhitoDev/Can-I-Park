package com.dugue.canipark.ui.camera

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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.dugue.canipark.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

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
    onDismiss: () -> Unit = {},
    onAdViewReady: (adView: AdView) -> Unit = {}
) {
    when (cameraState) {
        is CameraState.Error -> MessageDialog(
            message = cameraState.message,
            isPositive = false,
            onDismiss = onDismiss
        )
        CameraState.Idle -> CameraUI(
            onCameraReady = onCameraReady,
            onPictureTaken = onPictureTaken,
            onAdViewReady = onAdViewReady
        )
        CameraState.Loading -> LoadingDialog()
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
    onPictureTaken: () -> Unit = {},
    onAdViewReady: (adView: AdView) -> Unit = {}
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
        AndroidView(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            factory = { context ->
                AdView(context).apply {
                    adUnitId = "ca-app-pub-2138105660848240/1475290586"
                    setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { view ->
                onAdViewReady(view)
            }
        )
        NavigationBar (
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .alpha(0.3f),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = onPictureTaken,
                    modifier = Modifier.size(64.dp).alpha(0.8f),
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.camera),
                        contentDescription = "Take a picture",
                        tint = MaterialTheme.colorScheme.onPrimary
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
                        if (isPositive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge
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

@Composable
private fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter).padding(8.dp),
                factory = { context ->
                    AdView(context).apply {
                        adUnitId = "ca-app-pub-2138105660848240/7041466126"
                        setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                        loadAd(AdRequest.Builder().build())
                    }
                },
                update = { view ->
                    // onAdViewReady(view)
                }
            )
            Card(
                modifier = Modifier.padding(16.dp).align(Alignment.Center),
                shape = RoundedCornerShape(8.dp),
            ) { // Card to give a background color
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Analyzing...", style = MaterialTheme.typography.titleLarge)
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.height(50.dp).width(50.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp
                    )
                }
            }
            AndroidView(
                modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).padding(8.dp),
                factory = { context ->
                    AdView(context).apply {
                        adUnitId = "ca-app-pub-2138105660848240/1597567752"
                        setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                        loadAd(AdRequest.Builder().build())
                    }
                },
                update = { view ->
                    // onAdViewReady(view)
                }
            )
        }
    }
}