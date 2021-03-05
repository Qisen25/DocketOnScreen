package com.pocketdocket.database

import android.database.Cursor
import android.database.CursorWrapper
import com.pocketdocket.model.Item

class ItemCursor(cursor: Cursor) : CursorWrapper(cursor) {

    fun getItem(): Item {
        val name = getString(getColumnIndex(SchemaInfo.Items.COLUMN_NAME))
        val price = getDouble(getColumnIndex(SchemaInfo.Items.COLUMN_PRICE))
        val category = getString(getColumnIndex(SchemaInfo.Items.COLUMN_CATEGORY))
        val desc = getString(getColumnIndex(SchemaInfo.Items.COLUMN_DESCRIPTION))

        val item = Item(name, price, category, desc)
        item.id = getInt(getColumnIndex("_id"))
        item.menuId = getInt(getColumnIndex(SchemaInfo.Items.COLUMN_MENU_ID))

        return item
    }
}