package com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.DeleteMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.GetMediaByIdUseCase
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaViewerEvent
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaViewerUiState
import com.letsgotoperfection.kino.feature.media.navigation.MediaViewerRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the media viewer screen.
 */
@HiltViewModel
internal class MediaViewerViewModel @Inject constructor(
    private val getMediaByIdUseCase: GetMediaByIdUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaId: String = savedStateHandle.toRoute<MediaViewerRoute>().mediaId

    private val _uiState = MutableStateFlow(MediaViewerUiState())
    val uiState: StateFlow<MediaViewerUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<MediaViewerEvent>(Channel.BUFFERED)
    val uiEvent: Flow<MediaViewerEvent> = _uiEvent.receiveAsFlow()

    init {
        loadMedia()
    }

    fun loadMedia() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorRes = null) }
            getMediaByIdUseCase(mediaId).fold(
                onSuccess = { media ->
                    _uiState.value = MediaViewerUiState(
                        isLoading = false,
                        media = media,
                        mediaType = MediaType.fromMimeType(media.mimeType)
                    )
                },
                onFailure = {
                    _uiState.update {
                        it.copy(isLoading = false, errorRes = R.string.media_error_loading)
                    }
                }
            )
        }
    }

    fun navigateToSource() {
        val media = _uiState.value.media ?: return
        val event = when (media.sourceType) {
            MediaSourceType.TASK -> MediaViewerEvent.NavigateToTask(media.sourceId)
            MediaSourceType.NOTE -> MediaViewerEvent.NavigateToNote(media.sourceId)
        }
        _uiEvent.trySend(event)
    }

    fun deleteMedia() {
        val media = _uiState.value.media ?: return
        viewModelScope.launch {
            deleteMediaUseCase(media.id).fold(
                onSuccess = { _uiEvent.trySend(MediaViewerEvent.MediaDeleted) },
                onFailure = {
                    _uiEvent.trySend(
                        MediaViewerEvent.ShowMessage(R.string.media_delete_failed)
                    )
                }
            )
        }
    }
}
