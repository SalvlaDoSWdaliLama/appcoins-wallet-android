package com.asfoundation.wallet.promo_code.use_cases

import com.asfoundation.wallet.promo_code.repository.PromoCodeEntity
import com.asfoundation.wallet.promo_code.repository.PromoCodeRepository
import io.reactivex.Observable
import io.reactivex.Single

class GetCurrentPromoCodeUseCase(private val promoCodeRepository: PromoCodeRepository) {
  operator fun invoke(): Single<PromoCodeEntity> {
    return promoCodeRepository.getCurrentPromoCode().firstOrError()
  }
}