package com.docketonscreen.model

import android.os.Parcel
import android.os.Parcelable

/**
 * Class for storing catalogue/menu info
 */
data class Catalogue(var name: String?) : Parcelable {

    private var items: MutableList<Item> = mutableListOf<Item>()
    var dbPrimaryId: Int = 0

    constructor(parcel: Parcel) : this(parcel.readString()) {

    }

    fun addItem(item: Item) {
        items.add(item)
    }

    fun removeItem(item: Item) {
        items.remove(item)
    }

    fun removeItemAt(pos: Int) {
        items.removeAt(pos)
    }

    fun getItems(): MutableList<Item> {
        return this.items
    }

    fun setItems(list: MutableList<Item>) {
        this.items = list
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Catalogue> {
        override fun createFromParcel(parcel: Parcel): Catalogue {
            return Catalogue(parcel)
        }

        override fun newArray(size: Int): Array<Catalogue?> {
            return arrayOfNulls(size)
        }
    }
}