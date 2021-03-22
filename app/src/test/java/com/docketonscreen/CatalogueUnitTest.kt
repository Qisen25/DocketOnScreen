package com.docketonscreen

import com.docketonscreen.model.Catalogue
import com.docketonscreen.model.Item
import org.junit.After
import org.junit.Test

import org.junit.Assert.assertTrue
import org.junit.Before

/**
 * Unit test Catalogue data class
 */
class CatalogueUnitTest {

    private lateinit var catalogue: Catalogue

    @Before
    fun setup() {
        catalogue = Catalogue("TestCatalogue")
    }

    @After
    fun tearDown() {
        catalogue.getItems().clear()
    }

    @Test
    fun addItemIntoCatalogue() {
        catalogue.addItem(Item("TestItem", 12.0, "Test Category", "Test 123"))
        assertTrue("Catalogues item list should not be empty", catalogue.getItems().isNotEmpty())
    }

    @Test
    fun removeItemFromCatalogue() {
        val item = Item("TestItem", 12.0, "Test Category", "Test 123")
        catalogue.addItem(item)
        catalogue.removeItem(item)
        assertTrue("Remove catalogues item list should be empty", catalogue.getItems().isEmpty())
    }

    @Test
    fun removeItemAtIndex() {
        val item = Item("TestItem", 12.0, "Test Category", "Test 123")
        catalogue.addItem(item)
        catalogue.removeItemAt(0)
        assertTrue("Remove catalogues item by index list should be empty", catalogue.getItems().isEmpty())
    }
}