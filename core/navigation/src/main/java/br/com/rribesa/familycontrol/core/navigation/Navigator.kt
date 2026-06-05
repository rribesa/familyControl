package br.com.rribesa.familycontrol.core.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

class Navigator(startDestination: Destination) {
    val backStack: SnapshotStateList<Destination> = mutableStateListOf(startDestination)

    fun navigateTo(destination: Destination) {
        backStack.add(destination)
    }

    fun replace(destination: Destination) {
        if (backStack.isNotEmpty()) {
            backStack[backStack.lastIndex] = destination
        } else {
            backStack.add(destination)
        }
    }

    fun clearAndNavigateTo(destination: Destination) {
        backStack.clear()
        backStack.add(destination)
    }

    fun goBack() {
        if (backStack.size > 1) {
            backStack.removeLast()
        }
    }
}
