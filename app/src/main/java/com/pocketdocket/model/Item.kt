package com.pocketdocket.model

data class Item(val name: String, val price: Double, val category: String, val description: String) {

    var id = 0
    var menuId = 0
    var currencySign = "$"

    fun getPriceWithSign(): String {
        return "${currencySign}${price}"
    }
}