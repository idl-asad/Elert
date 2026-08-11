package com.example.elert.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RuleDao {

    @Query("SELECT * FROM rules ORDER BY title ASC")
    fun observeAll(): Flow<List<RuleEntity>>

    @Query("SELECT * FROM rules")
    suspend fun getAll(): List<RuleEntity>

    @Query("SELECT COUNT(*) FROM rules")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: RuleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rules: List<RuleEntity>): List<Long>

    @Query("DELETE FROM rules WHERE id = :id")
    suspend fun deleteById(id: String): Int

    @Query("UPDATE rules SET isEnabled = NOT isEnabled WHERE id = :id")
    suspend fun toggleEnabled(id: String): Int
}
