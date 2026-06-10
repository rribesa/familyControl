package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.GetDashboardStatsUseCase
import br.com.rribesa.familycontrol.feature.finance.api.domain.usecase.SyncTransactionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@Suppress("TooGenericExceptionCaught", "InstanceOfCheckForException", "SwallowedException")
class DashboardViewModel @Inject constructor(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val syncTransactionsUseCase: SyncTransactionsUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DashboardState())
    val state: StateFlow<DashboardState> = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DashboardEffect>()
    val effect: SharedFlow<DashboardEffect> = _effect.asSharedFlow()

    private var collectJob: Job? = null
    private var firestoreListener: com.google.firebase.firestore.ListenerRegistration? = null

    init {
        loadDashboardStats()
        setupFirestoreListener()
    }

    private fun setupFirestoreListener() {
        try {
            firestoreListener = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection(br.com.rribesa.familycontrol.core.data.FirestorePaths.TRANSACTIONS)
                .addSnapshotListener { _, error ->
                    if (error != null) return@addSnapshotListener
                    viewModelScope.launch {
                        try {
                            val user = authRepository.getCurrentUser().firstOrNull()
                            user?.let { u ->
                                syncTransactionsUseCase(u.id)
                            }
                        } catch (e: Exception) {
                            // ignore
                        }
                    }
                }
        } catch (e: Exception) {
            // ignore
        }
    }

    fun onEvent(event: DashboardEvent) {
        when (event) {
            DashboardEvent.OnRefresh -> {
                loadDashboardStats()
            }
            DashboardEvent.OnReportClicked -> {
                viewModelScope.launch {
                    _effect.emit(DashboardEffect.NavigateToReport)
                }
            }
            DashboardEvent.OnAddTransactionClicked -> {
                viewModelScope.launch {
                    val user = authRepository.getCurrentUser().firstOrNull()
                    if (user?.role == "Editor") {
                        _effect.emit(DashboardEffect.NavigateToRegisterTransaction)
                    } else {
                        _state.update {
                            it.copy(
                                errorMessageResId = R.string.error_permission_denied
                            )
                        }
                    }
                }
            }
            DashboardEvent.OnViewHistoryClicked -> {
                viewModelScope.launch {
                    _effect.emit(DashboardEffect.NavigateToHistory)
                }
            }
        }
    }

    private fun loadDashboardStats() {
        collectJob?.cancel()
        _state.update { it.copy(isLoading = true, errorMessageResId = null) }
        collectJob = viewModelScope.launch {
            try {
                getDashboardStatsUseCase().collect { stats ->
                    _state.update {
                        it.copy(
                            budgetStats = stats.budgetStats,
                            categorySummaries = stats.categorySummaries,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                Log.e("DashboardViewModel", "Error loading stats", e)
                _state.update {
                    it.copy(
                        isLoading = false,
                        errorMessageResId = R.string.error_generic
                    )
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        firestoreListener?.remove()
    }
}
