package com.pocketdocket.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Cart that lists all the items added by the the user
 */
class Cart() : Parcelable{
    private val cartList: MutableList<ItemOrder> = mutableListOf()
    var rate = 0.0

    constructor(parcel: Parcel) : this() {
    }

    /**
     * Add an an item to the cart
     * (If an item already exists in list just find it and update it
     */
    fun addOrder(order: ItemOrder) {
        if (cartList.contains(order)) {
            // increment item amount if item order exists
            cartList[cartList.indexOf(order)].amount++
        }
        else {
            cartList.add(order)
        }
    }

    fun removeOrder(order: ItemOrder) {
        cartList.remove(order)
    }

    fun count(): Int {
        return cartList.count()
    }

    fun get(index: Int): ItemOrder {
        return cartList[index]
    }

    /**
     * Get total item count in this cart
     * This included duplication of items
     */
    fun getItemCount(): Int {
        var totalCount = 0
        for (item in cartList) {
            totalCount += item.amount
        }

        return totalCount
    }

    fun getSubTotalCost(): Double {
        var total = 0.0

        for (item in cartList) {
            total += item.getCost()
        }

        return total
    }

    fun getExtraFee(): Double {
        val extra = getSubTotalCost() * rate
        return extra
    }

    fun getFinalTotal(extraFeeType: Int = 0): Double {
        val subTotal = getSubTotalCost()
        var total = subTotal
        if (extraFeeType == SURCHARGED && rate != 0.0) {
            total = subTotal + getExtraFee()
        }
        else if (extraFeeType == DISCOUNTED && rate != 0.0) {
            total = subTotal - getExtraFee()
        }

        return total
    }

    fun isEmpty(): Boolean {
        return cartList.isEmpty()
    }

    fun clear() {
        cartList.clear()
    }

    fun getItemOrderList(): MutableList<ItemOrder> {
        return cartList
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeList(cartList)
    }

    companion object CREATOR : Parcelable.Creator<Cart> {
        var DISCOUNTED = 1
        var SURCHARGED = 2

        override fun createFromParcel(parcel: Parcel): Cart {
            return Cart(parcel)
        }

        override fun newArray(size: Int): Array<Cart?> {
            return arrayOfNulls(size)
        }
    }
}