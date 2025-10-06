package com.letsgotoperfection.kino.feature.media.api

import com.letsgotoperfection.kino.core.common.Result
import com.letsgotoperfection.kino.core.model.MediaFile
import kotlinx.coroutines.flow.Flow

/**
 * Public API for Media feature.
 * 
 * This API allows other feature modules to:
 * - Query media files
 * - Upload/delete media
 * - Attach media to tasks/notes
 * 
 * @since 1.0.0
 * @see com.letsgotoperfection.kino.feature.kanban.api.KanbanApi for task operations
 * @see com.letsgotoperfection.kino.feature.notes.api.NotesApi for note operations
 */
interface MediaApi {
    
    /**
     * Retrieves a media file by its unique identifier.
     * 
     * @param mediaId The unique media identifier
     * @return Result containing the MediaFile or an error
     */
    suspend fun getMediaFile(mediaId: String): Result<MediaFile>
    
    /**
     * Uploads a new media file
     * 
     * @param mediaFile The media file to upload
     * @return Result containing the uploaded media ID or an error
     */
    suspend fun uploadMedia(mediaFile: MediaFile): Result<String>
    
    /**
     * Deletes a media file by ID
     * 
     * @param mediaId The media ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteMedia(mediaId: String): Result<Unit>
    
    /**
     * Attaches media to a task
     * 
     * @param mediaId The media ID to attach
     * @param taskId The task ID to attach to
     * @return Result indicating success or failure
     */
    suspend fun attachMediaToTask(mediaId: String, taskId: String): Result<Unit>
    
    /**
     * Attaches media to a note
     * 
     * @param mediaId The media ID to attach
     * @param noteId The note ID to attach to
     * @return Result indicating success or failure
     */
    suspend fun attachMediaToNote(mediaId: String, noteId: String): Result<Unit>
    
    
    /**
     * Get observable media updates
     * 
     * @return Flow of media update events
     */
    fun observeMediaUpdates(): Flow<MediaUpdate>
}

/**
 * Media update event for cross-feature communication
 */
data class MediaUpdate(
    val mediaId: String,
    val type: UpdateType,
    val timestamp: Long = System.currentTimeMillis()
)

enum class UpdateType {
    UPLOADED, DELETED, ATTACHED_TO_TASK, ATTACHED_TO_NOTE, DETACHED
}

