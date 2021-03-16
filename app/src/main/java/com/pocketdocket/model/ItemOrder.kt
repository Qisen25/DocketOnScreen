package com.pocketdocket.model

/**
 * Class for storing details about an item listed in the cart
 * @param item: Item - The item that is ordered
 * @param amount: Int - The amount listed in the cart
 */
data class ItemOrder(val item: Item, var amount: Int = 1) {

    override fun equals(other: Any?): Boolean {
        // We don't necessarily have to check if amount is same since it always changes
        return other is ItemOrder && other.item.equals(this.item)
    }

    fun getCost(): Double {
        return item.price
    }
}