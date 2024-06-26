package com.asfoundation.wallet.backup.save_on_device

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.asf.wallet.R
import com.asfoundation.wallet.backup.success.BackupSuccessFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class BackupSaveOnDeviceDialogNavigator @Inject constructor(val fragment: Fragment, val fragmentManager: FragmentManager) {

  fun dismiss() {
    (fragment as BottomSheetDialogFragment).dismiss()
  }

  fun navigateBack() {
    (fragment as BottomSheetDialogFragment).dismiss()
  }

  fun navigateToSuccessScreen(walletAddress: String) {
    fragmentManager.commit {
      replace(R.id.fragment_container, BackupSuccessFragment.newInstance(walletAddress, false))
      addToBackStack("SaveBackupDialogFragment")
    }
    dismiss()
  }
}