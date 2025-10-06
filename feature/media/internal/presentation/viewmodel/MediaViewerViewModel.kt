package com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.DeleteMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.GetMediaByIdUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.NavigateToSourceUseCase
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaViewerEvent
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaViewerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MediaViewerViewModel @Inject constructor(
    private val getMediaByIdUseCase: GetMediaByIdUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase,
    private val navigateToSourceUseCase: NavigateToSourceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val mediaId: String = savedStateHandle["mediaId"] ?: ""

    private val _uiState = MutableStateFlow(MediaViewerUiState())
    val uiState: StateFlow<MediaViewerUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableStateFlow<MediaViewerEvent?>(null)
    val uiEvent: StateFlow<MediaViewerEvent?> = _uiEvent.asStateFlow()

    init {
        if (mediaId.isNotBlank()) {
            loadMedia(mediaId)
        } else {
            _uiState.update { it.copy(isLoading = false, error = "Invalid media identifier") }
        }
    }

    fun loadMedia(id: String = mediaId) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            getMediaByIdUseCase(id)
                .onSuccess { media ->
                    _uiState.update { 
                        MediaViewerUiState(
                            isLoading = false, 
                            media = media,
                            mediaType = MediaType.fromMimeType(media.mimeType)
                        ) 
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Unable to load media"
                        )
                    }
                }
        }
    }

    fun openSource(
        onNavigateToTask: (String) -> Unit,
        onNavigateToNote: (String) -> Unit
    ) {
        val media: Media = _uiState.value.media ?: return
        navigateToSourceUseCase(media, onNavigateToTask, onNavigateToNote)
    }

    fun navigateToSource() {
        val media = _uiState.value.media ?: return
        
        when (media.sourceType) {
            com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType.TASK -> {
                _uiEvent.value = MediaViewerEvent.NavigateToTask(media.sourceId)
            }
            com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType.NOTE -> {
                _uiEvent.value = MediaViewerEvent.NavigateToNote(media.sourceId)
            }
        }
    }

    fun deleteMedia() {
        val media = _uiState.value.media ?: return
        
        viewModelScope.launch {
            deleteMediaUseCase(media.id)
                .onSuccess {
                    _uiEvent.value = MediaViewerEvent.MediaDeleted
                }
                .onFailure { error ->
                    _uiEvent.value = MediaViewerEvent.ShowError(error.message ?: "Failed to delete media")
                }
        }
    }

    fun clearEvent() {
        _uiEvent.value = null
    }
}
