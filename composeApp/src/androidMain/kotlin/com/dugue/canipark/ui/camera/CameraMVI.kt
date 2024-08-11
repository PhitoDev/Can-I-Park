package com.dugue.canipark.ui.camera

import domain.entities.ImageDetails

data class CameraUIState(
    val cameraState: CameraState = CameraState.Idle,
)

sealed interface CameraState {
    data object Idle : CameraState
    data object Loading : CameraState
    data object ShowingCamera : CameraState
    data class ShowingDisclaimer(val message: String) : CameraState
    data class ParkingAllowed(val message: String) : CameraState
    data class ParkingNotAllowed(val message: String): CameraState
    data class Error(val message: String) : CameraState
}

sealed interface CameraEvent {
    data object DisclaimerChecked : CameraEvent
    data class PictureTaken(val imageDetails: ImageDetails) : CameraEvent
    data class PictureError(val message: String) : CameraEvent
    data object MessageDismissed : CameraEvent
}