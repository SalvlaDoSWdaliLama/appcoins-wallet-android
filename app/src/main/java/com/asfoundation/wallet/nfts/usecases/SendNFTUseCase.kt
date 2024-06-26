package com.asfoundation.wallet.nfts.usecases

import com.asfoundation.wallet.nfts.domain.NFTItem
import com.asfoundation.wallet.nfts.domain.NftTransferResult
import com.asfoundation.wallet.nfts.repository.NFTRepository
import com.asfoundation.wallet.repository.PasswordStore
import com.asfoundation.wallet.service.KeyStoreFileManager
import com.asfoundation.wallet.wallets.usecases.GetCurrentWalletUseCase
import io.reactivex.Single
import org.web3j.crypto.WalletUtils
import java.math.BigInteger
import javax.inject.Inject

class SendNFTUseCase @Inject constructor(
  private val getCurrentWallet: GetCurrentWalletUseCase,
  private val NFTRepository: NFTRepository,
  private val keyStoreFileManager: KeyStoreFileManager,
  private val passwordStore: PasswordStore
) {

  operator fun invoke(toAddress: String, nft: NFTItem, gasPrice: BigInteger,
                      gasLimit: BigInteger): Single<NftTransferResult> {
    return getCurrentWallet().flatMap { wallet ->
      passwordStore.getPassword(wallet.address)
          .flatMap { password ->
            NFTRepository.sendNFT(wallet.address, toAddress, nft.tokenId, nft.contractAddress,
                nft.schema, gasPrice, gasLimit, WalletUtils.loadCredentials(password,
                keyStoreFileManager.getKeystore(wallet.address)))
        }
    }
  }
}