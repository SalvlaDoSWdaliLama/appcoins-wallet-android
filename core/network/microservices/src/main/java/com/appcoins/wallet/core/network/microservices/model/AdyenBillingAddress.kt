package com.appcoins.wallet.core.network.microservices.model

import com.google.gson.annotations.SerializedName

data class AdyenBillingAddress(
    val street: String,
    val city: String,
    @SerializedName("postalCode") val postalCode: String,
    @SerializedName("houseNumberOrName") val houseNumberOrName: String,
    @SerializedName("stateOrProvince") val stateOrProvince: String,
    val country: String
)