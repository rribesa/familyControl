package br.com.rribesa.familycontrol.feature.auth.impl.presentation.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.rribesa.familycontrol.core.ui.R
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import coil.compose.AsyncImage

private const val SUBTITLE_MAX_WIDTH_FRACTION = 0.85f

@Composable
fun LoginScreen(
    state: LoginState,
    webClientId: String,
    onEvent: (LoginEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LoginBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            LoginHeader()
            Spacer(modifier = Modifier.height(32.dp))
            LoginFormCard(
                state = state,
                webClientId = webClientId,
                onEvent = onEvent,
                onForgotPasswordClicked = { onEvent(LoginEvent.OnForgotPasswordClicked) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            LoginFooter(onRegisterClicked = { onEvent(LoginEvent.OnRegisterClicked) })
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
private fun BoxScope.LoginBackground() {
    Box(
        modifier = Modifier
            .align(Alignment.TopEnd)
            .size(400.dp)
            .blur(80.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
    )
    Box(
        modifier = Modifier
            .align(Alignment.BottomStart)
            .size(350.dp)
            .blur(60.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f))
    )
}

@Composable
private fun LoginHeader() {
    AsyncImage(
        model = stringResource(id = R.string.brand_logo_url),
        contentDescription = stringResource(id = R.string.brand_logo_content_description),
        modifier = Modifier
            .size(80.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(24.dp))
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = stringResource(id = R.string.login_title),
        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = stringResource(id = R.string.login_subtitle),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(SUBTITLE_MAX_WIDTH_FRACTION)
    )
}

@Composable
private fun LoginFormCard(
    state: LoginState,
    webClientId: String,
    onEvent: (LoginEvent) -> Unit,
    onForgotPasswordClicked: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            EmailField(state = state, onEvent = onEvent)
            PasswordField(
                state = state,
                onEvent = onEvent,
                onForgotPasswordClicked = onForgotPasswordClicked
            )
            ErrorBanner(state = state)
            SubmitButton(state = state, onEvent = onEvent)
            OrDivider()
            GoogleButton(state = state, webClientId = webClientId, onEvent = onEvent)
        }
    }
}


@Composable
private fun LoginFooter(onRegisterClicked: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.login_no_account),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = stringResource(id = R.string.login_create_account),
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onRegisterClicked() }
        )
    }
}

@Preview(name = "Normal Device (Phone)", showBackground = true, device = Devices.PHONE)
@Composable
internal fun LoginPreviewNormal() {
    FamilyControlTheme {
        LoginScreen(
            state = LoginState(),
            webClientId = "",
            onEvent = {}
        )
    }
}

@Preview(name = "Large Device (Tablet)", showBackground = true, device = Devices.TABLET)
@Composable
internal fun LoginPreviewLarge() {
    FamilyControlTheme {
        LoginScreen(
            state = LoginState(),
            webClientId = "",
            onEvent = {}
        )
    }
}

@Preview(name = "Expanded Device (Landscape)", showBackground = true, widthDp = 1024, heightDp = 600)
@Composable
internal fun LoginPreviewExpanded() {
    FamilyControlTheme {
        LoginScreen(
            state = LoginState(),
            webClientId = "",
            onEvent = {}
        )
    }
}
