package br.com.rribesa.familycontrol.feature.auth.impl.di

import br.com.rribesa.familycontrol.feature.auth.api.domain.repository.AuthRepository
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.ForgotPasswordUseCase
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithEmailUseCase
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.LoginWithGoogleUseCase
import br.com.rribesa.familycontrol.feature.auth.api.domain.usecase.RegisterUserUseCase
import br.com.rribesa.familycontrol.feature.auth.impl.data.repository.AuthRepositoryImpl
import br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase.ForgotPassword
import br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase.LoginWithEmail
import br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase.LoginWithGoogle
import br.com.rribesa.familycontrol.feature.auth.impl.domain.usecase.RegisterUser
import com.google.firebase.auth.FirebaseAuth
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindLoginWithEmailUseCase(
        impl: LoginWithEmail
    ): LoginWithEmailUseCase

    @Binds
    @Singleton
    abstract fun bindLoginWithGoogleUseCase(
        impl: LoginWithGoogle
    ): LoginWithGoogleUseCase

    @Binds
    @Singleton
    abstract fun bindRegisterUserUseCase(
        impl: RegisterUser
    ): RegisterUserUseCase

    @Binds
    @Singleton
    abstract fun bindForgotPasswordUseCase(
        impl: ForgotPassword
    ): ForgotPasswordUseCase

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }
    }
}
