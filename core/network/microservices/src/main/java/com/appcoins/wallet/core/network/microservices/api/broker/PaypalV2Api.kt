package com.appcoins.wallet.core.network.microservices.api.broker

import com.appcoins.wallet.core.network.microservices.model.*
import io.reactivex.Single
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface PaypalV2Api {

  @POST("8.20230522/gateways/paypal/transactions")
  fun createTransaction(
    @Header("PayPal-Mock-Response") mockHeader: String, // uncomment for testing errors in dev
    @Query("wallet.address") walletAddress: String,
    @Query("wallet.signature") walletSignature: String,
    @Body paypalPayment: PaypalPayment
  ): Single<PaypalV2StartResponse>

  @POST("8.20230522/gateways/paypal/billing-agreement/token/create")
  fun createToken(
    @Query("wallet.address") walletAddress: String,
    @Query("wallet.signature") walletSignature: String,
    @Body createTokenRequest: CreateTokenRequest
  ): Single<PaypalV2CreateTokenResponse>

  @POST("8.20230522/gateways/paypal/billing-agreement/create")
  fun createBillingAgreement(
    @Query("wallet.address") walletAddress: String,
    @Query("wallet.signature") walletSignature: String,
    @Body token: String
  ): Single<PaypalV2CreateAgreementResponse>

  @POST("8.20230522/gateways/paypal/billing-agreement/token/cancel")
  fun cancelToken(
    @Query("wallet.address") walletAddress: String,
    @Query("wallet.signature") walletSignature: String,
    @Body token: String
  ): Single<String?>

  @GET("8.20230522/gateways/paypal/billing-agreement")
  fun getCurrentBillingAgreement(
    @Query("wallet.address") walletAddress: String,
    @Query("wallet.signature") walletSignature: String,
  ): Single<PaypalV2GetAgreementResponse>

  @POST("8.20230522/gateways/paypal/billing-agreement/cancel")
  fun removeBillingAgreement(
    @Query("wallet.address") walletAddress: String,
    @Query("wallet.signature") walletSignature: String,
  ): Single<Result<String?>>

}