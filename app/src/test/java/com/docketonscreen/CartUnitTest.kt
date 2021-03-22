package com.docketonscreen

import com.docketonscreen.model.Cart
import com.docketonscreen.model.Item
import com.docketonscreen.model.ItemOrder
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before

/**
 * Unit test cart
 */
class CartUnitTest {

    private lateinit var cart: Cart

    @Before
    fun setup() {
        cart = Cart("Test")
    }

    @After
    fun tearDown() {
        cart.reset()
    }

    @Test
    fun addOrder() {
        val order = ItemOrder(Item("Test", 12.0, "Test Category", "This is a test"), 2)
        cart.addOrder(order)
        assertTrue("Cart should not be empty", !cart.isEmpty())
    }

    @Test
    fun removeOrder() {
        val order = ItemOrder(Item("Test", 12.0, "Test Category", "This is a test"), 2)
        cart.removeOrder(order)
        assertTrue("Cart should be empty", cart.isEmpty())
    }

    @Test
    fun orderItemCount() {
        val order = ItemOrder(Item("Test", 12.0, "Test Category", "This is a test"), 2)
        cart.addOrder(order)
        assertEquals("Item count in the order should be 2", 2, cart.getItemCount())
    }

    // Subtotal cost test
    @Test
    fun orderSubtotal() {
        val order = ItemOrder(Item("Test", 12.0, "Test Category", "This is a test"), 2)
        cart.addOrder(order)
        assertEquals("Order subtotal should be 24", 24.0, cart.getSubTotalCost(), 0.01)
    }

    @Test
    fun setRate() {
        cart.rate = 0.5
        assertEquals("Rate should be 0.5", 0.5, cart.rate, 0.01)
    }

    @Test
    fun setDiscounted() {
        cart.extraFeeType = Cart.DISCOUNTED
        assertEquals("Rate should be 0.5", Cart.DISCOUNTED, cart.extraFeeType)
    }

    @Test
    fun setSurcharged() {
        cart.extraFeeType = Cart.SURCHARGED
        assertEquals("Rate should be 0.5", Cart.SURCHARGED, cart.extraFeeType)
    }

    // No extra fee applied final cost test
    @Test
    fun orderFinalTotal() {
        val order = ItemOrder(Item("Test", 12.0, "Test Category", "This is a test"), 2)
        cart.addOrder(order)
        assertEquals("Order final total without extra fees should be 24.0", 24.0, cart.getFinalTotal(), 0.01)
    }

    // Discounted final cost test
    @Test
    fun orderFinalTotalDiscounted() {
        val order = ItemOrder(Item("Test", 12.0, "Test Category", "This is a test"), 2)
        cart.addOrder(order)
        cart.rate = 0.5
        cart.extraFeeType = Cart.DISCOUNTED
        assertEquals("Order final with discount should be 12.0", 12.0, cart.getFinalTotal(), 0.01)
    }

    // Surcharged final cost test
    @Test
    fun orderFinalTotalSurcharged() {
        val order = ItemOrder(Item("Test", 12.0, "Test Category", "This is a test"), 2)
        cart.addOrder(order)
        cart.rate = 0.5
        cart.extraFeeType = Cart.SURCHARGED
        assertEquals("Order final with discount should be 36.0", 36.0, cart.getFinalTotal(), 0.01)
    }
}