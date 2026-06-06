package br.com.rribesa.familycontrol.feature.finance.impl.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import java.util.UUID

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val amount: Double,
    val category: String,
    val date: Long,
    val description: String,
    val userId: String,
    val isSynced: Boolean = false
) {
    fun toDomain(): Transaction = Transaction(
        id = UUID.fromString(id),
        amount = amount,
        category = category,
        date = date,
        description = description,
        userId = userId
    )

    companion object {
        fun fromDomain(transaction: Transaction, isSynced: Boolean = false): TransactionEntity = TransactionEntity(
            id = transaction.id.toString(),
            amount = transaction.amount,
            category = transaction.category,
            date = transaction.date,
            description = transaction.description,
            userId = transaction.userId,
            isSynced = isSynced
        )
    }
}
