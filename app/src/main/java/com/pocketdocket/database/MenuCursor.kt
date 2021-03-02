package com.pocketdocket.database

import android.database.Cursor
import android.database.CursorWrapper
import com.pocketdocket.model.Catalogue

class MenuCursor(cursor: Cursor) : CursorWrapper(cursor) {

    fun getMenu() : Catalogue {
        val name = getString(getColumnIndex(SchemaInfo.Menus.COLUMN_NAME))

        return Catalogue(name)
    }
}