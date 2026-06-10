package com.letsgotoperfection.kino.core.database.dao

import androidx.room.*
import com.letsgotoperfection.kino.core.database.entity.AttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AttachmentDao {
    @Query("SELECT * FROM attachments WHERE targetId = :targetId AND targetType = :type ORDER BY addedAt DESC")
    fun getAttachments(targetId: String, type: String): Flow<List<AttachmentEntity>>
    
    @Query("SELECT * FROM attachments ORDER BY addedAt DESC")
    fun getAllAttachments(): Flow<List<AttachmentEntity>>
    
    @Query("SELECT * FROM attachments WHERE id = :attachmentId")
    suspend fun getAttachmentById(attachmentId: String): AttachmentEntity?
    
    @Upsert
    suspend fun upsertAttachment(attachment: AttachmentEntity)
    
    @Delete
    suspend fun deleteAttachment(attachment: AttachmentEntity)
    
    @Query("DELETE FROM attachments WHERE targetId = :targetId AND targetType = :type")
    suspend fun deleteAllForTarget(targetId: String, type: String)
}










