package com.dugue.canipark.ui.camera

import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.dugue.canipark.R
import com.dugue.canipark.ui.base.CameraFrameUI
import com.dugue.canipark.ui.base.LoadingDialog
import com.dugue.canipark.ui.base.MessageDialog
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

@Composable
@Preview
fun CameraUIPreview() {
    CameraUI()
}

@Composable
fun CameraScreen(
    appState: AppState,
    onEvent: (event: AppEvent) -> Unit,
) {
    when (appState) {
        is AppState.Error -> MessageDialog(
            message = stringResource(R.string.image_analysis_error),
            isPositive = false,
            onEvent = onEvent
        )
        AppState.Idle -> {
            Box {  }
        }
        AppState.Loading -> LoadingDialog()
        is AppState.ParkingNotAllowed -> MessageDialog(
            message = appState.message,
            isPositive = false,
            onEvent = onEvent
        )
        is AppState.ParkingAllowed -> MessageDialog(
            message = appState.message,
            isPositive = true,
            onEvent = onEvent
        )

        is AppState.ShowingDisclaimer -> MessageDialog(
            message = appState.message,
            isPositive = false,
            onEvent = onEvent
        )

        AppState.ShowingCamera -> CameraUI(onEvent = onEvent)
    }
}

@Composable
private fun CameraUI(
    onEvent: (event: AppEvent) -> Unit = {},
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .weight(1f),
            factory = { context ->
                AdView(context).apply {
                    adUnitId = "ca-app-pub-2138105660848240/1475290586"
                    setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                    loadAd(AdRequest.Builder().build())
                }
            },
            update = { view ->
                //onAdViewReady(view)
            }
        )
        CameraFrame(
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .weight(9f)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PreviewView(context)
                },
                update = { view ->
                    onEvent(ShowCamera(view))
                }
            )
        }
        NavigationBar (
            modifier = Modifier
                .fillMaxWidth()
                .height(65.dp)
                .weight(1f),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FloatingActionButton(
                    onClick = { onEvent(PictureTaken) },
                    modifier = Modifier.size(64.dp).alpha(0.8f),
                    containerColor = MaterialTheme.colorScheme.primary,
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.camera),
                        contentDescription = "Take picture",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraFrame(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .background(Color.Transparent)
    ) {
        content()
        CameraFrameUI()
    }
}