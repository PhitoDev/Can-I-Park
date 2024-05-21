package com.dugue.canipark.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import com.dugue.canipark.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

@Composable
fun LoadingDialog() {
    Dialog(onDismissRequest = {}) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.padding(16.dp).align(Alignment.Center),
                shape = RoundedCornerShape(8.dp),
            ) {
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
        }
    }
}

@Composable
fun MessageDialog(
    message: String,
    isPositive: Boolean,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = { onDismiss() }) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.TopCenter),
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
                modifier = Modifier
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
                        modifier = Modifier.padding(8.dp),
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onDismiss
                    ) {
                        Text("OK")
                    }
                }
            }
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.BottomCenter),
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