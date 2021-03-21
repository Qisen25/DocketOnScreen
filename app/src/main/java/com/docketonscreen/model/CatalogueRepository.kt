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
        lateinit var menus: MutableList<Catalogue>
        private lateinit var db: SQLiteDatabase
        // Keep track of menu index

        /**
         * start up database
         */
        fun load(context: Context) {
            menus = mutableListOf<Catalogue>()
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

        /**
         * Load items from database
         * @param menuId - Integer primary key ID
         */
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
        fun addMenu(cat: Catalogue): Long {
            // Primary key is auto incremented in this step but it also matches above step
            val cv = ContentValues()
            cv.put(SchemaInfo.Menus.COLUMN_NAME, cat.name)
            val rowId = db.insert(SchemaInfo.Menus.TABLE_NAME, null, cv)

            cat.dbPrimaryId = rowId.toInt()
            menus.add(cat)

            return rowId
        }

        /**
         * Add an item to a catalogue
         * @param menu - A catalogue to manipulate
         * @param item - An item to add to the menu param
         */
        fun addItem(menu: Catalogue, item: Item): Long {

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

            return rowId
        }

        /**
         * Remove item from catalogue and database
         * @param menu - A catalogue that contains an item
         * @param item - An Item associated with supplied menu param
         */
        fun removeItem(menu: Catalogue, item: Item): Int {
            val index = menu.getItems().indexOf(item)

            val whereArgs = arrayOf(item.dbPrimaryId.toString())
            db.delete(SchemaInfo.Items.TABLE_NAME, "_id = ?", whereArgs)

            // remove from menu's item list
            menu.removeItem(item)

            return index
        }

        /**
         * Remove catalogue from the list of menus and database
         * @param menu - A catalogue to remove from list and database
         */
        fun removeMenu(menu: Catalogue): Int {
            val id = menu.dbPrimaryId
            val whereArgs = arrayOf(id.toString())
            menus.remove(menu)

            // Delete Items associated with current menu
            db.delete(SchemaInfo.Items.TABLE_NAME, "menuId = ?", whereArgs)
            // Finally delete menu
            db.delete(SchemaInfo.Menus.TABLE_NAME, "_id = ?", whereArgs)

            return id
        }

        /**
         * update an item in database
         * @param item - An Item to update
         */
        fun updateMenu(catalogue: Catalogue): Int {

            val cv = ContentValues()
            cv.put(SchemaInfo.Menus.COLUMN_NAME, catalogue.name)

            val whereArgs = arrayOf(catalogue.dbPrimaryId.toString())
            val rowsAffected = db.update(SchemaInfo.Menus.TABLE_NAME, cv,"_id = ? ", whereArgs)

            return rowsAffected
        }

        /**
         * update an item in database
         * @param item - An Item to update
         */
        fun updateItem(item: Item): Int {

            val cv = ContentValues()
            cv.put(SchemaInfo.Items.COLUMN_NAME, item.name)
            cv.put(SchemaInfo.Items.COLUMN_PRICE, item.price)
            cv.put(SchemaInfo.Items.COLUMN_CATEGORY, item.category)
            cv.put(SchemaInfo.Items.COLUMN_DESCRIPTION, item.description)

            val whereArgs = arrayOf(item.dbPrimaryId.toString())
            val rowsAffected = db.update(SchemaInfo.Items.TABLE_NAME, cv,"_id = ? ", whereArgs)

            return rowsAffected
        }
    }
}