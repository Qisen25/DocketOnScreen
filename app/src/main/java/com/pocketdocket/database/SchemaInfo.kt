package com.pocketdocket.database

import android.provider.BaseColumns

/**
 * Database schema to be used for SQlite
 */
object SchemaInfo {

    // Create table queries
    const val CREATE_MENUS_TABLE_QUERY = "CREATE TABLE ${Menus.TABLE_NAME} (" +
                                             "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                                             "${Menus.COLUMN_NAME} TEXT )"

    const val CREATE_ITEMS_TABLE_QUERY = "CREATE TABLE ${Items.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${Items.COLUMN_NAME} TEXT," +
            "${Items.COLUMN_PRICE} REAL," +
            "${Items.COLUMN_CATEGORY} TEXT," +
            "${Items.COLUMN_DESCRIPTION} TEXT," +
            "${Items.COLUMN_MENU_ID} INTEGER," +
            "FOREIGN KEY(${Items.COLUMN_MENU_ID}) REFERENCES ${Menus.TABLE_NAME}(_id) )"

    const val CREATE_EXTRAS_TABLE_QUERY = "CREATE TABLE ${Extras.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," +
            "${Extras.COLUMN_NAME} TEXT," +
            "${Extras.COLUMN_PRICE} REAL )"

    const val CREATE_ITEM_EXTRAS_TABLE_QUERY = "CREATE TABLE ${ItemExtras.TABLE_NAME} (" +
            "${ItemExtras.COLUMN_ITEM_ID} INTEGER," +
            "${ItemExtras.COLUMN_EXTRA_ID} INTEGER," +
            "PRIMARY KEY(${ItemExtras.COLUMN_ITEM_ID}, ${ItemExtras.COLUMN_EXTRA_ID})," +
            "FOREIGN KEY(${ItemExtras.COLUMN_ITEM_ID}) REFERENCES ${Items.TABLE_NAME}(_id)," +
            "FOREIGN KEY(${ItemExtras.COLUMN_EXTRA_ID}) REFERENCES ${Extras.TABLE_NAME}(_id) )"

    // Menu information. ATM only contains a name as it could be a restaurant name or version of a menu
    // Could be extended to contain more info such as restaurant phone number and other details
    object Menus : BaseColumns {
        const val TABLE_NAME = "MenusTable"
        const val COLUMN_NAME = "name"
    }

    // Any item belonging to a menu such as foods
    object Items : BaseColumns {
        const val TABLE_NAME = "ItemsTable"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRICE = "price"
        const val COLUMN_CATEGORY = "category"
        const val COLUMN_DESCRIPTION = "description"
        const val COLUMN_MENU_ID = "menuId"
    }

    // Additional things that could be added to items
    object Extras : BaseColumns {
        const val TABLE_NAME = "ExtrasTable"
        const val COLUMN_NAME = "name"
        const val COLUMN_PRICE = "price"
    }

    // Many to many relationship between items and extras since items can have many extra options and extra options can belong to many items
    object ItemExtras {
        const val TABLE_NAME = "ItemExtrasTable"
        const val COLUMN_ITEM_ID = "itemId"
        const val COLUMN_EXTRA_ID = "extraId"
    }
}