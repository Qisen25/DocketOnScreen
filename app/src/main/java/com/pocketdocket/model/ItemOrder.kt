package com.pocketdocket.model

/**
 * Class for storing details about an item listed in the cart
 * @param item: Item - The item that is ordered
 * @param amount: Int - The amount listed in the cart
 */
data class ItemOrder(val item: Item, var amount: Int = 1) {

    override fun equals(other: Any?): Boolean {
        return other is ItemOrder && other.item.equals(this.item) &&
                other.amount == this.amount
    }
}