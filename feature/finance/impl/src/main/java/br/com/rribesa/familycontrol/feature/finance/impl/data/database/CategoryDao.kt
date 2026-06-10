package br.com.rribesa.familycontrol.feature.finance.impl.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY name ASC")
    fun getCategories(userId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE isSynced = 0 AND userId = :userId")
    fun getUnsyncedCategories(userId: String): List<CategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCategories(categories: List<CategoryEntity>): List<Long>

    @Query("UPDATE categories SET isSynced = 1 WHERE id = :id")
    fun markSynced(id: String): Int
}
