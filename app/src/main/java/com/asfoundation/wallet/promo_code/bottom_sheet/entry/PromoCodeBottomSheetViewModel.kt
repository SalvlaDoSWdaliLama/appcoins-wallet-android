package com.asfoundation.wallet.promo_code.bottom_sheet.entry

import com.asfoundation.wallet.base.Async
import com.asfoundation.wallet.base.BaseViewModel
import com.asfoundation.wallet.base.SideEffect
import com.asfoundation.wallet.base.ViewState
import com.asfoundation.wallet.promo_code.bottom_sheet.FailedPromoCode
import com.asfoundation.wallet.promo_code.bottom_sheet.PromoCodeResult
import com.asfoundation.wallet.promo_code.use_cases.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

sealed class PromoCodeBottomSheetSideEffect : SideEffect {
  object NavigateBack : PromoCodeBottomSheetSideEffect()
}

data class PromoCodeBottomSheetState(
  val promoCodeAsync: Async<PromoCodeResult> = Async.Uninitialized,
  val submitClickAsync: Async<Unit> = Async.Uninitialized,
  val shouldShowDefault: Boolean = false
) : ViewState

@HiltViewModel
class PromoCodeBottomSheetViewModel @Inject constructor(
  private val observePromoCodeResultUseCase: ObservePromoCodeResultUseCase,
  private val setPromoCodeUseCase: SetPromoCodeUseCase,
  private val deletePromoCodeUseCase: DeletePromoCodeUseCase
) :
  BaseViewModel<PromoCodeBottomSheetState, PromoCodeBottomSheetSideEffect>(initialState()) {

  companion object {
    fun initialState(): PromoCodeBottomSheetState {
      return PromoCodeBottomSheetState()
    }
  }

  init {
    getCurrentPromoCode()
  }

  private fun getCurrentPromoCode() {
    observePromoCodeResultUseCase()
      .asAsyncToState { copy(promoCodeAsync = it) }
      .doOnNext {
        if (it == FailedPromoCode.ExpiredCode()) deleteCode()
      }
      .repeatableScopedSubscribe(PromoCodeBottomSheetState::promoCodeAsync.name) { e ->
        e.printStackTrace()
      }
  }

  fun submitClick(promoCodeString: String) {
    setPromoCodeUseCase(promoCodeString)
      .asAsyncToState { copy(submitClickAsync = it) }
      .scopedSubscribe()
  }

  fun replaceClick() = setState { copy(shouldShowDefault = true) }

  fun deleteClick() {
    deletePromoCodeUseCase()
      .asAsyncToState { copy(promoCodeAsync = Async.Uninitialized) }
      .doOnComplete {
        sendSideEffect {
          PromoCodeBottomSheetSideEffect.NavigateBack
        }
      }
      .scopedSubscribe()
  }

  private fun deleteCode() {
    deletePromoCodeUseCase()
      .asAsyncToState { copy(promoCodeAsync = Async.Uninitialized) }
      .scopedSubscribe()
  }
}