package com.pocketdocket.model

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.pocketdocket.database.AppDatabaseHelper
import com.pocketdocket.database.CatalogueCursor
import com.pocketdocket.database.ItemCursor
import com.pocketdocket.database.SchemaInfo

/**
 * Singleton class for storing and caching existing menu items at run time
 */
class CatalogueRepository {
    companion object {
        var menus: MutableList<Catalogue> = mutableListOf<Catalogue>()
        private lateinit var db: SQLiteDatabase
        private var menuCount = 0

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
                    val itemList = loadItems(menu.id)
                    menu.setItems(itemList)
                    menus.add(menu)
                    menuCurs.moveToNext()
                }

                menuCount = menus.count()
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
            // Set item index, sqlite auto increments and follows this pattern
            // eg. start at index 1 if list count is 0 to follow sql convention where primary key integers start 1 by default
            menuCount++
            cat.id = menuCount
            menus.add(cat)

            // Primary key is auto incremented in this step but it also matches above step
            val cv = ContentValues()
            cv.put(SchemaInfo.Menus.COLUMN_NAME, cat.name)
            db.insert(SchemaInfo.Menus.TABLE_NAME, null, cv)
        }

        fun addItem(menu: Catalogue, item: Item) {
            item.menuId = menu.id
            menu.getItems().add(item)

            val cv = ContentValues()
            cv.put(SchemaInfo.Items.COLUMN_NAME, item.name)
            cv.put(SchemaInfo.Items.COLUMN_PRICE, item.price)
            cv.put(SchemaInfo.Items.COLUMN_CATEGORY, item.category)
            cv.put(SchemaInfo.Items.COLUMN_DESCRIPTION, item.description)
            cv.put(SchemaInfo.Items.COLUMN_MENU_ID, item.menuId)
            db.insert(SchemaInfo.Items.TABLE_NAME, null, cv)
        }
    }
}