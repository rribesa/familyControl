package br.com.rribesa.familycontrol.feature.auth.impl.presentation.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme

private const val SUBTITLE_MAX_WIDTH_FRACTION = 0.9f
private const val GOOGLE_BUTTON_WIDTH_FRACTION = 0.95f
private const val DIVIDER_WIDTH_FRACTION = 0.95f
private const val FORM_WIDTH_FRACTION = 0.95f

@Composable
fun RegisterScreen(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    onLoginClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RegisterBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            RegisterHeader()
            Spacer(modifier = Modifier.height(24.dp))
            RegisterGoogleButton(state = state, onEvent = onEvent)
            Spacer(modifier = Modifier.height(16.dp))
            RegisterDivider()
            Spacer(modifier = Modifier.height(16.dp))
            RegisterForm(state = state, onEvent = onEvent, onLoginClicked = onLoginClicked)
            Spacer(modifier = Modifier.height(24.dp))
            RegisterDisclaimer()
        }
    }
}

@Composable
private fun BoxScope.RegisterBackground() {
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .size(350.dp)
            .blur(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    )
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .size(300.dp)
            .blur(70.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f))
    )
}

@Composable
private fun RegisterHeader() {
    Text(
        text = stringResource(id = R.string.register_title),
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(id = R.string.register_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(SUBTITLE_MAX_WIDTH_FRACTION)
    )
}

@Composable
private fun RegisterGoogleButton(state: RegisterState, onEvent: (RegisterEvent) -> Unit) {
    Button(
        onClick = { onEvent(RegisterEvent.OnGoogleRegisterClicked) },
        enabled = !state.isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier
            .fillMaxWidth(GOOGLE_BUTTON_WIDTH_FRACTION)
            .height(48.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text = stringResource(id = R.string.register_btn_google),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
        )
    }
}

@Composable
private fun RegisterDivider() {
    Row(
        modifier = Modifier.fillMaxWidth(DIVIDER_WIDTH_FRACTION),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
        Text(
            text = stringResource(id = R.string.or_divider),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}

@Composable
private fun RegisterForm(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    onLoginClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(FORM_WIDTH_FRACTION),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        NameField(state = state, onEvent = onEvent)
        EmailField(state = state, onEvent = onEvent)
        BirthDateField(state = state, onEvent = onEvent)
        PasswordField(state = state, onEvent = onEvent)
        ConfirmPasswordField(state = state, onEvent = onEvent)
        RegisterErrorBanner(state = state)
        Spacer(modifier = Modifier.height(8.dp))
        RegisterSubmitButton(state = state, onEvent = onEvent)
        AlreadyHaveAccountLink(onLoginClicked = onLoginClicked)
    }
}


@Preview(name = "Normal Device (Phone)", showBackground = true, device = Devices.PHONE)
@Composable
internal fun RegisterPreviewNormal() {
    FamilyControlTheme {
        RegisterScreen(
            state = RegisterState(),
            onEvent = {},
            onLoginClicked = {}
        )
    }
}

@Preview(name = "Large Device (Tablet)", showBackground = true, device = Devices.TABLET)
@Composable
internal fun RegisterPreviewLarge() {
    FamilyControlTheme {
        RegisterScreen(
            state = RegisterState(),
            onEvent = {},
            onLoginClicked = {}
        )
    }
}

@Preview(name = "Expanded Device (Landscape)", showBackground = true, widthDp = 1024, heightDp = 600)
@Composable
internal fun RegisterPreviewExpanded() {
    FamilyControlTheme {
        RegisterScreen(
            state = RegisterState(),
            onEvent = {},
            onLoginClicked = {}
        )
    }
}
