package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

sealed interface ReportEvent {
    data object OnBackClicked : ReportEvent
}
