package com.asfoundation.wallet.ui.transact

import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.asf.wallet.R
import com.asfoundation.wallet.C
import com.asfoundation.wallet.entity.TokenInfo
import com.asfoundation.wallet.entity.TransactionBuilder
import com.asfoundation.wallet.interact.DefaultTokenProvider
import com.asfoundation.wallet.transfers.TransferConfirmationActivity
import com.asfoundation.wallet.ui.barcode.BarcodeCaptureActivity
import com.asfoundation.wallet.ui.iab.IabActivity
import com.asfoundation.wallet.wallet_blocked.WalletBlockedActivity
import io.reactivex.Completable
import java.math.BigDecimal
import javax.inject.Inject

class TransferFragmentNavigator @Inject constructor(private val fragmentManager: FragmentManager,
                                private val fragment: Fragment,
                                private val defaultTokenProvider: DefaultTokenProvider) {

  companion object {
    const val TRANSACTION_CONFIRMATION_REQUEST_CODE = 12344
    const val BARCODE_READER_REQUEST_CODE = 1
  }

  fun openAppcConfirmationView(walletAddress: String, toWalletAddress: String,
                               amount: BigDecimal): Completable {
    return defaultTokenProvider.defaultToken.doOnSuccess {
      with(TransactionBuilder(it)) {
        amount(amount)
        toAddress(toWalletAddress)
        fromAddress(walletAddress)
        openConfirmation(this)
      }
    }
        .ignoreElement()
  }

  fun openEthConfirmationView(walletAddress: String, toWalletAddress: String,
                              amount: BigDecimal): Completable {
    return Completable.fromAction {
      val transaction = TransactionBuilder(TokenInfo(null, "Ethereum", "ETH", 18))
      transaction.amount(amount)
      transaction.toAddress(toWalletAddress)
      transaction.fromAddress(walletAddress)
      openConfirmation(transaction)
    }
  }

  fun openAppcCreditsConfirmationView(walletAddress: String,
                                      amount: BigDecimal,
                                      currency: TransferFragmentView.Currency): Completable {
    return Completable.fromAction {
      val currencyName = when (currency) {
        TransferFragmentView.Currency.APPC_C -> fragment.getString(
            R.string.p2p_send_currency_appc_c)
        TransferFragmentView.Currency.APPC -> fragment.getString(R.string.p2p_send_currency_appc)
        TransferFragmentView.Currency.ETH -> fragment.getString(R.string.p2p_send_currency_eth)
      }
      fragmentManager.beginTransaction()
          .replace(R.id.fragment_container,
              AppcoinsCreditsTransferSuccessFragment.newInstance(amount, currencyName,
                  walletAddress))
          .commit()
    }
  }

  private fun openConfirmation(transactionBuilder: TransactionBuilder) {
    val intent = Intent(fragment.context, TransferConfirmationActivity::class.java).apply {
      putExtra(C.EXTRA_TRANSACTION_BUILDER, transactionBuilder)
    }
    fragment.startActivityForResult(intent, TRANSACTION_CONFIRMATION_REQUEST_CODE)
  }

  fun navigateBack() = fragment.requireActivity().onBackPressed()

  fun showWalletBlocked() {
    fragment.startActivityForResult(WalletBlockedActivity.newIntent(fragment.requireActivity()),
        IabActivity.BLOCKED_WARNING_REQUEST_CODE)
  }

  fun showQrCodeScreen() {
    val intent = Intent(fragment.requireActivity(), BarcodeCaptureActivity::class.java)
    fragment.startActivityForResult(intent, BARCODE_READER_REQUEST_CODE)
  }

  fun showLoading() {
    fragmentManager.beginTransaction()
        .add(android.R.id.content, LoadingFragment.newInstance(), LoadingFragment::class.java.name)
        .commit()
  }

  fun hideLoading() {
    val fragment = fragmentManager.findFragmentByTag(LoadingFragment::class.java.name)
    if (fragment != null) {
      fragmentManager.beginTransaction()
          .remove(fragment)
          .commit()
    }
  }
}
