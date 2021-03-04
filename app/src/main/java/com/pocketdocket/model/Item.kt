package com.pocketdocket.model

data class Item(val name: String, val price: Double, val category: String, val description: String) {

    var currencySign = "$"

    fun getPriceWithSign(): String {
        return "${currencySign}${price}"
    }
}