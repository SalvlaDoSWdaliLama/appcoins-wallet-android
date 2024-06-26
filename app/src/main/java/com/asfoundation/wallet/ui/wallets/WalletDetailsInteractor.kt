package com.asfoundation.wallet.ui.wallets

import com.appcoins.wallet.gamification.Gamification
import com.asfoundation.wallet.interact.SetDefaultWalletInteractor
import com.asfoundation.wallet.promo_code.use_cases.GetCurrentPromoCodeUseCase
import com.asfoundation.wallet.support.SupportInteractor
import io.reactivex.Completable
import javax.inject.Inject

class WalletDetailsInteractor @Inject constructor(
  private val setDefaultWalletInteractor: SetDefaultWalletInteractor,
  private val supportInteractor: SupportInteractor,
  private val gamificationRepository: Gamification,
  private val getCurrentPromoCodeUseCase: GetCurrentPromoCodeUseCase
) {

  fun setActiveWallet(address: String): Completable = setDefaultWalletInteractor.set(address)

  fun setActiveWalletSupport(address: String): Completable = getCurrentPromoCodeUseCase()
    .flatMap { gamificationRepository.getUserLevel(address, it.code) }
    .doOnSuccess { supportInteractor.registerUser(it, address) }
    .doOnError(Throwable::printStackTrace)
    .ignoreElement()
    .onErrorComplete()
}
