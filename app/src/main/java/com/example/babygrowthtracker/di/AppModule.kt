package com.example.babygrowthtracker.di

import com.example.babygrowthtracker.data.repository.AuthRepositoryImpl
import com.example.babygrowthtracker.data.repository.LogRepositoryImpl
import com.example.babygrowthtracker.data.repository.PdfRepositoryImpl
import com.example.babygrowthtracker.data.repository.SubscriptionRepositoryImpl
import com.example.babygrowthtracker.domain.repository.AuthRepository
import com.example.babygrowthtracker.domain.repository.LogRepository
import com.example.babygrowthtracker.domain.repository.PdfRepository
import com.example.babygrowthtracker.domain.repository.SubscriptionRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import com.google.firebase.firestore.FirebaseFirestore
import com.example.babygrowthtracker.data.repository.BabyRepositoryImpl
import com.example.babygrowthtracker.domain.repository.BabyRepository

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideLogRepository(impl: LogRepositoryImpl): LogRepository {
        return impl
    }
    
    @Provides
    @Singleton
    fun providePdfRepository(impl: PdfRepositoryImpl): PdfRepository {
        return impl
    }

    @Provides
    @Singleton
    fun provideSubscriptionRepository(impl: SubscriptionRepositoryImpl): SubscriptionRepository {
        return impl
    }
    
    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideBabyRepository(impl: BabyRepositoryImpl): BabyRepository {
        return impl
    }
}