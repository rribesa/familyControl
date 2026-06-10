package br.com.rribesa.familycontrol.feature.finance.impl.domain.usecase

import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Category
import br.com.rribesa.familycontrol.feature.finance.api.domain.repository.TransactionRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetCategoriesUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategories @Inject constructor(
    private val repository: TransactionRepository
) : GetCategoriesUseCase {
    override fun invoke(userId: String): Flow<List<Category>> {
        return repository.getCategories(userId)
    }
}
