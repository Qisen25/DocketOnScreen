package com.docketonscreen.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class AppDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "PocketDocket.db"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // MENUS table
        db?.execSQL(SchemaInfo.CREATE_MENUS_TABLE_QUERY)
        // ITEMS table
        db?.execSQL(SchemaInfo.CREATE_ITEMS_TABLE_QUERY)
        // EXTRAS table
        db?.execSQL(SchemaInfo.CREATE_EXTRAS_TABLE_QUERY)
        // ITEMS has EXTRAS table
        db?.execSQL(SchemaInfo.CREATE_ITEM_EXTRAS_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}