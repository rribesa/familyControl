package br.com.rribesa.familycontrol.feature.finance.impl.presentation.registertransaction

sealed interface RegisterTransactionEvent {
    data class OnAmountChanged(val amount: String) : RegisterTransactionEvent
    data class OnCategoryChanged(val category: String) : RegisterTransactionEvent
    data class OnDescriptionChanged(val description: String) : RegisterTransactionEvent
    data class OnDateChanged(val date: Long) : RegisterTransactionEvent
    data object OnSaveClicked : RegisterTransactionEvent
    data object OnBackClicked : RegisterTransactionEvent
}
