package br.com.rribesa.familycontrol.feature.finance.impl.presentation.history

sealed interface HistoryEffect {
    data object NavigateBack : HistoryEffect
}
