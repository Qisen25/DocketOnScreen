package com.docketonscreen

import android.R.attr.src
import com.docketonscreen.model.Cart
import com.docketonscreen.model.Item
import com.docketonscreen.model.ItemOrder
import com.docketonscreen.print.PdfBuildHelper
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfName
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.PdfWriter
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.FileOutputStream

/**
 * Unit test to check if pdf builder is working as intended
 */
class PdfBuilderUnitTest {
    private lateinit var doc: Document
    private lateinit var pdfBuilder: PdfBuildHelper
    private lateinit var cart: Cart

    @Before
    fun setup() {
        doc = Document()
        doc.open()
        cart = Cart("Test")
        cart.addOrder(ItemOrder(Item("TestItem", 20.0, "Test category", "Testing 123"), 1))
        val fontname = this.javaClass.classLoader.getResource("assets/serif.otf").toString()
        pdfBuilder = PdfBuildHelper(cart, doc, fontname)
    }

    // Test to make sure pdf builder creates a pdf containing items and cost of order
    @Test
    fun testDocBuild() {
        pdfBuilder.build()
        doc.close()
        assertEquals("TestItem", doc.accessibleAttributes[PdfName("Item=TestItem")].toString())
        assertEquals("20.0", doc.accessibleAttributes[PdfName("ItemOrderPrice")].toString())
        assertEquals("1", doc.accessibleAttributes[PdfName("ItemCount")].toString())
        assertEquals(cart.finalTotalString(), doc.accessibleAttributes[PdfName("FinalCostSummary")].toString())
    }
}