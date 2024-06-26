package com.asfoundation.wallet.topup.paymentMethods

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.asfoundation.wallet.ui.iab.PaymentMethod
import com.asfoundation.wallet.ui.iab.PaymentMethodsViewHolder
import com.jakewharton.rxrelay2.PublishRelay


class TopUpPaymentMethodsAdapter(
  private var paymentMethods: List<PaymentMethod>,
  private var paymentMethodClick: PublishRelay<String>,
  private val showPaypalLogout: Boolean,
  private val wasLoggedOut: () -> Boolean,
  private val logoutCallback: () -> Unit
) :
  RecyclerView.Adapter<PaymentMethodsViewHolder>() {
  private var selectedItem = 0

  fun setSelectedItem(position: Int) {
    selectedItem = position
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentMethodsViewHolder {
    return PaymentMethodsViewHolder(
      LayoutInflater.from(parent.context)
        .inflate(R.layout.item_payment_method, parent, false)
    )
  }

  override fun getItemCount() = paymentMethods.size

  override fun onBindViewHolder(holder: PaymentMethodsViewHolder, position: Int) {
    holder.bind(
      data = paymentMethods[position],
      checked = selectedItem == position,
      listener = View.OnClickListener {
        selectedItem = position
        paymentMethodClick.accept(paymentMethods[position].id)
        notifyDataSetChanged()
      },
      onClickListenerTopup = { },
      showPaypalLogout = showPaypalLogout,
      onClickPaypalLogout = logoutCallback,
      wasLoggedOut = wasLoggedOut
    )
  }

  fun getSelectedItemData(): PaymentMethod = paymentMethods[selectedItem]
}