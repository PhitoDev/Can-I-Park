package com.dugue.canipark.ui.camera

import androidx.camera.view.PreviewView

data class CameraUIState(
    val appState: AppState = AppState.Idle,
)

sealed interface AppState {
    data object Idle : AppState
    data object Loading : AppState
    data object ShowingCamera : AppState
    data class ShowingDisclaimer(val message: String) : AppState
    data class ParkingAllowed(val message: String) : AppState
    data class ParkingNotAllowed(val message: String): AppState
    data class Error(val message: String) : AppState
}

sealed interface AppEvent
data object DisclaimerChecked : AppEvent
data class ShowCamera(val view: PreviewView) : AppEvent
data object PictureTaken : AppEvent
data class PictureError(val message: String) : AppEvent
data object MessageDismissed : AppEvent
