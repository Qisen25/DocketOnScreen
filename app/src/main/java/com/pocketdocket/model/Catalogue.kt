package com.pocketdocket.model

import android.os.Parcel
import android.os.Parcelable

data class Catalogue(val name: String?) : Parcelable {

    private var items: MutableList<Item> = mutableListOf<Item>()
    var id: Int = 0

//    init {
//        items.add(Item("poop", 100.0, "pop", "My dinner yummeee"))
//    }

    constructor(parcel: Parcel) : this(parcel.readString()) {

    }

    fun addItems(item: Item) {
        items.add(item)
    }

    fun removeItem(item: Item) {
        items.remove(item)
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