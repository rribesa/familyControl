package br.com.rribesa.familycontrol.feature.finance.impl.presentation.registertransaction

sealed interface RegisterTransactionEffect {
    data object NavigateBack : RegisterTransactionEffect
    data object ShowSuccessMessage : RegisterTransactionEffect
}
