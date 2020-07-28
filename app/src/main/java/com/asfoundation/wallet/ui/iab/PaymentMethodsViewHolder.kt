package com.asfoundation.wallet.ui.iab

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.asf.wallet.R
import com.asfoundation.wallet.GlideApp
import kotlinx.android.synthetic.main.item_payment_method.view.*

class PaymentMethodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

  fun bind(data: PaymentMethod, checked: Boolean, listener: View.OnClickListener) {
    GlideApp.with(itemView.context)
        .load(data.iconUrl)
        .into(itemView.payment_method_ic)

    val selected = data.isEnabled && checked
    itemView.radio_button.isChecked = selected
    itemView.radio_button.isEnabled = data.isEnabled

    setDescription(data, selected)

    if (data.isEnabled) {
      itemView.setOnClickListener(listener)
      itemView.radio_button.visibility = View.VISIBLE
      hideDisableReason()
      removeAlphaScale(itemView.payment_method_ic)
    } else {
      itemView.setOnClickListener(null)
      itemView.radio_button.visibility = View.INVISIBLE
      itemView.background = null
      if (data.disabledReason != null) {
        showDisableReason(data.disabledReason)
      } else {
        hideDisableReason()
      }

      applyAlphaScale(itemView.payment_method_ic)
    }
  }

  private fun setDescription(data: PaymentMethod, selected: Boolean) {
    itemView.payment_method_description.text = data.label
    if (selected) {
      itemView.payment_method_description.setTextColor(
          ContextCompat.getColor(itemView.context, R.color.details_address_text_color))
      itemView.payment_method_description.typeface =
          Typeface.create("sans-serif-medium", Typeface.NORMAL)
    } else {
      itemView.payment_method_description.setTextColor(
          ContextCompat.getColor(itemView.context, R.color.grey_alpha_active_54))
      itemView.payment_method_description.typeface = Typeface.create("sans-serif", Typeface.NORMAL)
    }
  }

  private fun applyAlphaScale(imageView: ImageView) {
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    imageView.colorFilter = filter
  }

  private fun removeAlphaScale(imageView: ImageView) {
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(1f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    imageView.colorFilter = filter
  }

  private fun showDisableReason(@StringRes reason: Int?) {
    reason?.let {
      itemView.payment_method_reason.visibility = View.VISIBLE
      itemView.payment_method_reason.text = itemView.context.getString(it)
    }
  }

  private fun hideDisableReason() {
    itemView.payment_method_reason.visibility = View.GONE
    itemView.payment_method_reason.text = null
  }


}