package com.asfoundation.wallet.promo_code.use_cases

import com.asfoundation.wallet.promo_code.PromoCodeMapper
import com.asfoundation.wallet.promo_code.PromoCodeResult
import com.asfoundation.wallet.promo_code.repository.PromoCode
import com.asfoundation.wallet.promo_code.repository.PromoCodeRepository
import io.reactivex.Single
import javax.inject.Inject

class VerifyAndSavePromoCodeUseCase @Inject constructor(
  private val promoCodeRepository: PromoCodeRepository
) {

  operator fun invoke(promoCodeString: String): Single<PromoCodeResult> {
    return promoCodeRepository.verifyAndSavePromoCode(promoCodeString)
      .map { PromoCodeMapper().map(it) }
  }
}