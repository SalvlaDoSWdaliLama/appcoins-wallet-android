package com.asfoundation.wallet.verification.ui.credit_card.intro

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.adyen.checkout.card.CardConfiguration
import com.adyen.checkout.core.api.Environment
import com.appcoins.wallet.billing.adyen.PaymentInfoModel
import com.appcoins.wallet.billing.adyen.VerificationPaymentModel
import com.asf.wallet.BuildConfig
import com.asf.wallet.R
import com.asfoundation.wallet.billing.adyen.AdyenCardWrapper
import com.asfoundation.wallet.util.AdyenCardView
import com.appcoins.wallet.core.utils.android_common.CurrencyFormatUtils
import com.appcoins.wallet.core.utils.android_common.KeyboardUtils
import com.appcoins.wallet.core.utils.android_common.WalletCurrency
import com.asf.wallet.databinding.FragmentVerificationIntroBinding
import com.asfoundation.wallet.verification.ui.credit_card.VerificationCreditCardActivityView
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import com.jakewharton.rxbinding2.view.RxView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

@AndroidEntryPoint
class VerificationIntroFragment : BasePageViewFragment(), VerificationIntroView {

  companion object {

    @JvmStatic
    fun newInstance() = VerificationIntroFragment()
  }

  @Inject
  lateinit var presenter: VerificationIntroPresenter

  @Inject
  lateinit var formatter: CurrencyFormatUtils

  @Inject
  lateinit var adyenEnvironment: Environment

  private lateinit var activityView: VerificationCreditCardActivityView
  private lateinit var cardConfiguration: CardConfiguration
  private lateinit var adyenCardView: AdyenCardView

  private var isStored = false
  private var paymentDataSubject: BehaviorSubject<AdyenCardWrapper> = BehaviorSubject.create()

  private var _binding: FragmentVerificationIntroBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  // fragment_verification_intro.xml
  private val adyen_card_form get() = binding.adyenCardForm.root
  private val description get() = binding.description
  private val change_card_button get() = binding.changeCardButton
  private val submit get() = binding.submit
  private val cancel get() = binding.cancel
  private val no_network get() = binding.noNetwork.root
  private val fragment_adyen_error get() = binding.fragmentAdyenError.root
  private val content_container get() = binding.contentContainer
  private val progress_bar get() = binding.progressBar

  // error_top_up_layout.xml
  private val layout_support_icn get() = binding.fragmentAdyenError.layoutSupportIcn
  private val layout_support_logo get() = binding.fragmentAdyenError.layoutSupportLogo
  private val try_again get() = binding.fragmentAdyenError.tryAgain
  private val error_message get() = binding.fragmentAdyenError.errorMessage

  // no_network_retry_only_layout.xml
  private val retry_button get() = binding.noNetwork.retryButton

  // selected_payment_method_cc.xml
  private val adyen_card_form_pre_selected get() = binding.adyenCardForm.adyenCardFormPreSelected
  private val adyen_card_form_pre_selected_number get() = binding.adyenCardForm.adyenCardFormPreSelectedNumber
  private val payment_method_ic get() = binding.adyenCardForm.paymentMethodIc

  override fun onAttach(context: Context) {
    super.onAttach(context)

    require(context is VerificationCreditCardActivityView) {
      throw IllegalStateException(
        "Wallet Verification Intro must be attached to Wallet Verification Activity"
      )
    }
    activityView = context
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    _binding = FragmentVerificationIntroBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setupUi()
    presenter.present(savedInstanceState)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    presenter.onSavedInstance(outState)
  }

  private fun setupUi() {
    adyenCardView = AdyenCardView(adyen_card_form)
    setupCardConfiguration()
  }

  private fun setupCardConfiguration() {
    cardConfiguration = CardConfiguration
      .Builder(activity as Context, BuildConfig.ADYEN_PUBLIC_KEY)
      .setEnvironment(adyenEnvironment)
      .build()
  }

  @SuppressLint("StringFormatInvalid")
  override fun updateUi(verificationIntroModel: VerificationIntroModel) {
    val amount = formatter.formatCurrency(
      verificationIntroModel.verificationInfoModel.value,
      WalletCurrency.FIAT
    )
    description.text = getString(
      R.string.card_verification_charde_disclaimer,
      "${verificationIntroModel.verificationInfoModel.symbol}$amount"
    )
  }

  override fun finishCardConfiguration(paymentInfoModel: PaymentInfoModel, forget: Boolean) {
    this.isStored = paymentInfoModel.isStored

    prepareCardComponent(paymentInfoModel, forget)
    handleLayoutVisibility(isStored)
    setStoredPaymentInformation(isStored)
  }

  private fun handleLayoutVisibility(isStored: Boolean) {
    adyenCardView.showInputFields(!isStored)
    change_card_button.visibility = if (isStored) View.VISIBLE else View.GONE
    if (isStored) {
      view?.let { KeyboardUtils.showKeyboard(it) }
    }
  }

  private fun prepareCardComponent(
    paymentInfoModel: PaymentInfoModel,
    forget: Boolean
  ) {
    if (forget) adyenCardView.clear(this)
    val cardComponent = paymentInfoModel.cardComponent!!(this, cardConfiguration)
    adyen_card_form_pre_selected?.attach(cardComponent, this)
    cardComponent.observe(this) {
      adyenCardView.setError(null)
      if (it != null && it.isValid) {
        submit.isEnabled = true
        view?.let { view -> KeyboardUtils.hideKeyboard(view) }
        it.data.paymentMethod?.let { paymentMethod ->
          val hasCvc = !paymentMethod.encryptedSecurityCode.isNullOrEmpty()
          paymentDataSubject.onNext(
            AdyenCardWrapper(
              paymentMethod,
              adyenCardView.cardSave,
              hasCvc,
              paymentInfoModel.supportedShopperInteractions
            )
          )
        }
      } else {
        submit.isEnabled = false
      }
    }
  }

  private fun setStoredPaymentInformation(isStored: Boolean) {
    if (isStored) {
      adyen_card_form_pre_selected_number?.text = adyenCardView.cardNumber
      adyen_card_form_pre_selected_number?.visibility = View.VISIBLE
      payment_method_ic?.setImageDrawable(adyenCardView.cardImage)
    } else {
      adyen_card_form_pre_selected_number?.visibility = View.GONE
      payment_method_ic?.visibility = View.GONE
    }
  }

  override fun getCancelClicks() = RxView.clicks(cancel)

  override fun getSubmitClicks() = RxView.clicks(submit)

  override fun forgetCardClick(): Observable<Any> = RxView.clicks(change_card_button)

  override fun getTryAgainClicks() = RxView.clicks(try_again)

  override fun retryClick() = RxView.clicks(retry_button)

  override fun getSupportClicks(): Observable<Any> {
    return Observable.merge(RxView.clicks(layout_support_logo), RxView.clicks(layout_support_icn))
  }

  override fun showLoading() {
    no_network.visibility = View.GONE
    fragment_adyen_error?.visibility = View.GONE
    content_container.visibility = View.GONE
    progress_bar.visibility = View.VISIBLE
  }

  override fun hideLoading() {
    progress_bar.visibility = View.GONE
    content_container.visibility = View.VISIBLE
    activityView.hideLoading()
  }

  override fun showError(errorType: VerificationPaymentModel.ErrorType?) {
    if (errorType == VerificationPaymentModel.ErrorType.TOO_MANY_ATTEMPTS) {
      showSpecificError(R.string.verification_error_attempts_reached)
    } else {
      showSpecificError(R.string.unknown_error)
    }
  }

  override fun showGenericError() = showSpecificError(R.string.unknown_error)

  override fun showNetworkError() {
    unlockRotation()
    progress_bar.visibility = View.GONE
    content_container.visibility = View.GONE
    no_network.visibility = View.VISIBLE
  }

  override fun showSpecificError(stringRes: Int) {
    unlockRotation()
    progress_bar.visibility = View.GONE
    content_container.visibility = View.GONE

    val message = getString(stringRes)
    error_message?.text = message
    fragment_adyen_error?.visibility = View.VISIBLE
  }

  override fun showCvvError() {
    unlockRotation()
    progress_bar.visibility = View.GONE
    submit.isEnabled = false
    if (isStored) {
      change_card_button.visibility = View.VISIBLE
    } else {
      change_card_button.visibility = View.INVISIBLE
    }
    content_container.visibility = View.VISIBLE
    adyenCardView.setError(getString(R.string.purchase_card_error_CVV))
  }

  override fun retrievePaymentData() = paymentDataSubject

  override fun hideKeyboard() {
    view?.let { KeyboardUtils.hideKeyboard(view) }
  }

  override fun lockRotation() = activityView.lockRotation()

  override fun unlockRotation() = activityView.unlockRotation()

  override fun cancel() = activityView.cancel()

  override fun onDestroyView() {
    presenter.stop()
    super.onDestroyView()
    _binding = null
  }
}