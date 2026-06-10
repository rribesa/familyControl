package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Category
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.TransactionRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.ManageCategoriesUseCase
import javax.inject.Inject

class ManageCategories @Inject constructor(
    private val repository: TransactionRepository
) : ManageCategoriesUseCase {
    override suspend fun invoke(category: Category) {
        repository.addCategory(category)
    }
}
