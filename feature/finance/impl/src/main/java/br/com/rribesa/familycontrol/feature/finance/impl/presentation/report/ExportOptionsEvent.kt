package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

sealed interface ExportOptionsEvent {
    data object OnBackClicked : ExportOptionsEvent
    data class OnPeriodSelected(val period: String) : ExportOptionsEvent
    data class OnFormatSelected(val format: String) : ExportOptionsEvent
    data object OnGenerateClicked : ExportOptionsEvent
}
