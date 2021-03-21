package com.docketonscreen.dataInstrumentTests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.docketonscreen.model.Catalogue
import com.docketonscreen.model.CatalogueRepository
import com.docketonscreen.model.Item

import org.junit.runner.RunWith

import org.junit.Before
import org.junit.Test

/**
 * Instrumented test for database operations
 */
@RunWith(AndroidJUnit4::class)
class CatalogueRepositoryTest {

    private lateinit var item: Item
    private lateinit var catalogue: Catalogue

    @Before
    fun setup() {
        item = Item("TestItem", 10.0, "Test", "This is a test")
        catalogue = Catalogue("Test123")
        CatalogueRepository.load(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test
    fun insertCatalogue() {
        val row = CatalogueRepository.addMenu(catalogue)
        assert(row != (-1).toLong())
        { "Insert catalogue row should should not be -1 otherwise error has occurred" }
    }

    @Test
    fun insertItem() {
        val row = CatalogueRepository.addItem(catalogue, item)
        assert(row != (-1).toLong())
        { "Insert item row should should not be -1 otherwise error has occurred" }
    }

    @Test
    fun removeMenu() {
        val row = CatalogueRepository.removeMenu(catalogue)
        assert(row != -1)
        { "Remove item row should should not be -1 otherwise error has occurred" }
    }

    @Test
    fun removeItem() {
        // Make sure catalogue are inserted into database
        CatalogueRepository.addMenu(catalogue)
        CatalogueRepository.addItem(catalogue, item)
        val row = CatalogueRepository.removeItem(catalogue, item)
        assert(row != -1)
        { "Remove item row should should not be -1 otherwise error has occurred" }
    }

    @Test
    fun updateCatalogue() {
        // Make sure catalogue are inserted into database
        CatalogueRepository.addMenu(catalogue)
        catalogue.name = "UpdatedName"
        val rowsAffected = CatalogueRepository.updateMenu(catalogue)
        assert(rowsAffected != -1)
        { "Update catalogue rows affected should should not be 0 otherwise error has occurred" }
    }

    @Test
    fun updateItem() {
        // Make sure catalogue and item are inserted into database
        CatalogueRepository.addMenu(catalogue)
        CatalogueRepository.addItem(catalogue, item)
        item.name = "UpdatedName"
        val rowsAffected = CatalogueRepository.updateItem(item)
        assert(rowsAffected != -1)
        { "Update item rows affected should should not be 0 otherwise error has occurred" }
    }
}