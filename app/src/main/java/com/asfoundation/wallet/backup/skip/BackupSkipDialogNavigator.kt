package com.asfoundation.wallet.backup.skip

import androidx.fragment.app.Fragment
import com.appcoins.wallet.sharedpreferences.BackupTriggerPreferencesDataSource
import com.appcoins.wallet.sharedpreferences.BackupTriggerPreferencesDataSource.TriggerSource.DISABLED
import com.asfoundation.wallet.backup.triggers.BackupTriggerDialogFragment
import com.asfoundation.wallet.backup.triggers.TriggerUtils.toJson
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class BackupSkipDialogNavigator @Inject constructor(
  val fragment: Fragment,
  val backupTriggerPreferences: BackupTriggerPreferencesDataSource
) {

  fun navigateBack(
    walletAddress: String,
    triggerSource: BackupTriggerPreferencesDataSource.TriggerSource
  ) {
    (fragment as BottomSheetDialogFragment).dismiss()
    val bottomSheet = BackupTriggerDialogFragment.newInstance(walletAddress, triggerSource)
    bottomSheet.isCancelable = false
    bottomSheet.show(fragment.parentFragmentManager, "BackupTriggerFromSkip")
  }

  fun finishBackup(walletAddress: String) {
    backupTriggerPreferences.setTriggerState(
      walletAddress = walletAddress,
      active = false,
      triggerSource = DISABLED.toJson()
    )
    (fragment as BottomSheetDialogFragment).dismiss()
  }
}