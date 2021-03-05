package com.pocketdocket.model

data class Item(var name: String, var price: Double, var category: String, var description: String) {

    var dbPrimaryId = 0
    var menuId = 0
    var currencySign = "$"

    fun getPriceWithSign(): String {
        return "${currencySign}${price}"
    }
}