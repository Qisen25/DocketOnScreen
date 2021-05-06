package com.docketonscreen.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Class for storing details about an item listed in the cart
 * @param item: Item - The item that is ordered
 * @param amount: Int - The amount listed in the cart
 */
data class ItemOrder(val item: Item, var amount: Int = 1) : Parcelable{

    constructor(parcel: Parcel) : this(
            parcel.readParcelable<Item>(Item::class.java.classLoader)!!,
            parcel.readInt()) {
    }

    override fun equals(other: Any?): Boolean {
        // We don't necessarily have to check if amount is same since it always changes
        return other is ItemOrder && other.item.equals(this.item)
    }

    /**
     * Get total cost for this item order
     * @return: Price (Real number)
     */
    fun getCost(): Double {
        return item.price * amount
    }

    /**
     * Get cost with dollar sign
     * @return: Price (String)
     */
    fun getCostWithDollarSign(): String {
        return "$${item.price * amount}"
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(amount)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ItemOrder> {
        override fun createFromParcel(parcel: Parcel): ItemOrder {
            return ItemOrder(parcel)
        }

        override fun newArray(size: Int): Array<ItemOrder?> {
            return arrayOfNulls(size)
        }
    }
}