package br.com.rribesa.familycontrol.feature.finance.api.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Category

interface ManageCategoriesUseCase {
    suspend operator fun invoke(category: Category)
}
