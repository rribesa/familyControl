package br.com.rribesa.familycontrol.feature.finance.impl.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Category
import java.util.UUID

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    val name: String,
    val userId: String,
    val isSynced: Boolean = false
) {
    fun toDomain(): Category = Category(
        id = UUID.fromString(id),
        name = name,
        userId = userId
    )

    companion object {
        fun fromDomain(category: Category, isSynced: Boolean = false): CategoryEntity = CategoryEntity(
            id = category.id.toString(),
            name = category.name,
            userId = category.userId,
            isSynced = isSynced
        )
    }
}
