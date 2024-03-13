package com.dugue.canipark.ui.camera

import domain.entities.BitmapRequest
import domain.entities.ParkingRequest

data class CameraUIState(
    val cameraState: CameraState = CameraState.Idle,
)

sealed interface CameraState {
    object Idle : CameraState
    object Loading : CameraState
    data class ParkingAllowed(val message: String) : CameraState
    data class ParkingNotAllowed(val message: String): CameraState
    data class Error(val message: String) : CameraState
}

sealed interface CameraEvent {
    data class PictureTaken(val parkingRequest: ParkingRequest) : CameraEvent
    data class PictureTakenBitmap(val bitmapRequest: BitmapRequest) : CameraEvent
    data class PictureError(val message: String) : CameraEvent
    data object ResultDismissed : CameraEvent
}