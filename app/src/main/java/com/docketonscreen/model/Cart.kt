package com.docketonscreen.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Cart that lists all the items added by the the user
 */
class Cart(var menuName: String = "") : Parcelable{
    private val cartList: MutableList<ItemOrder> = mutableListOf()
    var rate = 0.0
    var extraFeeType = 0
    var customerDetails: LinkedHashMap<String, String>
        = linkedMapOf(Pair(Cart.IDNAME, ""), Pair(Cart.PHONENO, ""), Pair(Cart.NUMOFPPL, ""),
            Pair(Cart.ADDRESS, ""), Pair(Cart.COMMENTS, ""))

    constructor(parcel: Parcel) : this(parcel.readString()!!) {
        rate = parcel.readDouble()
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

    /**
     * Get cost of items in cart without extra fees applied
     * @return subtotal (Real number)
     */
    fun getSubTotalCost(): Double {
        var total = 0.0

        for (item in cartList) {
            total += item.getCost()
        }

        return total
    }

    /**
     * Get the amount of surcharge or discount that could be applied
     * @return extra fee (Real number)
     */
    fun getExtraFee(): Double {
        val extra = getSubTotalCost() * rate
        return extra
    }

    /**
     * Get final cost with all extra fees applied
     * @return finalTotal (Real number)
     */
    fun getFinalTotal(): Double {
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

    /**
     * Get a string that shows the cost details
     * @return string describing subtotal, discount/surcharge and final total
     */
    fun finalTotalString(): String {
        if (extraFeeType == SURCHARGED) {
            return "Sub total: $${getSubTotalCost()}\nSurcharge: +$${getExtraFee()}\nTotal: $${"%.2f".format(getFinalTotal())}"
        }
        else if (extraFeeType == DISCOUNTED) {
            return "Sub total: $${getSubTotalCost()}\nDiscount: -$${getExtraFee()}\nTotal: $${"%.2f".format(getFinalTotal())}"
        }

        return "Total: $${getSubTotalCost()}"
    }

    fun isEmpty(): Boolean {
        return cartList.isEmpty()
    }

    fun clear() {
        cartList.clear()
    }

    /**
     * Clear cart if to start new order
     */
    fun reset() {
        clear()
        extraFeeType = 0
        rate = 0.0
        customerDetails = linkedMapOf(Pair(Cart.IDNAME, ""), Pair(Cart.PHONENO, ""), Pair(Cart.NUMOFPPL, ""),
                            Pair(Cart.ADDRESS, ""), Pair(Cart.COMMENTS, ""))
    }

    fun getItemOrderList(): MutableList<ItemOrder> {
        return cartList
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeList(cartList)
        dest?.writeString(menuName)
        dest?.writeMap(customerDetails)
    }

    companion object CREATOR : Parcelable.Creator<Cart> {
        const val NONE = 0
        const val DISCOUNTED = 1
        const val SURCHARGED = 2

        const val IDNAME = "idName"
        const val PHONENO = "phoneNo"
        const val NUMOFPPL = "numOfPpl"
        const val ADDRESS = "address"
        const val COMMENTS = "comments"

        override fun createFromParcel(parcel: Parcel): Cart {
            return Cart(parcel)
        }

        override fun newArray(size: Int): Array<Cart?> {
            return arrayOfNulls(size)
        }
    }
}