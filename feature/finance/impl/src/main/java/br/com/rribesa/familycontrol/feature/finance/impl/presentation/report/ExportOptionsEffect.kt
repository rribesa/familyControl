package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

sealed interface ExportOptionsEffect {
    data object NavigateBack : ExportOptionsEffect
}
