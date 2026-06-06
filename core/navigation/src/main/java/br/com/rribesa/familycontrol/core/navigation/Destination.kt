package br.com.rribesa.familycontrol.core.navigation

sealed interface Destination {
    data object Splash : Destination
    data object Login : Destination
    data object Register : Destination
    data object Dashboard : Destination
    data object PasswordRecovery : Destination
}
