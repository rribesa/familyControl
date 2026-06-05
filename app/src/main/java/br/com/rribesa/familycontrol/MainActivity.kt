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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import br.com.rribesa.familycontrol.core.navigation.Destination
import br.com.rribesa.familycontrol.core.navigation.Navigator
import br.com.rribesa.familycontrol.core.ui.screen.splashScreen
import br.com.rribesa.familycontrol.core.ui.theme.familyControlTheme
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.login.LoginEffect
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.login.loginScreen
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.login.LoginViewModel
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.register.RegisterEffect
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.register.registerScreen
import br.com.rribesa.familycontrol.feature.auth.impl.presentation.register.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            familyControlTheme {
                appNavDisplay(navigator)
            }
        }
    }
}

@Composable
private fun appNavDisplay(navigator: Navigator) {
    NavDisplay(
        backStack = navigator.backStack,
        onBack = { navigator.goBack() },
        entryProvider = { destination ->
            when (destination) {
                Destination.Splash -> NavEntry(destination) {
                    splashScreen(
                        modifier = Modifier.fillMaxSize(),
                        onSplashFinished = {
                            navigator.navigateTo(Destination.Login)
                        }
                    )
                }
                Destination.Login -> NavEntry(destination) {
                    loginRouteContent(navigator)
                }
                Destination.Register -> NavEntry(destination) {
                    registerRouteContent(navigator)
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
private fun loginRouteContent(navigator: Navigator) {
    val viewModel: LoginViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LoginEffect.NavigateToDashboard -> {
                    navigator.navigateTo(Destination.Dashboard)
                }
                LoginEffect.NavigateToRegister -> {
                    navigator.navigateTo(Destination.Register)
                }
                LoginEffect.NavigateToForgotPassword -> {
                    // Navigate to forgot password screen stub
                }
                is LoginEffect.ShowError -> {
                    // Display error toast/alert
                }
            }
        }
    }

    loginScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onForgotPasswordClicked = {
            // Trigger forgot password flow stub
        },
        onRegisterClicked = {
            navigator.navigateTo(Destination.Register)
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun registerRouteContent(navigator: Navigator) {
    val viewModel: RegisterViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                RegisterEffect.NavigateToDashboard -> {
                    navigator.navigateTo(Destination.Dashboard)
                }
                RegisterEffect.NavigateToLogin -> {
                    navigator.navigateTo(Destination.Login)
                }
                is RegisterEffect.ShowError -> {
                    // Handle registration error feedback
                }
            }
        }
    }

    registerScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onLoginClicked = {
            navigator.navigateTo(Destination.Login)
        },
        modifier = Modifier.fillMaxSize()
    )
}
