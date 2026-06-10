package br.com.rribesa.familycontrol.feature.auth.impl.data.repository

import br.com.rribesa.familycontrol.feature.auth.api.domain.entity.User
import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import br.com.rribesa.familycontrol.core.data.FirestorePaths
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                firestore.collection(FirestorePaths.USERS)
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role") ?: "Editor"
                        trySend(
                            User(
                                id = firebaseUser.uid,
                                email = firebaseUser.email.orEmpty(),
                                name = firebaseUser.displayName.orEmpty(),
                                role = role
                            )
                        )
                    }
                    .addOnFailureListener {
                        trySend(
                            User(
                                id = firebaseUser.uid,
                                email = firebaseUser.email.orEmpty(),
                                name = firebaseUser.displayName.orEmpty(),
                                role = "Editor"
                            )
                        )
                    }
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): User = suspendCancellableCoroutine { continuation ->
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    continuation.resume(firebaseUser.toDomainUser())
                } else {
                    continuation.resumeWithException(Exception("Firebase User is null"))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override suspend fun loginWithGoogle(
        idToken: String
    ): User = suspendCancellableCoroutine { continuation ->
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    continuation.resume(firebaseUser.toDomainUser())
                } else {
                    continuation.resumeWithException(Exception("Firebase User is null"))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override suspend fun register(
        email: String,
        name: String,
        password: String
    ): User = suspendCancellableCoroutine { continuation ->
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { authResult ->
                val firebaseUser = authResult.user
                if (firebaseUser != null) {
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    firebaseUser.updateProfile(profileUpdates)
                        .addOnCompleteListener { task ->
                            val userData = mapOf(
                                "id" to firebaseUser.uid,
                                "email" to email,
                                "name" to name,
                                "role" to "Editor"
                            )
                            firestore.collection(FirestorePaths.USERS)
                                .document(firebaseUser.uid)
                                .set(userData)
                                .addOnCompleteListener {
                                    if (task.isSuccessful) {
                                        continuation.resume(
                                            firebaseUser.toDomainUser().copy(name = name, role = "Editor")
                                        )
                                    } else {
                                        continuation.resume(
                                            firebaseUser.toDomainUser().copy(role = "Editor")
                                        )
                                    }
                                }
                        }
                } else {
                    continuation.resumeWithException(Exception("Firebase User is null"))
                }
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun sendPasswordResetEmail(
        email: String
    ) = suspendCancellableCoroutine { continuation ->
        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                continuation.resume(Unit)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    private fun FirebaseUser.toDomainUser(): User {
        return User(
            id = uid,
            email = email.orEmpty(),
            name = displayName.orEmpty(),
            role = "Editor"
        )
    }
}
