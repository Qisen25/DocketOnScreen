package com.pocketdocket.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.pocketdocket.database.AppDatabaseHelper
import com.pocketdocket.database.CatalogueCursor
import com.pocketdocket.database.SchemaInfo

/**
 * Singleton class for storing and caching existing menu items at run time
 */
class CatalogueRepository {
    companion object {
        var menus: MutableList<Catalogue> = mutableListOf<Catalogue>()
        private lateinit var db: SQLiteDatabase

        /**
         * start up database
         */
        fun load(context: Context) {

            db = AppDatabaseHelper(context).writableDatabase
            loadMenus(db)
        }

        /**
         * load menus
         */
        fun loadMenus(db: SQLiteDatabase) {

            val menuCurs = CatalogueCursor(db.query(SchemaInfo.Menus.TABLE_NAME, null, null, null, null, null, null))

            try{
                menuCurs.moveToFirst()
                while (!menuCurs.isAfterLast) {
                    menus.add(menuCurs.getMenu())
                    menuCurs.moveToNext()
                }
            }
            finally {
                menuCurs.close()
            }
        }

        /**
         * Add menu to list and database
         */
        fun addMenu(cat: Catalogue) {
            menus.add(cat)

            val cv = ContentValues()
            cv.put(SchemaInfo.Menus.COLUMN_NAME, cat.name)
            db.insert(SchemaInfo.Menus.TABLE_NAME, null, cv)
        }
    }
}