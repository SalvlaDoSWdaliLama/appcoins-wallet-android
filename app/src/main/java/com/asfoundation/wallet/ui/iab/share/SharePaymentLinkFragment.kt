package com.asfoundation.wallet.ui.iab.share

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ShareCompat
import androidx.core.content.res.ResourcesCompat
import com.asf.wallet.R
import com.asf.wallet.databinding.FragmentSharePaymentLinkBinding
import com.asfoundation.wallet.billing.analytics.BillingAnalytics
import com.asfoundation.wallet.ui.iab.IabView
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import com.jakewharton.rxbinding2.view.RxView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import javax.inject.Inject

@AndroidEntryPoint
class SharePaymentLinkFragment : BasePageViewFragment(),
    SharePaymentLinkFragmentView {

  @Inject
  lateinit var interactor: ShareLinkInteractor

  lateinit var presenter: SharePaymentLinkPresenter
  private var iabView: IabView? = null

  @Inject
  lateinit var analytics: BillingAnalytics

  companion object {

    private const val PARAM_DOMAIN = "AMOUNT_DOMAIN"
    private const val PARAM_SKUID = "AMOUNT_SKUID"
    private const val PARAM_ORIGINAL_AMOUNT = "PARAM_ORIGINAL_AMOUNT"
    private const val PARAM_AMOUNT = "PARAM_AMOUNT"
    private const val PARAM_ORIGINAL_CURRENCY = "PARAM_ORIGINAL_CURRENCY"
    private const val PARAM_TRANSACTION_TYPE = "PARAM_TRANSACTION_TYPE"
    private const val PAYMENT_METHOD_NAME = "ASK_SOMEONE"
    private const val PARAM_PAYMENT_KEY = "PAYMENT_NAME"

    @JvmStatic
    fun newInstance(domain: String, skuId: String?, originalAmount: String?,
                    originalCurrency: String?, amount: BigDecimal,
                    type: String, paymentMethod: String): SharePaymentLinkFragment =
        SharePaymentLinkFragment().apply {
          arguments = Bundle(2).apply {
            putString(PARAM_DOMAIN, domain)
            putString(PARAM_SKUID, skuId)
            putString(PARAM_ORIGINAL_AMOUNT, originalAmount)
            putString(PARAM_ORIGINAL_CURRENCY, originalCurrency)
            putString(PARAM_TRANSACTION_TYPE, type)
            putString(PARAM_PAYMENT_KEY, paymentMethod)
            putSerializable(PARAM_AMOUNT, amount)
          }
        }
  }

  val domain: String by lazy {
    if (requireArguments().containsKey(PARAM_DOMAIN)) {
      requireArguments().getString(PARAM_DOMAIN)!!
    } else {
      throw IllegalArgumentException("Domain not found")
    }
  }

  val paymentMethod: String by lazy {
    if (requireArguments().containsKey(PARAM_PAYMENT_KEY)) {
      requireArguments().getString(PARAM_PAYMENT_KEY)!!
    } else {
      throw IllegalArgumentException("paymentMethod not found")
    }
  }

  val type: String by lazy {
    if (requireArguments().containsKey(PARAM_TRANSACTION_TYPE)) {
      requireArguments().getString(PARAM_TRANSACTION_TYPE)!!
    } else {
      throw IllegalArgumentException("type not found")
    }
  }

  private val originalAmount: String? by lazy {
    if (requireArguments().containsKey(
            PARAM_ORIGINAL_AMOUNT)) {
      requireArguments().getString(
          PARAM_ORIGINAL_AMOUNT)
    } else {
      throw IllegalArgumentException("Original amount not found")
    }
  }

  private val originalCurrency: String? by lazy {
    if (requireArguments().containsKey(
            PARAM_ORIGINAL_CURRENCY)) {
      requireArguments().getString(
          PARAM_ORIGINAL_CURRENCY)
    } else {
      throw IllegalArgumentException("Domain not found")
    }
  }

  val skuId: String? by lazy {
    if (requireArguments().containsKey(
            PARAM_SKUID)) {
      val value = requireArguments().getString(
          PARAM_SKUID) ?: return@lazy null
      value
    } else {
      throw IllegalArgumentException("SkuId not found")
    }
  }

  val amount: BigDecimal by lazy {
    if (requireArguments().containsKey(
            PARAM_AMOUNT)) {
      val value = requireArguments().getSerializable(
          PARAM_AMOUNT) as BigDecimal
      value
    } else {
      throw IllegalArgumentException("amount not found")
    }
  }

  private var _binding: FragmentSharePaymentLinkBinding? = null
  // This property is only valid between onCreateView and
  // onDestroyView.
  private val binding get() = _binding!!

  // fragment_share_payment_link.xml
  private val note get() = binding.note
  private val share_link_title get() = binding.shareLinkTitle
  private val share_btn get() = binding.shareBtn
  private val close_btn get() = binding.closeBtn

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    if (savedInstanceState == null) {
      analytics.sendPaymentEvent(domain, skuId, amount.toString(), PAYMENT_METHOD_NAME, type)
    }
    presenter =
        SharePaymentLinkPresenter(this, interactor, AndroidSchedulers.mainThread(), Schedulers.io(),
            CompositeDisposable(), analytics)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    _binding = FragmentSharePaymentLinkBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    presenter.present()
  }

  override fun onAttach(context: Context) {
    if (context !is IabView) {
      throw IllegalStateException("share payment link fragment must be attached to IAB activity")
    }
    iabView = context
    super.onAttach(context)
  }

  override fun onDetach() {
    iabView = null
    super.onDetach()
  }

  override fun onDestroyView() {
    presenter.stop()
    super.onDestroyView()
  }

  override fun getShareButtonClick(): Observable<SharePaymentLinkFragmentView.SharePaymentData> {
    return RxView.clicks(share_btn)
        .map {
          val message = if (note.text.isNotEmpty()) note.text.toString() else null
          SharePaymentLinkFragmentView.SharePaymentData(domain, skuId, message, originalAmount,
              originalCurrency, paymentMethod, amount.toFloat()
              .toString(), type)
        }
  }

  override fun getCancelButtonClick(): Observable<SharePaymentLinkFragmentView.SharePaymentData> {
    return RxView.clicks(close_btn)
        .map {
          val message = if (note.text.isNotEmpty()) note.text.toString() else null
          SharePaymentLinkFragmentView.SharePaymentData(domain, skuId, message, originalAmount,
              originalCurrency, paymentMethod, amount.toFloat()
              .toString(), type)
        }
  }

  override fun showFetchingLinkInfo() {
    share_link_title.text = getString(R.string.askafriend_generating_link_message)
    share_link_title.setTextColor(
        ResourcesCompat.getColor(resources, R.color.styleguide_black, null))
    close_btn.visibility = View.INVISIBLE
    share_btn.visibility = View.INVISIBLE
  }

  override fun showErrorInfo() {
    share_link_title.text = getString(R.string.askafriend_generating_link_error_message)
    share_link_title.setTextColor(
        ResourcesCompat.getColor(resources, R.color.styleguide_red, null))
    close_btn.visibility = View.VISIBLE
    share_btn.visibility = View.VISIBLE
  }

  override fun shareLink(url: String) {
    share_link_title.text = getString(R.string.askafriend_share_body)
    share_link_title.setTextColor(
        ResourcesCompat.getColor(resources, R.color.styleguide_black, null))
    close_btn.visibility = View.VISIBLE
    share_btn.visibility = View.VISIBLE

    activity?.let {
      ShareCompat.IntentBuilder.from(it)
          .setText(url)
          .setType("text/plain")
          .setChooserTitle(R.string.askafriend_share_popup_title)
          .startChooser()
    }
  }

  override fun close() {
    iabView?.close(Bundle())
  }
}