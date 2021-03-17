package com.pocketdocket.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Data class for holding info about an item. Parcelable to allow reliability when moving between
 * fragments/activities
 */
data class Item(var name: String, var price: Double, var category: String, var description: String) : Parcelable {

    var dbPrimaryId = 0
    var menuId = 0
    var currencySign = "$"

    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readDouble()!!,
            parcel.readString()!!,
            parcel.readString()!!) {
        dbPrimaryId = parcel.readInt()
        menuId = parcel.readInt()
        currencySign = parcel.readString()!!
    }

    fun getPriceWithSign(): String {
        return "${currencySign}${price}"
    }

    override fun equals(other: Any?): Boolean {
        return other is Item && this.dbPrimaryId == other.dbPrimaryId &&
                other.name.equals(this.name) && (this.price - other.price) < 0.001 &&
                other.category.equals(this.category) && other.description.equals(this.description)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeString(category)
        parcel.writeString(description)
        parcel.writeInt(dbPrimaryId)
        parcel.writeInt(menuId)
        parcel.writeString(currencySign)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }
}