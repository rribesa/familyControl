@file:Suppress("TooManyFunctions")
package br.com.rribesa.familycontrol.feature.finance.impl.presentation.dashboard

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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
            } else if (state.budgetStats.totalIncome == 0.0 &&
                state.budgetStats.totalExpenses == 0.0 &&
                state.categorySummaries.isEmpty()
            ) {
                DashboardWelcomeEmptyState(
                    onConfigureClick = { onEvent(DashboardEvent.OnAddTransactionClicked) }
                )
            } else {
                MetricsGrid(stats = state.budgetStats)
                Spacer(modifier = Modifier.height(16.dp))
                BudgetLimitProgressCard(stats = state.budgetStats)
                Spacer(modifier = Modifier.height(16.dp))
                QuickCategoriesList(
                    categories = state.categorySummaries,
                    onViewHistoryClicked = { onEvent(DashboardEvent.OnViewHistoryClicked) }
                )
                Spacer(modifier = Modifier.height(24.dp))
                ReportNavigationButton(onClick = { onEvent(DashboardEvent.OnReportClicked) })
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
        FloatingActionButton(
            onClick = { onEvent(DashboardEvent.OnAddTransactionClicked) },
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            content = { Icon(imageVector = Icons.Default.Add, contentDescription = null) }
        )
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
        val expenseVal = String.format(Locale.getDefault(), "%.2f", stats.totalExpenses)
        MetricCard(
            label = stringResource(id = R.string.dashboard_total_expenses),
            value = "${stats.currency} $expenseVal",
            valueColor = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f)
        )

        val limitVal = String.format(Locale.getDefault(), "%.2f", stats.budgetLimit)
        MetricCard(
            label = stringResource(id = R.string.dashboard_limit),
            value = "${stats.currency} $limitVal",
            valueColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
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
private fun QuickCategoriesList(
    categories: List<CategorySummary>,
    onViewHistoryClicked: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Gastos por Categoria",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Ver Histórico",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable(onClick = onViewHistoryClicked)
        )
    }

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

@Composable
private fun DashboardWelcomeIllustration(modifier: Modifier = Modifier) {
    val illustrationUrl = "https://lh3.googleusercontent.com/aida/" +
        "AP1WRLsXbN1iLsOena7GlHN6frOZz8bD5SSycCC0AAK5AlT9L7zJk57_" +
        "TSD5GQxHRF4eXN4i3IJHGYersKK5Q-1z8yx2930_3mNTRxVJESkUoqDC" +
        "jH-n4ymU0pmABX4zGEDfB8W_G832G4IiqUWzpuVLRb1Zr8rdp0H6OWJ5" +
        "7I22yf6uvL8zHtTpgwD8Q_9VPbZrm_Ulbuax-samCzS36GRfsAAlsW3e" +
        "IF8otlS2NQp4s-u2vJwYev7wLX5vu4jd"
    Box(
        modifier = modifier
            .size(240.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = illustrationUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        )
    }
}

@Composable
private fun DashboardWelcomeCta(
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onConfigureClick,
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.empty_state_welcome_btn_configure),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }

        Button(
            onClick = {},
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            ),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(id = R.string.empty_state_welcome_btn_how_it_works),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Composable
private fun DashboardWelcomeTip(modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = stringResource(id = R.string.empty_state_welcome_tip_title),
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(id = R.string.empty_state_welcome_tip_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun DashboardWelcomeEmptyState(
    onConfigureClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DashboardWelcomeIllustration()
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(id = R.string.empty_state_welcome_title),
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = stringResource(id = R.string.empty_state_welcome_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))
        DashboardWelcomeCta(onConfigureClick = onConfigureClick)
        Spacer(modifier = Modifier.height(24.dp))
        DashboardWelcomeTip()
    }
}
