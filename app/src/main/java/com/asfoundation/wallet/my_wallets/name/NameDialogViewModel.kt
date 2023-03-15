package com.asfoundation.wallet.my_wallets.name

import androidx.lifecycle.SavedStateHandle
import com.appcoins.wallet.ui.arch.Async
import com.appcoins.wallet.ui.arch.BaseViewModel
import com.appcoins.wallet.ui.arch.SideEffect
import com.appcoins.wallet.ui.arch.ViewState
import com.asfoundation.wallet.wallets.usecases.UpdateWalletNameUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class NameDialogSideEffect : com.appcoins.wallet.ui.arch.SideEffect {
  object NavigateBack : NameDialogSideEffect()
}

data class NameDialogState(
  val walletAddress: String,
  val walletNameAsync: com.appcoins.wallet.ui.arch.Async<String> = com.appcoins.wallet.ui.arch.Async.Uninitialized
) : com.appcoins.wallet.ui.arch.ViewState

@HiltViewModel
class NameDialogViewModel @Inject constructor(
  savedStateHandle: SavedStateHandle,
  private val updateWalletNameUseCase: UpdateWalletNameUseCase,
) :
  com.appcoins.wallet.ui.arch.BaseViewModel<NameDialogState, NameDialogSideEffect>(initialState(savedStateHandle)) {

  companion object {
    fun initialState(savedStateHandle: SavedStateHandle): NameDialogState = NameDialogState(
      savedStateHandle.get<String>(NameDialogFragment.WALLET_ADDRESS_KEY)!!,
      com.appcoins.wallet.ui.arch.Async.Success(savedStateHandle.get<String>(NameDialogFragment.WALLET_NAME_KEY)!!),
    )
  }

  fun setName(name: String) {
    val retainValue = NameDialogState::walletNameAsync
    state.walletNameAsync()?.also {
      updateWalletNameUseCase(state.walletAddress, name)
        .toSingleDefault(it)
        .delay(500, TimeUnit.MILLISECONDS) // for progress showing
        .asAsyncToState(retainValue) { walletName -> copy(walletNameAsync = walletName) }
        .doOnSuccess { sendSideEffect { NameDialogSideEffect.NavigateBack } }
        .scopedSubscribe(Throwable::printStackTrace)
    }
  }
}