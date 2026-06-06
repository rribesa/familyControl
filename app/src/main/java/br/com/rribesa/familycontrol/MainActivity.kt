package br.com.rribesa.familycontrol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import br.com.rribesa.familycontrol.core.navigation.Destination
import br.com.rribesa.familycontrol.core.navigation.Navigator
import br.com.rribesa.familycontrol.core.ui.screen.SplashScreen
import br.com.rribesa.familycontrol.core.ui.theme.FamilyControlTheme
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.login.LoginEffect
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.login.LoginScreen
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.login.LoginViewModel
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.register.RegisterEffect
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.register.RegisterScreen
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.register.RegisterViewModel
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery.PasswordRecoveryEffect
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery.PasswordRecoveryScreen
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.passwordrecovery.PasswordRecoveryViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FamilyControlTheme {
                AppNavDisplay(navigator)
            }
        }
    }
}

@Composable
private fun AppNavDisplay(navigator: Navigator) {
    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        entryProvider = { destination ->
            when (destination) {
                Destination.Splash -> NavEntry(destination) {
                    SplashScreen(
                        modifier = Modifier.fillMaxSize(),
                        onSplashFinished = {
                            navigator.replace(Destination.Login)
                        }
                    )
                }
                Destination.Login -> NavEntry(destination) {
                    LoginRouteContent(navigator)
                }
                Destination.Register -> NavEntry(destination) {
                    RegisterRouteContent(navigator)
                }
                Destination.PasswordRecovery -> NavEntry(destination) {
                    PasswordRecoveryRouteContent(navigator)
                }
                Destination.Dashboard -> NavEntry(destination) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Dashboard Screen (Placeholder)")
                    }
                }
            }
        }
    )
}

@Composable
private fun LoginRouteContent(navigator: Navigator) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToDashboard -> {
                    navigator.clearAndNavigateTo(Destination.Dashboard)
                }
                LoginEffect.NavigateToRegister -> {
                    navigator.replace(Destination.Register)
                }
                LoginEffect.NavigateToForgotPassword -> {
                    navigator.replace(Destination.PasswordRecovery)
                }
                is LoginEffect.ShowError -> {
                }
            }
        }
    }

    val webClientId = stringResource(id = R.string.default_web_client_id)
    LoginScreen(
        state = state,
        webClientId = webClientId,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun RegisterRouteContent(navigator: Navigator) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToDashboard -> {
                    navigator.clearAndNavigateTo(Destination.Dashboard)
                }
                RegisterEffect.NavigateToLogin -> {
                    navigator.replace(Destination.Login)
                }
                is RegisterEffect.ShowError -> {
                }
            }
        }
    }

    val webClientId = stringResource(id = R.string.default_web_client_id)
    RegisterScreen(
        state = state,
        webClientId = webClientId,
        onEvent = viewModel::onEvent,
        onLoginClicked = {
            navigator.replace(Destination.Login)
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun PasswordRecoveryRouteContent(navigator: Navigator) {
    val viewModel: PasswordRecoveryViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                PasswordRecoveryEffect.NavigateToLogin -> {
                    navigator.replace(Destination.Login)
                }
                is PasswordRecoveryEffect.ShowError -> {
                }
            }
        }
    }

    PasswordRecoveryScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = Modifier.fillMaxSize()
    )
}

