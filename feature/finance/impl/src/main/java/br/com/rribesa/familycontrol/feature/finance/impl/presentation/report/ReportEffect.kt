package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

sealed interface ReportEffect {
    data object NavigateBack : ReportEffect
}
