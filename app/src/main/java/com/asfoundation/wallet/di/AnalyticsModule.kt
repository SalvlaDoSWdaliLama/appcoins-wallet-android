package com.asfoundation.wallet.di

import cm.aptoide.analytics.AnalyticsManager
import cm.aptoide.skills.SkillsAnalytics
import com.appcoins.wallet.core.network.base.annotations.DefaultHttpClient
import com.appcoins.wallet.core.analytics.analytics.*
import com.appcoins.wallet.core.analytics.analytics.BackendEventLogger
import com.appcoins.wallet.core.network.analytics.api.AnalyticsApi
import com.asf.wallet.BuildConfig
import com.asfoundation.wallet.analytics.*
import com.asfoundation.wallet.app_start.AppStartProbe
import com.asfoundation.wallet.billing.analytics.BillingAnalytics
import com.asfoundation.wallet.billing.analytics.PageViewAnalytics
import com.asfoundation.wallet.billing.analytics.WalletsAnalytics
import com.asfoundation.wallet.feature_flags.topup.TopUpDefaultValueProbe
import com.asfoundation.wallet.home.ui.HomeAnalytics
import com.asfoundation.wallet.main.nav_bar.NavBarAnalytics
import com.asfoundation.wallet.onboarding_new_payment.OnboardingPaymentEvents
import com.asfoundation.wallet.rating.RatingAnalytics
import com.asfoundation.wallet.topup.TopUpAnalytics
import com.asfoundation.wallet.ui.iab.PaymentMethodsAnalytics
import com.asfoundation.wallet.verification.ui.credit_card.VerificationAnalytics
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Named
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AnalyticsModule {

  @Singleton
  @Provides
  @Named("bi_event_list")
  fun provideBiEventList() =
    listOf(
      BillingAnalytics.PURCHASE_DETAILS,
      BillingAnalytics.PAYMENT_METHOD_DETAILS,
      BillingAnalytics.PAYMENT,
    )

  @Singleton
  @Provides
  @Named("indicative_event_list")
  fun provideIndicativeEventList() =
    listOf(
      AppStartProbe.FIRST_LAUNCH, HomeAnalytics.WALLET_HOME_INTERACTION_EVENT,
      BillingAnalytics.WALLET_PRESELECTED_PAYMENT_METHOD, BillingAnalytics.WALLET_PAYMENT_METHOD,
      BillingAnalytics.WALLET_PAYMENT_CONFIRMATION, BillingAnalytics.WALLET_PAYMENT_CONCLUSION,
      BillingAnalytics.WALLET_PAYMENT_START, BillingAnalytics.WALLET_PAYPAL_URL,
      BillingAnalytics.WALLET_PAYMENT_METHOD_DETAILS, BillingAnalytics.WALLET_PAYMENT_BILLING,
      TopUpAnalytics.WALLET_TOP_UP_START, TopUpAnalytics.WALLET_TOP_UP_SELECTION,
      TopUpAnalytics.WALLET_TOP_UP_CONFIRMATION, TopUpAnalytics.WALLET_TOP_UP_CONCLUSION,
      TopUpAnalytics.WALLET_TOP_UP_PAYPAL_URL, TopUpAnalytics.WALLET_TOP_UP_BILLING,
      WalletsAnalytics.WALLET_BACKUP_CREATE, WalletsAnalytics.WALLET_BACKUP_INFO,
      WalletsAnalytics.WALLET_BACKUP_CONFIRMATION, WalletsAnalytics.WALLET_BACKUP_CONCLUSION,
      WalletsAnalytics.WALLET_IMPORT_RESTORE,
      WalletsAnalytics.WALLET_MY_WALLETS_INTERACTION_EVENT,
      WalletsAnalytics.WALLET_PASSWORD_RESTORE, PageViewAnalytics.WALLET_PAGE_VIEW,
      TopUpDefaultValueProbe.TOPUP_DEFAULT_VALUE_PARTICIPATING_EVENT,
      RatingAnalytics.WALLET_RATING_WELCOME_EVENT, RatingAnalytics.WALLET_RATING_POSITIVE_EVENT,
      RatingAnalytics.WALLET_RATING_NEGATIVE_EVENT, RatingAnalytics.WALLET_RATING_FINISH_EVENT,
      VerificationAnalytics.START_EVENT, VerificationAnalytics.INSERT_CARD_EVENT,
      VerificationAnalytics.REQUEST_CONCLUSION_EVENT, VerificationAnalytics.CONFIRM_EVENT,
      VerificationAnalytics.CONCLUSION_EVENT,
      PaymentMethodsAnalytics.WALLET_PAYMENT_LOADING_TOTAL,
      PaymentMethodsAnalytics.WALLET_PAYMENT_LOADING_STEP,
      PaymentMethodsAnalytics.WALLET_PAYMENT_PROCESSING_TOTAL,
      PaymentMethodsAnalytics.WALLET_3DS_START,
      PaymentMethodsAnalytics.WALLET_3DS_CANCEL,
      PaymentMethodsAnalytics.WALLET_3DS_ERROR,
      NavBarAnalytics.WALLET_CALLOUT_PROMOTIONS_CLICK,
      OnboardingPaymentEvents.EVENT_WALLET_PAYMENT_CONCLUSION_NAVIGATION,
      OnboardingPaymentEvents.ONBOARDING_PAYMENT,
      SkillsAnalytics.WALLET_PAGE_VIEW,
      SkillsAnalytics.ESKILLS_PAYMENT_CONCLUSION,
      SkillsAnalytics.ESKILLS_ONBOARDING_CONCLUSION,
      SkillsAnalytics.ESKILLS_PAYMENT_QUEUE_ID_INPUT,
      SkillsAnalytics.ESKILLS_PAYMENT_TOPUP_ERROR,
      SkillsAnalytics.ESKILLS_PAYMENT_NOT_SUPPORTED_ERROR,
      SkillsAnalytics.ESKILLS_PAYMENT_NO_FUNDS_ERROR,
      SkillsAnalytics.ESKILLS_PAYMENT_BUY_CLICK,
      SkillsAnalytics.ESKILLS_PAYMENT_CONCLUSION,
      SkillsAnalytics.ESKILLS_MATCHMAKING_CONCLUSION,
      SkillsAnalytics.ESKILLS_REFERRAL_SHARE_CLICK
    )

  @Singleton
  @Provides
  @Named("sentry_event_list")
  fun provideSentryEventList() =
    listOf(
      AppStartProbe.FIRST_LAUNCH, HomeAnalytics.WALLET_HOME_INTERACTION_EVENT,
      BillingAnalytics.WALLET_PRESELECTED_PAYMENT_METHOD, BillingAnalytics.WALLET_PAYMENT_METHOD,
      BillingAnalytics.WALLET_PAYMENT_CONFIRMATION, BillingAnalytics.WALLET_PAYMENT_CONCLUSION,
      BillingAnalytics.WALLET_PAYMENT_START, BillingAnalytics.WALLET_PAYPAL_URL,
      BillingAnalytics.WALLET_PAYMENT_METHOD_DETAILS, BillingAnalytics.WALLET_PAYMENT_BILLING,
      TopUpAnalytics.WALLET_TOP_UP_START, TopUpAnalytics.WALLET_TOP_UP_SELECTION,
      TopUpAnalytics.WALLET_TOP_UP_CONFIRMATION, TopUpAnalytics.WALLET_TOP_UP_CONCLUSION,
      TopUpAnalytics.WALLET_TOP_UP_PAYPAL_URL, TopUpAnalytics.WALLET_TOP_UP_BILLING,
      WalletsAnalytics.WALLET_BACKUP_CREATE, WalletsAnalytics.WALLET_BACKUP_INFO,
      WalletsAnalytics.WALLET_BACKUP_CONFIRMATION, WalletsAnalytics.WALLET_BACKUP_CONCLUSION,
      WalletsAnalytics.WALLET_IMPORT_RESTORE,
      WalletsAnalytics.WALLET_MY_WALLETS_INTERACTION_EVENT,
      WalletsAnalytics.WALLET_PASSWORD_RESTORE, PageViewAnalytics.WALLET_PAGE_VIEW,
      TopUpDefaultValueProbe.TOPUP_DEFAULT_VALUE_PARTICIPATING_EVENT,
      RatingAnalytics.WALLET_RATING_WELCOME_EVENT, RatingAnalytics.WALLET_RATING_POSITIVE_EVENT,
      RatingAnalytics.WALLET_RATING_NEGATIVE_EVENT, RatingAnalytics.WALLET_RATING_FINISH_EVENT,
      VerificationAnalytics.START_EVENT, VerificationAnalytics.INSERT_CARD_EVENT,
      VerificationAnalytics.REQUEST_CONCLUSION_EVENT, VerificationAnalytics.CONFIRM_EVENT,
      VerificationAnalytics.CONCLUSION_EVENT,
      PaymentMethodsAnalytics.WALLET_PAYMENT_LOADING_TOTAL,
      PaymentMethodsAnalytics.WALLET_PAYMENT_LOADING_STEP,
      PaymentMethodsAnalytics.WALLET_PAYMENT_PROCESSING_TOTAL,
      PaymentMethodsAnalytics.WALLET_3DS_START,
      PaymentMethodsAnalytics.WALLET_3DS_CANCEL,
      PaymentMethodsAnalytics.WALLET_3DS_ERROR,
      NavBarAnalytics.WALLET_CALLOUT_PROMOTIONS_CLICK,
      OnboardingPaymentEvents.EVENT_WALLET_PAYMENT_CONCLUSION_NAVIGATION,
      OnboardingPaymentEvents.ONBOARDING_PAYMENT
    )

  @Singleton
  @Provides
  fun provideAnalyticsManager(
    @DefaultHttpClient okHttpClient: OkHttpClient, api: AnalyticsApi,
    @Named("bi_event_list") biEventList: List<String>,
    @Named("indicative_event_list") indicativeEventList: List<String>,
    @Named("sentry_event_list") sentryEventList: List<String>,
    indicativeAnalytics: IndicativeAnalytics
  ): AnalyticsManager {
    return AnalyticsManager.Builder()
      .addLogger(BackendEventLogger(api, BuildConfig.VERSION_CODE, BuildConfig.APPLICATION_ID), biEventList)
      .addLogger(IndicativeEventLogger(indicativeAnalytics), indicativeEventList)
      .addLogger(SentryEventLogger(), sentryEventList)
      .setAnalyticsNormalizer(KeysNormalizer())
      .setDebugLogger(LogcatAnalyticsLogger())
      .setKnockLogger(HttpClientKnockLogger(okHttpClient))
      .build()
  }
}
