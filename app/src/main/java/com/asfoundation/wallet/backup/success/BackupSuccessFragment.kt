package com.asfoundation.wallet.backup.success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import by.kirich1409.viewbindingdelegate.viewBinding
import com.asf.wallet.R
import com.asf.wallet.databinding.BackupSuccessFragmentBinding
import com.asfoundation.wallet.backup.repository.preferences.BackupTriggerPreferences
import com.appcoins.wallet.ui.arch.SideEffect
import com.appcoins.wallet.ui.arch.SingleStateFragment
import com.appcoins.wallet.ui.arch.ViewState
import com.asfoundation.wallet.billing.analytics.WalletsAnalytics
import com.asfoundation.wallet.billing.analytics.WalletsEventSender
import com.asfoundation.wallet.viewmodel.BasePageViewFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class BackupSuccessFragment : BasePageViewFragment(),
  com.appcoins.wallet.ui.arch.SingleStateFragment<com.appcoins.wallet.ui.arch.ViewState, com.appcoins.wallet.ui.arch.SideEffect> {

  @Inject
  lateinit var backupTriggerPreferences: BackupTriggerPreferences

  @Inject
  lateinit var walletsEventSender: WalletsEventSender

  private val views by viewBinding(BackupSuccessFragmentBinding::bind)

  companion object {
    const val EMAIL_KEY = "email"
    const val WALLET_ADDRESS_KEY = "wallet_address_key"

    @JvmStatic
    fun newInstance(walletAddress: String, email: Boolean): BackupSuccessFragment {
      return BackupSuccessFragment()
        .apply {
          arguments = Bundle().apply {
            putString(WALLET_ADDRESS_KEY, walletAddress)
            putBoolean(EMAIL_KEY, email)
          }
        }
    }
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.backup_success_fragment, container, false)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    backupTriggerPreferences.setTriggerState(
      walletAddress = requireArguments().getString(WALLET_ADDRESS_KEY, ""),
      active = false,
      triggerSource = BackupTriggerPreferences.TriggerSource.DISABLED
    )

    views.closeButton.setOnClickListener {
      walletsEventSender.sendBackupConclusionEvent(
        WalletsAnalytics.ACTION_UNDERSTAND
      )
      this.activity?.finish()
    }

    setSuccessInfo()
  }

  private fun setSuccessInfo() {
    var info = R.string.backup_success_save_on_device

    if (requireArguments().getBoolean(EMAIL_KEY)) {
      info = R.string.backup_success_save_on_email
    }

    views.backupSuccessInfo.body.text = context?.getString(info)
  }

  override fun onStateChanged(state: com.appcoins.wallet.ui.arch.ViewState) = Unit

  override fun onSideEffect(sideEffect: com.appcoins.wallet.ui.arch.SideEffect) = Unit
}
