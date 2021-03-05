package com.pocketdocket.database

import android.database.Cursor
import android.database.CursorWrapper
import com.pocketdocket.model.Catalogue

class CatalogueCursor(cursor: Cursor) : CursorWrapper(cursor) {

    fun getMenu() : Catalogue {
        val name = getString(getColumnIndex(SchemaInfo.Menus.COLUMN_NAME))
        val id = getInt(getColumnIndex("_id"))

        val menu = Catalogue(name)
        menu.id = id

        return menu
    }
}