package com.letsgotoperfection.kino.core.common.event

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Event bus for decoupled cross-feature communication
 */
@Singleton
class EventBus @Inject constructor() {
    private val _events = MutableSharedFlow<AppEvent>(
        replay = 0,
        extraBufferCapacity = 10,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    
    val events: SharedFlow<AppEvent> = _events.asSharedFlow()
    
    suspend fun emit(event: AppEvent) {
        _events.emit(event)
    }
}

