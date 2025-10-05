package com.letsgotoperfection.kino.core.database.dao

import androidx.room.*
import com.letsgotoperfection.kino.core.database.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChecklistDao {
    @Query("SELECT * FROM checklist_items WHERE taskId = :taskId ORDER BY `order`")
    fun getChecklistItems(taskId: String): Flow<List<ChecklistItemEntity>>
    
    @Upsert
    suspend fun upsertChecklistItem(item: ChecklistItemEntity)
    
    @Delete
    suspend fun deleteChecklistItem(item: ChecklistItemEntity)
    
    @Query("UPDATE checklist_items SET isCompleted = :completed WHERE id = :itemId")
    suspend fun updateCompletion(itemId: String, completed: Boolean)
    
    @Query("UPDATE checklist_items SET `order` = :order WHERE id = :itemId")
    suspend fun updateOrder(itemId: String, order: Int)
    
    @Query("DELETE FROM checklist_items WHERE taskId = :taskId")
    suspend fun deleteAllForTask(taskId: String)
    
    @Query("SELECT * FROM checklist_items WHERE id = :itemId")
    suspend fun getById(itemId: String): ChecklistItemEntity?
    
    @Query("SELECT COUNT(*) FROM checklist_items WHERE taskId = :taskId")
    suspend fun getCount(taskId: String): Int
}
