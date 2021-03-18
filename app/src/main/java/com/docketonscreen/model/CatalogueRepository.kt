package com.docketonscreen.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.docketonscreen.database.AppDatabaseHelper
import com.docketonscreen.database.CatalogueCursor
import com.docketonscreen.database.ItemCursor
import com.docketonscreen.database.SchemaInfo

/**
 * Singleton class for storing and caching existing menu items at run time
 */
class CatalogueRepository {
    companion object {
        var menus: MutableList<Catalogue> = mutableListOf<Catalogue>()
        private lateinit var db: SQLiteDatabase
        // Keep track of menu index

        /**
         * start up database
         */
        fun load(context: Context) {

            db = AppDatabaseHelper(context).writableDatabase
            loadMenus()
        }

        /**
         * load menus
         */
        private fun loadMenus() {

            val menuCurs = CatalogueCursor(db.query(SchemaInfo.Menus.TABLE_NAME, null, null, null, null, null, null))

            try{
                menuCurs.moveToFirst()
                while (!menuCurs.isAfterLast) {
                    val menu = menuCurs.getMenu()
                    val itemList = loadItems(menu.dbPrimaryId)
                    menu.setItems(itemList)
                    menus.add(menu)
                    menuCurs.moveToNext()
                }
            }
            finally {
                menuCurs.close()
            }
        }

        private fun loadItems(menuId: Int): MutableList<Item> {
            val cols = arrayOf("name", "price", "category", "description")
            val itemCurs = ItemCursor(db.query(SchemaInfo.Items.TABLE_NAME, null, "menuId = $menuId", null, null, null, null))

            val itemList = mutableListOf<Item>()

            try{
                itemCurs.moveToFirst()
                while (!itemCurs.isAfterLast) {
                    itemList.add(itemCurs.getItem())
                    itemCurs.moveToNext()
                }
            }
            finally {
                itemCurs.close()
            }

            return itemList
        }

        /**
         * Add menu to list and database
         */
        fun addMenu(cat: Catalogue) {
            // Primary key is auto incremented in this step but it also matches above step
            val cv = ContentValues()
            cv.put(SchemaInfo.Menus.COLUMN_NAME, cat.name)
            val rowId = db.insert(SchemaInfo.Menus.TABLE_NAME, null, cv)

            cat.dbPrimaryId = rowId.toInt()
            menus.add(cat)
        }

        fun addItem(menu: Catalogue, item: Item) {

            item.menuId = menu.dbPrimaryId

            val cv = ContentValues()
            cv.put(SchemaInfo.Items.COLUMN_NAME, item.name)
            cv.put(SchemaInfo.Items.COLUMN_PRICE, item.price)
            cv.put(SchemaInfo.Items.COLUMN_CATEGORY, item.category)
            cv.put(SchemaInfo.Items.COLUMN_DESCRIPTION, item.description)
            cv.put(SchemaInfo.Items.COLUMN_MENU_ID, item.menuId)
            val rowId = db.insert(SchemaInfo.Items.TABLE_NAME, null, cv)

            // Db insert returns the row Id which is also the primary key
            // It also doesn't reset to 0 when leaving the app and coming back on to adding item
            // Recent index is based off additions made previously (Persitent logging of ids I guess)
            item.dbPrimaryId = rowId.toInt()

            menu.getItems().add(item)
        }

        fun removeItem(menu: Catalogue, item: Item): Int {
            val index = menu.getItems().indexOf(item)

            val whereArgs = arrayOf(item.dbPrimaryId.toString())
            db.delete(SchemaInfo.Items.TABLE_NAME, "_id = ?", whereArgs)

            // remove from menu's item list
            menu.removeItem(item)

            return index
        }

        fun removeMenu(menu: Catalogue) {
            val id = menu.dbPrimaryId
            val whereArgs = arrayOf(id.toString())
            menus.remove(menu)

            // Delete Items associated with current menu
            db.delete(SchemaInfo.Items.TABLE_NAME, "menuId = ?", whereArgs)
            // Finally delete menu
            db.delete(SchemaInfo.Menus.TABLE_NAME, "_id = ?", whereArgs)
        }

        fun updateItem(item: Item) {

            val cv = ContentValues()
            cv.put(SchemaInfo.Items.COLUMN_NAME, item.name)
            cv.put(SchemaInfo.Items.COLUMN_PRICE, item.price)
            cv.put(SchemaInfo.Items.COLUMN_CATEGORY, item.category)
            cv.put(SchemaInfo.Items.COLUMN_DESCRIPTION, item.description)

            val whereArgs = arrayOf(item.dbPrimaryId.toString())
            db.update(SchemaInfo.Items.TABLE_NAME, cv,"_id = ? ", whereArgs)
        }
    }
}