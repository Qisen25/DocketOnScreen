package com.docketonscreen.database

import android.database.Cursor
import android.database.CursorWrapper
import com.docketonscreen.model.Catalogue

class CatalogueCursor(cursor: Cursor) : CursorWrapper(cursor) {

    fun getMenu() : Catalogue {
        val name = getString(getColumnIndex(SchemaInfo.Menus.COLUMN_NAME))
        val id = getInt(getColumnIndex("_id"))

        val menu = Catalogue(name)
        menu.dbPrimaryId = id

        return menu
    }
}