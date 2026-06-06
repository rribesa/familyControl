@file:Suppress("TooManyFunctions")
package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.BudgetStats
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.HealthStatus
import java.util.Locale

@Composable
@Suppress("MagicNumber")
fun DashboardScreen(
    state: DashboardState,
    onEvent: (DashboardEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DashboardBackgroundBlur(modifier = Modifier.align(Alignment.TopEnd))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            DashboardHeader(isLoading = state.isLoading, onRefresh = { onEvent(DashboardEvent.OnRefresh) })
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = state.errorMessageResId != null) {
                Text(
                    text = state.errorMessageResId?.let { stringResource(id = it) }.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (state.isLoading && state.budgetStats.budgetLimit == 0.0) {
                DashboardLoadingContainer()
            } else {
                MetricsGrid(stats = state.budgetStats)
                Spacer(modifier = Modifier.height(16.dp))
                BudgetLimitProgressCard(stats = state.budgetStats)
                Spacer(modifier = Modifier.height(16.dp))
                QuickCategoriesList(categories = state.categorySummaries)
                Spacer(modifier = Modifier.height(24.dp))
                ReportNavigationButton(onClick = { onEvent(DashboardEvent.OnReportClicked) })
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun DashboardBackgroundBlur(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(350.dp)
            .blur(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    )
}

@Composable
private fun DashboardLoadingContainer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun DashboardHeader(isLoading: Boolean, onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.dashboard_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
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
@Suppress("MagicNumber")
private fun MetricsGrid(stats: BudgetStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val incomeVal = String.format(Locale.getDefault(), "%.2f", stats.totalIncome)
        MetricCard(
            label = stringResource(id = R.string.dashboard_total_income),
            value = "${stats.currency} $incomeVal",
            valueColor = Color(0xFF2E7D32),
            modifier = Modifier.weight(1f)
        )

        val expenseVal = String.format(Locale.getDefault(), "%.2f", stats.totalExpenses)
        MetricCard(
            label = stringResource(id = R.string.dashboard_total_expenses),
            value = "${stats.currency} $expenseVal",
            valueColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    BalanceCard(stats = stats)
}

@Composable
private fun MetricCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = valueColor
            )
        }
    }
}

@Composable
private fun BalanceCard(stats: BudgetStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.dashboard_balance),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            val balanceVal = String.format(Locale.getDefault(), "%.2f", stats.balance)
            Text(
                text = "${stats.currency} $balanceVal",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun BudgetLimitProgressCard(stats: BudgetStats) {
    val progress = (stats.expensePercentage / 100.0).toFloat().coerceIn(0f, 1f)
    val healthColor = getBudgetHealthColor(stats.healthStatus)
    val healthText = getBudgetHealthText(stats.healthStatus)

    Card(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.dashboard_limit),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                val limitVal = String.format(Locale.getDefault(), "%.2f", stats.budgetLimit)
                Text(
                    text = "${stats.currency} $limitVal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                color = healthColor,
                trackColor = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = healthText,
                    style = MaterialTheme.typography.labelMedium,
                    color = healthColor,
                    fontWeight = FontWeight.Bold
                )
                val percentageVal = String.format(Locale.getDefault(), "%.1f", stats.expensePercentage)
                Text(
                    text = "$percentageVal%",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun getBudgetHealthColor(status: HealthStatus): Color {
    return when (status) {
        HealthStatus.HEALTHY -> MaterialTheme.colorScheme.primary
        HealthStatus.WARNING -> Color(0xFFFFB300)
        HealthStatus.CRITICAL -> MaterialTheme.colorScheme.error
    }
}

@Composable
private fun getBudgetHealthText(status: HealthStatus): String {
    return when (status) {
        HealthStatus.HEALTHY -> stringResource(id = R.string.dashboard_health_healthy)
        HealthStatus.WARNING -> stringResource(id = R.string.dashboard_health_warning)
        HealthStatus.CRITICAL -> stringResource(id = R.string.dashboard_health_critical)
    }
}

@Composable
@Suppress("MagicNumber")
private fun QuickCategoriesList(categories: List<CategorySummary>) {
    Text(
        text = "Gastos por Categoria",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            categories.forEach { category ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = category.category,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        val amountVal = String.format(Locale.getDefault(), "%.2f", category.amount)
                        Text(
                            text = "R$ $amountVal",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { (category.percentage / 100f).toFloat().coerceIn(0f, 1f) },
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportNavigationButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.dashboard_btn_report),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Preview(name = "Normal Device (Phone)", showBackground = true, device = Devices.PHONE)
@Composable
@Suppress("MagicNumber")
internal fun DashboardPreviewNormal() {
    FamilyControlTheme {
        DashboardScreen(
            state = DashboardState(
                budgetStats = BudgetStats(5000.0, 3500.0, 4000.0),
                categorySummaries = listOf(
                    CategorySummary("Alimentação", 1200.0, 30.0),
                    CategorySummary("Moradia", 1500.0, 37.5)
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Large Device (Tablet)", showBackground = true, device = Devices.TABLET)
@Composable
@Suppress("MagicNumber")
internal fun DashboardPreviewLarge() {
    FamilyControlTheme {
        DashboardScreen(
            state = DashboardState(
                budgetStats = BudgetStats(5000.0, 3500.0, 4000.0),
                categorySummaries = listOf(
                    CategorySummary("Alimentação", 1200.0, 30.0),
                    CategorySummary("Moradia", 1500.0, 37.5)
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Expanded Device (Landscape)", showBackground = true, widthDp = 1024, heightDp = 600)
@Composable
@Suppress("MagicNumber")
internal fun DashboardPreviewExpanded() {
    FamilyControlTheme {
        DashboardScreen(
            state = DashboardState(
                budgetStats = BudgetStats(5000.0, 3500.0, 4000.0),
                categorySummaries = listOf(
                    CategorySummary("Alimentação", 1200.0, 30.0),
                    CategorySummary("Moradia", 1500.0, 37.5)
                )
            ),
            onEvent = {}
        )
    }
}
