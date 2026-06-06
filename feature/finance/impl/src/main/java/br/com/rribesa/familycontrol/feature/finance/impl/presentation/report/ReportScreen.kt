package br.com.rribesa.familycontrol.feature.finance.impl.presentation.report

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import br.com.rribesa.familycontrol.feature.finance.api.domain.model.CategorySummary
import java.util.Locale

@Composable
@Suppress("MagicNumber")
fun ReportScreen(
    state: ReportState,
    onEvent: (ReportEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ReportBackgroundBlur(modifier = Modifier.align(Alignment.BottomStart))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            ReportHeader(onBack = { onEvent(ReportEvent.OnBackClicked) })
            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(visible = state.errorMessageResId != null) {
                Text(
                    text = state.errorMessageResId?.let { stringResource(id = it) }.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            if (state.isLoading && state.totalExpenses == 0.0) {
                ReportLoadingContainer()
            } else {
                ReportOverviewCard(totalExpenses = state.totalExpenses)
                Spacer(modifier = Modifier.height(24.dp))
                CategoriesDetailedList(categories = state.categorySummaries)
                Spacer(modifier = Modifier.height(24.dp))
                ReportBackButton(onClick = { onEvent(ReportEvent.OnBackClicked) })
            }
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun ReportBackgroundBlur(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(350.dp)
            .blur(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f))
    )
}

@Composable
private fun ReportLoadingContainer() {
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
private fun ReportHeader(onBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(id = R.string.report_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ReportOverviewCard(totalExpenses: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = stringResource(id = R.string.report_total_expenses),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            val expensesVal = String.format(Locale.getDefault(), "%.2f", totalExpenses)
            Text(
                text = "R$ $expensesVal",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun CategoriesDetailedList(categories: List<CategorySummary>) {
    Text(
        text = "Detalhamento de Gastos",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            categories.forEach { category ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = category.category,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            val percentageVal = String.format(Locale.getDefault(), "%.1f", category.percentage)
                            Text(
                                text = "$percentageVal% do total",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        val amountVal = String.format(Locale.getDefault(), "%.2f", category.amount)
                        Text(
                            text = "R$ $amountVal",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { (category.percentage / 100f).toFloat().coerceIn(0f, 1f) },
                        color = MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun ReportBackButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.outlineVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(
            text = stringResource(id = R.string.report_btn_back),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Preview(name = "Normal Device (Phone)", showBackground = true, device = Devices.PHONE)
@Composable
@Suppress("MagicNumber")
internal fun ReportPreviewNormal() {
    FamilyControlTheme {
        ReportScreen(
            state = ReportState(
                totalExpenses = 3500.0,
                categorySummaries = listOf(
                    CategorySummary("Alimentação", 1200.0, 34.3),
                    CategorySummary("Moradia", 1500.0, 42.9),
                    CategorySummary("Transporte", 500.0, 14.3),
                    CategorySummary("Lazer", 300.0, 8.5)
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Large Device (Tablet)", showBackground = true, device = Devices.TABLET)
@Composable
@Suppress("MagicNumber")
internal fun ReportPreviewLarge() {
    FamilyControlTheme {
        ReportScreen(
            state = ReportState(
                totalExpenses = 3500.0,
                categorySummaries = listOf(
                    CategorySummary("Alimentação", 1200.0, 34.3),
                    CategorySummary("Moradia", 1500.0, 42.9)
                )
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Expanded Device (Landscape)", showBackground = true, widthDp = 1024, heightDp = 600)
@Composable
@Suppress("MagicNumber")
internal fun ReportPreviewExpanded() {
    FamilyControlTheme {
        ReportScreen(
            state = ReportState(
                totalExpenses = 3500.0,
                categorySummaries = listOf(
                    CategorySummary("Alimentação", 1200.0, 34.3),
                    CategorySummary("Moradia", 1500.0, 42.9)
                )
            ),
            onEvent = {}
        )
    }
}
