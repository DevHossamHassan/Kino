package com.letsgotoperfection.kino.feature.media.internal.presentation.viewmodel

import android.app.PendingIntent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.letsgotoperfection.kino.core.common.UiState
import com.letsgotoperfection.kino.feature.media.internal.data.storage.DeleteRequiresUserPermissionException
import com.letsgotoperfection.kino.feature.media.internal.domain.model.Media
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaFilter
import com.letsgotoperfection.kino.feature.media.internal.domain.model.MediaSourceType
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.DeleteMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.GetAllMediaUseCase
import com.letsgotoperfection.kino.feature.media.internal.domain.usecase.NavigateToSourceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Media Manager screen
 * 
 * Manages media state, filtering, view modes, and user actions
 * Uses StateFlow for UI state and SharedFlow for one-time events
 */
@HiltViewModel
internal class MediaManagerViewModel @Inject constructor(
    private val getAllMediaUseCase: GetAllMediaUseCase,
    private val deleteMediaUseCase: DeleteMediaUseCase,
    private val navigateToSourceUseCase: NavigateToSourceUseCase
) : ViewModel() {
    
    // UI State
    private val _uiState = MutableStateFlow<UiState<MediaScreenState>>(UiState.Loading)
    val uiState: StateFlow<UiState<MediaScreenState>> = _uiState.asStateFlow()
    
    // View Mode (Grid/List)
    private val _viewMode = MutableStateFlow(ViewMode.GRID)
    val viewMode: StateFlow<ViewMode> = _viewMode.asStateFlow()
    
    // Filter state
    private val _filter = MutableStateFlow(MediaFilter())
    val filter: StateFlow<MediaFilter> = _filter.asStateFlow()
    
    // One-time events
    private val _uiEvent = MutableSharedFlow<MediaUiEvent>()
    val uiEvent: SharedFlow<MediaUiEvent> = _uiEvent.asSharedFlow()
    
    init {
        loadMedia()
    }
    
    /**
     * Load media with current filter
     */
    fun loadMedia() {
        viewModelScope.launch {
            getAllMediaUseCase(_filter.value)
                .catch { error ->
                    _uiState.value = UiState.Error(
                        error.message ?: "Failed to load media"
                    )
                }
                .collect { mediaList ->
                    _uiState.value = UiState.Success(
                        MediaScreenState(
                            media = mediaList,
                            totalSize = mediaList.sumOf { it.size },
                            totalCount = mediaList.size
                        )
                    )
                }
        }
    }
    
    /**
     * Delete media by ID
     */
    fun deleteMedia(mediaId: String) {
        viewModelScope.launch {
            deleteMediaUseCase(mediaId).fold(
                onSuccess = {
                    // Success - media deleted, state will be updated via Flow
                    _uiEvent.emit(MediaUiEvent.ShowMessage("Media deleted successfully"))
                },
                onFailure = { error ->
                    when (error) {
                        is DeleteRequiresUserPermissionException -> {
                            // Emit event to request user permission
                            _uiEvent.emit(
                                MediaUiEvent.RequiresDeletePermission(error.pendingIntent)
                            )
                        }
                        else -> {
                            _uiEvent.emit(
                                MediaUiEvent.ShowError(
                                    error.message ?: "Failed to delete media"
                                )
                            )
                        }
                    }
                }
            )
        }
    }
    
    /**
     * Toggle between grid and list view
     */
    fun toggleViewMode() {
        _viewMode.value = when (_viewMode.value) {
            ViewMode.GRID -> ViewMode.LIST
            ViewMode.LIST -> ViewMode.GRID
        }
    }
    
    /**
     * Update filter criteria
     */
    fun updateFilter(filter: MediaFilter) {
        _filter.value = filter
        loadMedia()
    }
    
    /**
     * Navigate to source of media
     */
    fun navigateToSource(
        media: Media,
        onNavigateToTask: (String) -> Unit,
        onNavigateToNote: (String) -> Unit
    ) {
        navigateToSourceUseCase(media, onNavigateToTask, onNavigateToNote)
    }
    
    /**
     * Refresh media list
     */
    fun refresh() {
        loadMedia()
    }
    
    /**
     * Clear current filter
     */
    fun clearFilter() {
        _filter.value = MediaFilter()
        loadMedia()
    }
}

/**
 * UI State for Media Manager screen
 */
internal data class MediaScreenState(
    val media: List<Media>,
    val totalSize: Long,
    val totalCount: Int
)

/**
 * View mode for media display
 */
internal enum class ViewMode {
    GRID,
    LIST
}

/**
 * One-time UI events
 */
internal sealed class MediaUiEvent {
    data class ShowMessage(val message: String) : MediaUiEvent()
    data class ShowError(val message: String) : MediaUiEvent()
    data class RequiresDeletePermission(val pendingIntent: PendingIntent) : MediaUiEvent()
}
