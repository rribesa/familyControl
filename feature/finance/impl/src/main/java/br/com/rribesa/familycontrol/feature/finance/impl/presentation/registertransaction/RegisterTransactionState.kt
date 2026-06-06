package br.com.rribesa.familycontrol.feature.finance.impl.presentation.registertransaction

data class RegisterTransactionState(
    val amount: String = "",
    val category: String = "",
    val description: String = "",
    val date: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val errorMessageResId: Int? = null,
    val success: Boolean = false
)
