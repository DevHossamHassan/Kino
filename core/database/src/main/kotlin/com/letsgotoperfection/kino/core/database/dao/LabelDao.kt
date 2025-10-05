package com.letsgotoperfection.kino.core.database.dao

import androidx.room.*
import com.letsgotoperfection.kino.core.database.entity.LabelEntity
import com.letsgotoperfection.kino.core.database.entity.TaskLabelCrossRef
import com.letsgotoperfection.kino.core.database.entity.NoteLabelCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface LabelDao {
    @Query("SELECT * FROM labels ORDER BY name")
    fun getAllLabels(): Flow<List<LabelEntity>>
    
    @Upsert
    suspend fun upsertLabel(label: LabelEntity)
    
    @Delete
    suspend fun deleteLabel(label: LabelEntity)
    
    @Query("""
        SELECT labels.* FROM labels 
        INNER JOIN task_labels ON labels.id = task_labels.labelId 
        WHERE task_labels.taskId = :taskId
    """)
    fun getTaskLabels(taskId: String): Flow<List<LabelEntity>>
    
    @Query("""
        SELECT labels.* FROM labels 
        INNER JOIN note_labels ON labels.id = note_labels.labelId 
        WHERE note_labels.noteId = :noteId
    """)
    fun getNoteLabels(noteId: String): Flow<List<LabelEntity>>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTaskLabel(crossRef: TaskLabelCrossRef)
    
    @Delete
    suspend fun removeTaskLabel(crossRef: TaskLabelCrossRef)
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addNoteLabel(crossRef: NoteLabelCrossRef)
    
    @Delete
    suspend fun removeNoteLabel(crossRef: NoteLabelCrossRef)
    
    @Query("DELETE FROM task_labels WHERE taskId = :taskId")
    suspend fun removeAllTaskLabels(taskId: String)
    
    @Query("DELETE FROM note_labels WHERE noteId = :noteId")
    suspend fun removeAllNoteLabels(noteId: String)
}





