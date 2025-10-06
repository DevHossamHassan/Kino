package com.letsgotoperfection.kino.core.database.dao

import androidx.room.*
import com.letsgotoperfection.kino.core.database.entity.SectionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SectionDao {
    @Query("SELECT * FROM sections ORDER BY orderIndex ASC")
    fun getSections(): Flow<List<SectionEntity>>

    @Query("SELECT * FROM sections WHERE id = :id")
    fun observeById(id: String): Flow<SectionEntity?>

    @Upsert
    suspend fun upsert(section: SectionEntity)

    @Query("UPDATE sections SET name = :name WHERE id = :id")
    suspend fun rename(id: String, name: String)

    @Query("UPDATE sections SET orderIndex = :orderIndex WHERE id = :id")
    suspend fun updateOrder(id: String, orderIndex: Int)

    @Delete
    suspend fun delete(section: SectionEntity)
}



