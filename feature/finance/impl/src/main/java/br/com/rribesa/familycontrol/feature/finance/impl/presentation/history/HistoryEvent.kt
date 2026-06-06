package br.com.rribesa.familycontrol.feature.finance.impl.presentation.history

sealed interface HistoryEvent {
    data class OnFilterChanged(val filter: HistoryFilter) : HistoryEvent
    data object OnRefresh : HistoryEvent
    data object OnBackClicked : HistoryEvent
}
