package com.asfoundation.wallet.nfts.ui.nftTransactDialog

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asf.wallet.R
import com.asf.wallet.databinding.FragmentNftTransactBinding
import com.asfoundation.wallet.base.Async
import com.asfoundation.wallet.base.SingleStateFragment
import com.asfoundation.wallet.nfts.domain.GasInfo
import com.asfoundation.wallet.util.BalanceUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.apache.commons.lang3.StringUtils.capitalize
import java.math.BigInteger
import java.math.RoundingMode
import javax.inject.Inject

@AndroidEntryPoint
class NFTTransactDialogFragment : BottomSheetDialogFragment(),
    SingleStateFragment<NFTTransactState, NFTTransactSideEffect> {

  @Inject
  lateinit var viewModelFactory: NFTTransactDialogViewModelFactory

  private val viewModel: NFTTransactDialogViewModel by viewModels { viewModelFactory }
  private val views by viewBinding(FragmentNftTransactBinding::bind)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    return inflater.inflate(R.layout.fragment_nft_transact, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.collectStateAndEvents(lifecycle, viewLifecycleOwner.lifecycleScope)
    setListeners()
  }

  override fun onStart() {
    val behavior = BottomSheetBehavior.from(requireView().parent as View)
    behavior.state = BottomSheetBehavior.STATE_EXPANDED
    super.onStart()
  }

  override fun getTheme(): Int = R.style.AppBottomSheetDialogTheme

  override fun onStateChanged(state: NFTTransactState) {
    views.layoutNftTransactDone.doneMensage.text =
        getString(R.string.nfts_transact_done_mensage, state.data.name)

    when (val transactionHash = state.transactionHashAsync) {
      is Async.Fail -> showError(getString(R.string.nfts_generic_error))
      is Async.Loading -> showLoading()
      is Async.Success -> if (transactionHash.value?.startsWith(
              "0x") == true) showSuccess() else transactionHash.value?.let {
        showError(it)
      }
      Async.Uninitialized -> setGasPrice(state.gasPriceAsync)
    }
  }

  private fun setGasPrice(gasInfoAsync: Async<GasInfo>) {
    when (gasInfoAsync) {
      is Async.Fail -> showError(getString(R.string.nfts_generic_error))
      is Async.Loading -> showLoading()
      is Async.Success -> showPickGas(gasInfoAsync())
      Async.Uninitialized -> Unit
    }
  }

  private fun showPickGas(gasInfo: GasInfo) {
    views.layoutNftTransactEntry.root.visibility = View.INVISIBLE
    views.layoutNftTransactDone.root.visibility = View.GONE
    views.layoutNftTransactLoading.root.visibility = View.GONE
    views.layoutNftTransactPickGas.gasPriceInput.text = Editable.Factory.getInstance()
        .newEditable(gasInfo.gasPrice.toString())
    views.layoutNftTransactPickGas.gasPriceInput.doAfterTextChanged {
      updateFee(gasInfo.copyWith(
          gasPrice = BigInteger(views.layoutNftTransactPickGas.gasPriceInput.text.toString()),
          gasLimit = BigInteger(views.layoutNftTransactPickGas.gasLimitInput.text.toString())))
    }
    views.layoutNftTransactPickGas.gasLimitInput.text = Editable.Factory.getInstance()
        .newEditable(gasInfo.gasLimit.toString())
    views.layoutNftTransactPickGas.gasLimitInput.doAfterTextChanged {
      updateFee(gasInfo.copyWith(
          gasPrice = BigInteger(views.layoutNftTransactPickGas.gasPriceInput.text.toString()),
          gasLimit = BigInteger(views.layoutNftTransactPickGas.gasLimitInput.text.toString())))
    }
    updateFee(gasInfo)
    views.layoutNftTransactPickGas.root.visibility = View.VISIBLE
  }

  private fun updateFee(gasInfo: GasInfo) {
    val fiat = gasInfo.symbol + (BalanceUtils.weiToEth(
        (gasInfo.gasPrice * gasInfo.gasLimit).toBigDecimal()) * gasInfo.rate).setScale(4,
        RoundingMode.CEILING) + " " + gasInfo.currency
    val eth = BalanceUtils.weiToEth((gasInfo.gasPrice * gasInfo.gasLimit).toBigDecimal())
        .setScale(6, RoundingMode.FLOOR)
        .toString() + " ETH"

    views.layoutNftTransactPickGas.priceFiat.text = fiat
    views.layoutNftTransactPickGas.priceEth.text = eth
  }


  private fun showLoading() {
    views.layoutNftTransactEntry.root.visibility = View.INVISIBLE
    views.layoutNftTransactDone.root.visibility = View.GONE
    views.layoutNftTransactPickGas.root.visibility = View.GONE
    views.layoutNftTransactLoading.root.visibility = View.VISIBLE
  }

  private fun showSuccess() {
    views.layoutNftTransactEntry.root.visibility = View.INVISIBLE
    views.layoutNftTransactLoading.root.visibility = View.INVISIBLE
    views.layoutNftTransactPickGas.root.visibility = View.GONE
    views.layoutNftTransactDone.errorAnimation.visibility = View.GONE
    views.layoutNftTransactDone.successAnimation.visibility = View.VISIBLE
    views.layoutNftTransactDone.root.visibility = View.VISIBLE
  }

  private fun showError(errorMessage: String) {
    views.layoutNftTransactEntry.root.visibility = View.INVISIBLE
    views.layoutNftTransactLoading.root.visibility = View.INVISIBLE
    views.layoutNftTransactPickGas.root.visibility = View.GONE
    views.layoutNftTransactDone.successAnimation.visibility = View.INVISIBLE
    views.layoutNftTransactDone.errorAnimation.visibility = View.VISIBLE
    views.layoutNftTransactDone.doneMensage.text = capitalize(errorMessage)
    views.layoutNftTransactDone.root.visibility = View.VISIBLE
  }

  override fun onSideEffect(sideEffect: NFTTransactSideEffect) = Unit

  companion object {
    internal const val NFT_ITEM_DATA = "data"
  }

  private fun setListeners() {
    views.layoutNftTransactEntry.sendButton.setOnClickListener {
      viewModel.estimateGas(views.layoutNftTransactEntry.sendToAddress.text.toString())
    }

    views.layoutNftTransactDone.doneButton.setOnClickListener {
      dismiss()
    }

    views.layoutNftTransactPickGas.sendButton.setOnClickListener {
      viewModel.send(views.layoutNftTransactEntry.sendToAddress.text.toString(),
          BigInteger(views.layoutNftTransactPickGas.gasPriceInput.text.toString()),
          BigInteger(views.layoutNftTransactPickGas.gasLimitInput.text.toString()))
    }
  }
}