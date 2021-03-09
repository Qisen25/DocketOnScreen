package com.pocketdocket.model

data class Item(var name: String, var price: Double, var category: String, var description: String) {

    var dbPrimaryId = 0
    var menuId = 0
    var currencySign = "$"

    fun getPriceWithSign(): String {
        return "${currencySign}${price}"
    }

    override fun equals(other: Any?): Boolean {
        return other is Item && this.dbPrimaryId == other.dbPrimaryId &&
                other.name.equals(this.name) && (this.price - other.price) < 0.001 &&
                other.category.equals(this.category) && other.description.equals(this.description)
    }
}