package com.asfoundation.wallet.nfts.ui.nftlist

import com.appcoins.wallet.ui.arch.Async
import com.appcoins.wallet.ui.arch.BaseViewModel
import com.appcoins.wallet.ui.arch.SideEffect
import com.appcoins.wallet.ui.arch.ViewState
import com.asfoundation.wallet.nfts.domain.NFTItem
import com.asfoundation.wallet.nfts.list.NFTClick
import com.asfoundation.wallet.nfts.usecases.GetNFTListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class NFTSideEffect : com.appcoins.wallet.ui.arch.SideEffect {
  data class NavigateToInfo(val nftData: NFTItem) : NFTSideEffect()
}

data class NFTState(val nftListModelAsync: com.appcoins.wallet.ui.arch.Async<List<NFTItem>> = com.appcoins.wallet.ui.arch.Async.Uninitialized) :
  com.appcoins.wallet.ui.arch.ViewState

@HiltViewModel
class NFTViewModel @Inject constructor(private val getNFTList: GetNFTListUseCase) :
    com.appcoins.wallet.ui.arch.BaseViewModel<NFTState, NFTSideEffect>(initialState()) {

  companion object {
    fun initialState(): NFTState {
      return NFTState()
    }
  }

  fun fetchNFTList() {
    getNFTList().asAsyncToState(NFTState::nftListModelAsync) {
          copy(nftListModelAsync = it)
        }
        .repeatableScopedSubscribe(NFTState::nftListModelAsync.name) { e ->
          e.printStackTrace()
        }
  }

  fun nftClicked(nftClick: NFTClick) {
    sendSideEffect {
      NFTSideEffect.NavigateToInfo(nftClick.data)
    }
  }
}