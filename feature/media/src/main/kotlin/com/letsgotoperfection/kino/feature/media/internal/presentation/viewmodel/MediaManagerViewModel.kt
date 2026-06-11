package com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.resources.R
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaFilter
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaType
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.DeleteMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.GetAllMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.NavigateToSourceUseCase
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaManagerEvent
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.MediaManagerUiState
import com.letsgotoperfection.kino.feature.media.internal.presentation.state.ViewMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the media manager screen.
 *
 * Observes media reactively, supports type filtering, grid/list toggling
 * and deletion with one-time snackbar events.
 */
@HiltViewModel
internal class MediaManagerViewModel @Inject constructor(
    private val getAllMediaUseCase: GetAllMediaUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase,
    private val navigateToSourceUseCase: NavigateToSourceUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MediaManagerUiState())
    val uiState: StateFlow<MediaManagerUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<MediaManagerEvent>(Channel.BUFFERED)
    val uiEvent: Flow<MediaManagerEvent> = _uiEvent.receiveAsFlow()

    private val typeFilter = MutableStateFlow<MediaType?>(null)

    init {
        observeMedia()
    }

    fun setTypeFilter(type: MediaType?) {
        typeFilter.value = type
        _uiState.update { it.copy(typeFilter = type) }
    }

    fun toggleViewMode() {
        _uiState.update {
            it.copy(
                viewMode = when (it.viewMode) {
                    ViewMode.GRID -> ViewMode.LIST
                    ViewMode.LIST -> ViewMode.GRID
                }
            )
        }
    }

    fun deleteMedia(mediaId: String) {
        viewModelScope.launch {
            deleteMediaUseCase(mediaId).fold(
                onSuccess = {
                    _uiEvent.trySend(
                        MediaManagerEvent.ShowMessage(R.string.media_deleted_successfully)
                    )
                },
                onFailure = {
                    _uiEvent.trySend(
                        MediaManagerEvent.ShowMessage(R.string.media_delete_failed)
                    )
                }
            )
        }
    }

    fun navigateToSource(
        media: Media,
        onNavigateToTask: (String) -> Unit,
        onNavigateToNote: (String) -> Unit
    ) {
        navigateToSourceUseCase(media, onNavigateToTask, onNavigateToNote)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeMedia() {
        viewModelScope.launch {
            typeFilter
                .flatMapLatest { type ->
                    if (type == null) {
                        getAllMediaUseCase()
                    } else {
                        getAllMediaUseCase(MediaFilter(type = type))
                    }
                }
                .catch {
                    _uiState.update {
                        it.copy(isLoading = false, errorRes = R.string.media_error_loading)
                    }
                }
                .collect { mediaList ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            media = mediaList,
                            totalSize = mediaList.sumOf { media -> media.size },
                            errorRes = null
                        )
                    }
                }
        }
    }
}
