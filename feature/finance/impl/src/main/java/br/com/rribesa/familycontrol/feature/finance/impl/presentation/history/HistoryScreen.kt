@file:Suppress("TooManyFunctions")
package br.com.rribesa.familycontrol.feature.finance.impl.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.Transaction
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
@Suppress("MagicNumber")
fun HistoryScreen(
    state: HistoryState,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HistoryBackgroundBlur(modifier = Modifier.align(Alignment.BottomStart))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            HistoryHeader(
                isLoading = state.isLoading,
                onBack = { onEvent(HistoryEvent.OnBackClicked) },
                onRefresh = { onEvent(HistoryEvent.OnRefresh) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            ErrorMessage(errorMessageResId = state.errorMessageResId)

            HistoryFilterRow(
                selectedFilter = state.filter,
                onFilterChanged = { onEvent(HistoryEvent.OnFilterChanged(it)) }
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading && state.transactions.isEmpty()) {
                HistoryLoadingIndicator(modifier = Modifier.weight(1f))
            } else if (state.filteredTransactions.isEmpty()) {
                EmptyHistory(modifier = Modifier.weight(1f))
            } else {
                TransactionsList(
                    transactions = state.filteredTransactions,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun HistoryBackgroundBlur(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(350.dp)
            .blur(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f))
    )
}

@Composable
private fun HistoryHeader(
    isLoading: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.history_title),
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        IconButton(onClick = onRefresh, enabled = !isLoading) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun ErrorMessage(errorMessageResId: Int?) {
    AnimatedVisibility(visible = errorMessageResId != null) {
        Text(
            text = errorMessageResId?.let { stringResource(id = it) }.orEmpty(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }
}

@Composable
private fun HistoryFilterRow(
    selectedFilter: HistoryFilter,
    onFilterChanged: (HistoryFilter) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChipItem(
            text = stringResource(id = R.string.history_filter_all),
            isSelected = selectedFilter == HistoryFilter.ALL,
            onClick = { onFilterChanged(HistoryFilter.ALL) },
            modifier = Modifier.weight(1f)
        )
        FilterChipItem(
            text = stringResource(id = R.string.history_filter_income),
            isSelected = selectedFilter == HistoryFilter.INCOME,
            onClick = { onFilterChanged(HistoryFilter.INCOME) },
            modifier = Modifier.weight(1f)
        )
        FilterChipItem(
            text = stringResource(id = R.string.history_filter_expense),
            isSelected = selectedFilter == HistoryFilter.EXPENSE,
            onClick = { onFilterChanged(HistoryFilter.EXPENSE) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
@Suppress("MagicNumber")
private fun FilterChipItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val containerColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = contentColor
        )
    }
}

@Composable
private fun HistoryLoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.history_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TransactionsList(
    transactions: List<Transaction>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(transactions, key = { it.id.toString() }) { transaction ->
            TransactionItem(transaction = transaction)
        }
        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun TransactionItem(transaction: Transaction) {
    val isIncome = transaction.category == "Salário"
    val icon = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val iconColor = if (isIncome) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error
    val amountPrefix = if (isIncome) "+" else "-"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = transaction.description.ifBlank { transaction.category },
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = transaction.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(transaction.date))
                    Text(
                        text = dateStr,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            val amountStr = String.format(Locale.getDefault(), "%.2f", transaction.amount)
            Text(
                text = "$amountPrefix R$ $amountStr",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = iconColor
            )
        }
    }
}

@Preview(name = "Normal Device (Phone)", showBackground = true, device = Devices.PHONE)
@Composable
@Suppress("MagicNumber")
internal fun HistoryPreviewNormal() {
    val sampleTransactions = listOf(
        Transaction(UUID.randomUUID(), 1500.0, "Salário", System.currentTimeMillis(), "Salário Mensal", "1"),
        Transaction(UUID.randomUUID(), 85.50, "Alimentação", System.currentTimeMillis(), "Almoço", "1")
    )
    FamilyControlTheme {
        HistoryScreen(
            state = HistoryState(transactions = sampleTransactions, filteredTransactions = sampleTransactions),
            onEvent = {}
        )
    }
}

@Preview(name = "Large Device (Tablet)", showBackground = true, device = Devices.TABLET)
@Composable
@Suppress("MagicNumber")
internal fun HistoryPreviewLarge() {
    val sampleTransactions = listOf(
        Transaction(UUID.randomUUID(), 1500.0, "Salário", System.currentTimeMillis(), "Salário Mensal", "1"),
        Transaction(UUID.randomUUID(), 85.50, "Alimentação", System.currentTimeMillis(), "Almoço", "1")
    )
    FamilyControlTheme {
        HistoryScreen(
            state = HistoryState(transactions = sampleTransactions, filteredTransactions = sampleTransactions),
            onEvent = {}
        )
    }
}

@Preview(name = "Expanded Device (Landscape)", showBackground = true, widthDp = 1024, heightDp = 600)
@Composable
@Suppress("MagicNumber")
internal fun HistoryPreviewExpanded() {
    val sampleTransactions = listOf(
        Transaction(UUID.randomUUID(), 1500.0, "Salário", System.currentTimeMillis(), "Salário Mensal", "1"),
        Transaction(UUID.randomUUID(), 85.50, "Alimentação", System.currentTimeMillis(), "Almoço", "1")
    )
    FamilyControlTheme {
        HistoryScreen(
            state = HistoryState(transactions = sampleTransactions, filteredTransactions = sampleTransactions),
            onEvent = {}
        )
    }
}
