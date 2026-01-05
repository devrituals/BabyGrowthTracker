package com.example.babygrowthtracker.data.repository

import android.app.Activity
import android.util.Log
import com.example.babygrowthtracker.domain.repository.SubscriptionRepository
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.Offerings
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesError
import com.revenuecat.purchases.interfaces.LogInCallback
import com.revenuecat.purchases.interfaces.PurchaseCallback
import com.revenuecat.purchases.interfaces.ReceiveCustomerInfoCallback
import com.revenuecat.purchases.interfaces.ReceiveOfferingsCallback
import com.revenuecat.purchases.interfaces.UpdatedCustomerInfoListener
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.PurchaseParams
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SubscriptionRepositoryImpl @Inject constructor(
    private val purchases: Purchases
) : SubscriptionRepository {

    override fun isUserPremium(): Flow<Boolean> = callbackFlow {
        val listener = UpdatedCustomerInfoListener { customerInfo ->
            trySend(customerInfo.entitlements.all["premium"]?.isActive == true)
        }

        purchases.updatedCustomerInfoListener = listener

        awaitClose {
            purchases.updatedCustomerInfoListener = null
        }
    }

    override suspend fun purchasePremium(activity: Activity) {
        return suspendCancellableCoroutine { continuation ->
            purchases.getOfferings(object : ReceiveOfferingsCallback {
                override fun onReceived(offerings: Offerings) {
                    val packageToPurchase = offerings.current?.monthly

                    if (packageToPurchase != null) {
                        val params = PurchaseParams.Builder(activity, packageToPurchase).build()
                        purchases.purchase(params, object : PurchaseCallback {
                                override fun onCompleted(storeTransaction: StoreTransaction, customerInfo: CustomerInfo) {
                                    if (continuation.isActive) continuation.resume(Unit)
                                }

                                override fun onError(error: PurchasesError, userCancelled: Boolean) {
                                    if (userCancelled) {
                                        if (continuation.isActive) continuation.cancel()
                                    } else {
                                        if (continuation.isActive) continuation.resumeWithException(Exception(error.message))
                                    }
                                }
                            }
                        )
                    } else {
                        if (continuation.isActive) continuation.resumeWithException(Exception("No packages found"))
                    }
                }

                override fun onError(error: PurchasesError) {
                    if (continuation.isActive) continuation.resumeWithException(Exception(error.message))
                }
            })
        }
    }

    override suspend fun restorePurchases() {
        return suspendCancellableCoroutine { continuation ->
            purchases.restorePurchases(object : ReceiveCustomerInfoCallback {
                override fun onReceived(customerInfo: CustomerInfo) {
                    if (continuation.isActive) continuation.resume(Unit)
                }

                override fun onError(error: PurchasesError) {
                    Log.e("RevenueCat", "Restore failed: ${error.message}")
                    // Resume successfully even on error so the app doesn't crash/hang
                    if (continuation.isActive) continuation.resume(Unit)
                }
            })
        }
    }

    suspend fun login(appUserId: String) {
        return suspendCancellableCoroutine { continuation ->
            purchases.logIn(appUserId, object : LogInCallback {
                override fun onReceived(customerInfo: CustomerInfo, created: Boolean) {
                    Log.d("RevenueCat", "Logged in. User created: $created")
                    if (continuation.isActive) continuation.resume(Unit)
                }

                override fun onError(error: PurchasesError) {
                    Log.e("RevenueCat", "Login failed: ${error.message}")
                    if (continuation.isActive) continuation.resumeWithException(Exception(error.message))
                }
            })
        }
    }
}