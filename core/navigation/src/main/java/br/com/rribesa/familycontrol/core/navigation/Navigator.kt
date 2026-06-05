package br.com.rribesa.familycontrol.core.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Navigator(startDestination: Destination) {
    val backStack: SnapshotStateList<Destination> = mutableStateListOf(startDestination)

    fun navigateTo(destination: Destination) {
        backStack.add(destination)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }
}
