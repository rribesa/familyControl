@file:Suppress("TooManyFunctions")
package br.com.rribesa.familycontrol.feature.finance.impl.presentation.registertransaction

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
@Suppress("MagicNumber")
fun RegisterTransactionScreen(
    state: RegisterTransactionState,
    onEvent: (RegisterTransactionEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RegisterBackgroundBlur(modifier = Modifier.align(Alignment.BottomEnd))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            RegisterHeader(onBack = { onEvent(RegisterTransactionEvent.OnBackClicked) })
            Spacer(modifier = Modifier.height(16.dp))

            ErrorMessage(errorMessageResId = state.errorMessageResId)

            TransactionAmountInput(
                amount = state.amount,
                onAmountChanged = { onEvent(RegisterTransactionEvent.OnAmountChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            CategorySelectionGrid(
                selectedCategory = state.category,
                categories = state.categoriesList,
                onCategorySelected = { onEvent(RegisterTransactionEvent.OnCategoryChanged(it)) }
            )

            Spacer(modifier = Modifier.height(12.dp))
            CustomCategoryInputField(
                name = state.newCategoryName,
                onNameChanged = { onEvent(RegisterTransactionEvent.OnNewCategoryNameChanged(it)) },
                onAddClick = { onEvent(RegisterTransactionEvent.OnAddCustomCategoryClicked) }
            )

            Spacer(modifier = Modifier.height(16.dp))
            DescriptionAndDateInputs(
                desc = state.description,
                onDescChanged = { onEvent(RegisterTransactionEvent.OnDescriptionChanged(it)) },
                date = state.date
            )

            Spacer(modifier = Modifier.height(24.dp))
            SaveButton(isLoading = state.isLoading, onSave = { onEvent(RegisterTransactionEvent.OnSaveClicked) })
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun RegisterBackgroundBlur(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(350.dp)
            .blur(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    )
}

@Composable
private fun RegisterHeader(onBack: () -> Unit) {
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
            text = stringResource(id = R.string.transactions_title),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onBackground
        )
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
private fun TransactionAmountInput(
    amount: String,
    onAmountChanged: (String) -> Unit
) {
    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChanged,
        label = { Text(text = stringResource(id = R.string.transactions_amount_label)) },
        placeholder = { Text(text = stringResource(id = R.string.transactions_amount_placeholder)) },
        prefix = { Text(text = "R$ ") },
        leadingIcon = { Icon(imageVector = Icons.Default.AttachMoney, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
@Suppress("MagicNumber")
private fun CategorySelectionGrid(
    selectedCategory: String,
    categories: List<String>,
    onCategorySelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Category,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(id = R.string.transactions_category_label),
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunks = categories.chunked(3)
                chunks.forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        rowCategories.forEach { category ->
                            CategoryChip(
                                category = category,
                                isSelected = category == selectedCategory,
                                onClick = { onCategorySelected(category) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CustomCategoryInputField(
    name: String,
    onNameChanged: (String) -> Unit,
    onAddClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = name,
            onValueChange = onNameChanged,
            placeholder = { Text(text = stringResource(id = R.string.transactions_add_category_placeholder)) },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )
        Button(
            onClick = onAddClick,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(56.dp)
        ) {
            Text(text = stringResource(id = R.string.transactions_add_category_btn))
        }
    }
}

@Composable
@Suppress("MagicNumber")
private fun CategoryChip(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val baseColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .height(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(baseColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = category,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                ),
                color = contentColor
            )
        }
    }
}

@Composable
private fun DescriptionAndDateInputs(
    desc: String,
    onDescChanged: (String) -> Unit,
    date: Long
) {
    OutlinedTextField(
        value = desc,
        onValueChange = onDescChanged,
        label = { Text(text = stringResource(id = R.string.transactions_description_label)) },
        placeholder = { Text(text = stringResource(id = R.string.transactions_description_placeholder)) },
        leadingIcon = { Icon(imageVector = Icons.Default.Description, contentDescription = null) },
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(date))
    OutlinedTextField(
        value = dateStr,
        onValueChange = {},
        enabled = false,
        label = { Text(text = stringResource(id = R.string.transactions_date_label)) },
        colors = OutlinedTextFieldDefaults.colors(
            disabledBorderColor = MaterialTheme.colorScheme.outlineVariant,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTextColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SaveButton(
    isLoading: Boolean,
    onSave: () -> Unit
) {
    Button(
        onClick = onSave,
        enabled = !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(
                text = stringResource(id = R.string.transactions_btn_save),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
            )
        }
    }
}

@Preview(name = "Normal Device (Phone)", showBackground = true, device = Devices.PHONE)
@Composable
internal fun RegisterTransactionPreviewNormal() {
    FamilyControlTheme {
        RegisterTransactionScreen(
            state = RegisterTransactionState(
                amount = "120.50",
                category = "Alimentação",
                description = "Compras no mercado"
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Large Device (Tablet)", showBackground = true, device = Devices.TABLET)
@Composable
internal fun RegisterTransactionPreviewLarge() {
    FamilyControlTheme {
        RegisterTransactionScreen(
            state = RegisterTransactionState(
                amount = "120.50",
                category = "Alimentação",
                description = "Compras no mercado"
            ),
            onEvent = {}
        )
    }
}

@Preview(name = "Expanded Device (Landscape)", showBackground = true, widthDp = 1024, heightDp = 600)
@Composable
internal fun RegisterTransactionPreviewExpanded() {
    FamilyControlTheme {
        RegisterTransactionScreen(
            state = RegisterTransactionState(
                amount = "120.50",
                category = "Alimentação",
                description = "Compras no mercado"
            ),
            onEvent = {}
        )
    }
}
