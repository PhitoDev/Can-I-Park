package com.dugue.canipark.ui.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics
import domain.entities.ParkingResponse
import domain.repositories.CameraRepository
import domain.repositories.PreferencesRepository
import domain.repositories.LLMRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CameraViewModel(
    private val preferencesRepository: PreferencesRepository,
    private val cameraRepository: CameraRepository,
    private val repository: LLMRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CameraUIState())
    val uiState: StateFlow<CameraUIState> = _uiState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        _uiState.value
    )

    private val _events = MutableSharedFlow<AppEvent>()
    private val events = _events.asSharedFlow()

    init {
        subscribeToEvents()
    }

    fun onEvent(event: AppEvent) {
        viewModelScope.launch(Dispatchers.IO) {
            _events.emit(event)
        }
    }

    private fun subscribeToEvents() {
        events.onEach { event ->
            when (event) {
                PictureTaken -> pictureTaken()
                MessageDismissed -> dismissResult()
                is PictureError -> _uiState.update {
                    it.copy(appState = AppState.Error(event.message))
                }
                DisclaimerChecked -> disclaimerChecked()
                is ShowCamera -> {
                    _uiState.update { it.copy(appState = AppState.ShowingCamera) }
                    cameraRepository.startCamera(event.view)
                }
            }
        }
            .flowOn(Dispatchers.IO)
            .launchIn(viewModelScope)
    }

    private suspend fun disclaimerChecked() {
        val userHasSeen = preferencesRepository.hasUserSeenDisclaimer().getOrDefault(false)
        if (!userHasSeen) {
            _uiState.update {
                val disclaimer = preferencesRepository.getDisclaimer()
                it.copy(appState = AppState.ShowingDisclaimer(disclaimer.message))
            }
            preferencesRepository.markDisclaimerShown()
        } else {
            _uiState.update { it.copy(appState = AppState.ShowingCamera) }
        }
    }

    private suspend fun pictureTaken() {
        _uiState.update { it.copy(appState = AppState.Loading) }
        runCatching {
            val imageDetails = cameraRepository.takePicture().getOrThrow()
            val result = repository.analyzeImage(imageDetails).getOrThrow()
            _uiState.update {
                it.copy(
                    appState = if (result.canIPark) {
                        AppState.ParkingAllowed(buildMessage(result))
                    } else {
                        AppState.ParkingNotAllowed(buildMessage(result))
                    }
                )
            }
        }
            .onFailure { exception ->
                Firebase.crashlytics.recordException(exception)
                _uiState.update {
                    it.copy(appState = AppState.Error(exception.localizedMessage ?: "Unknown error"))
                }
            }
    }

    private fun dismissResult() {
        _uiState.update { it.copy(appState = AppState.ShowingCamera) }
    }

    private fun buildMessage(parkingResponse: ParkingResponse): String {
        val builder = StringBuilder()
        if (parkingResponse.canIPark) {
            builder.append("You can park here")
            if (parkingResponse.howLong != null) {
                builder.append(" for ${parkingResponse.howLong}.")
            }
            if (parkingResponse.cost != null) {
                builder.append(" It will cost you ${parkingResponse.cost}.")
            }
            if (parkingResponse.restrictions != null) {
                builder.append(" ${parkingResponse.restrictions}.")
            }
        } else {
            builder.append("You cannot park here.")
            if (parkingResponse.reasonIfNo != null) {
                builder.append(" ${parkingResponse.reasonIfNo}.")
            }
        }
        return builder.toString()
    }
}
