package com.example.babygrowthtracker.di

import android.content.Context
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RevenueCatModule {

    @Provides
    @Singleton
    fun provideRevenueCat(@ApplicationContext context: Context): Purchases {
        Purchases.logLevel = LogLevel.DEBUG
        Purchases.configure(
            PurchasesConfiguration.Builder(context, "YOUR_REVENUECAT_API_KEY").build()
        )
        return Purchases.sharedInstance
    }
}