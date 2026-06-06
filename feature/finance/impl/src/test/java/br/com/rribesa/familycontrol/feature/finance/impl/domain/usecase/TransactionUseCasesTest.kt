package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.TransactionRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.UUID

class TransactionUseCasesTest {

    private val repository: TransactionRepository = mockk(relaxed = true)

    private val addTransaction = AddTransaction(repository)
    private val getTransactionHistory = GetTransactionHistory(repository)
    private val syncTransactions = SyncTransactions(repository)

    @Test
    fun `add transaction should delegate to repository`() = runTest {
        val transaction = Transaction(UUID.randomUUID(), 100.0, "Lazer", System.currentTimeMillis(), "Cinema", "user1")
        
        addTransaction(transaction)
        
        coVerify(exactly = 1) { repository.addTransaction(transaction) }
    }

    @Test
    fun `get transaction history should return stream from repository`() = runTest {
        val userId = "user1"
        val mockList = listOf(
            Transaction(UUID.randomUUID(), 10.0, "Outros", System.currentTimeMillis(), "Cafe", userId)
        )
        every { repository.getTransactions(userId) } returns flowOf(mockList)

        val result = getTransactionHistory(userId).toList()

        assertEquals(1, result.size)
        assertEquals(mockList, result[0])
    }

    @Test
    fun `sync transactions should delegate to repository`() = runTest {
        val userId = "user1"
        
        syncTransactions(userId)
        
        coVerify(exactly = 1) { repository.syncTransactions(userId) }
    }
}
