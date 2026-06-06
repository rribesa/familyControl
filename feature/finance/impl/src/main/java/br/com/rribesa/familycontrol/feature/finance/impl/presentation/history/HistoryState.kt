package br.com.rribesa.familycontrol.feature.finance.impl.presentation.history

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction

data class HistoryState(
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val filter: HistoryFilter = HistoryFilter.ALL,
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null
)
