package com.docketonscreen.print

import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.draw.LineSeparator
import com.itextpdf.text.pdf.draw.VerticalPositionMark
import com.docketonscreen.model.Cart
import com.itextpdf.text.pdf.PdfName
import com.itextpdf.text.pdf.PdfString

/**
 * Class that helps build order into a pdf document
 * Note: I assume any print could take this documents but I'm not sure about POS thermal printers
 */
class PdfBuildHelper(val cart: Cart, val document: Document, private val filename: String) {

    /**
     * Function to build/style document
     */
    fun build() {

        document.pageSize = PageSize.A7
        document.addCreationDate()
        document.addAuthor("Pocket Docket")

        // Add Title
        val baseFont = BaseFont.createFont(filename, "UTF-8", BaseFont.EMBEDDED)
        val titleFont = Font(baseFont, 20.0f, Font.BOLD, BaseColor.BLACK)
        addItem("${cart.menuName} Docket", Element.ALIGN_CENTER, titleFont)

        addLineSeparator(document)

        // Add cart items
        for (i in cart.getItemOrderList()) {
            addLeftAndRightText("${i.amount}x ${i.item.name}", "$${"%.2f".format(i.getCost())}")
            // Keep track of item details
            document.setAccessibleAttribute(PdfName("Item=${i.item.name}"), PdfString(i.item.name))
            document.setAccessibleAttribute(PdfName("ItemOrderPrice"), PdfString(i.getCost().toString()))
        }

        addLineSeparator(document)

        val standoutFont = Font(baseFont, 13.0f, Font.BOLD, BaseColor.BLACK)

        // Add comments
        addItem("Comments: ${cart.customerDetails[Cart.COMMENTS]}", Element.ALIGN_MIDDLE, standoutFont)

        addLineSeparator(document)


        // Add item count and cost summary
        val cartItemCount = cart.getItemCount()
        addItem("${cart.getItemCount()} Items", Element.ALIGN_RIGHT, standoutFont)
        addItem(cart.finalTotalString(), Element.ALIGN_RIGHT, standoutFont)

        // Keep track of cost summary details
        document.setAccessibleAttribute(PdfName("ItemCount"), PdfString(cartItemCount.toString()))
        document.setAccessibleAttribute(PdfName("FinalCostSummary"), PdfString(cart.finalTotalString()))

        addLineSeparator(document)

        val customerTitle = Font(baseFont, 16.0f, Font.BOLD, BaseColor.BLACK)
        addItem("Customer Details", Element.ALIGN_CENTER, customerTitle)

        addLineSeparator(document)

        // exclude comment details since included above
        for (detail in cart.customerDetails) {
            if (detail.value.isNotEmpty()) {
                println(detail.value)
                if (detail.key == Cart.NUMOFPPL) {
                    addItem("# of Seats: ${detail.value}", Element.ALIGN_LEFT, standoutFont)
                }
                else {
                    addItem(detail.value, Element.ALIGN_LEFT, standoutFont)
                }
            }
        }
    }

    /**
     * Function to add Text to document
     */
    fun addItem(text: String, align: Int, style: Font) {
        val chunk = Chunk(text, style)
        val paragraph = Paragraph(chunk)
        paragraph.alignment = align
        document.add(paragraph)
    }

    /**
     * Function to add new line between sections of document
     */
    fun addLineSeparator(document: Document) {
        val separator = LineSeparator()
        separator.lineColor = BaseColor(0, 0, 0, 0)
        addLineSpace(document)
        document.add(Chunk(separator))
        addLineSpace(document)
    }

    /**
     * Function to position two corresponding strings left and right in the document
     */
    fun addLeftAndRightText(textLeft: String, textRight: String) {
        val chunkLeft = Chunk(textLeft)
        val chunkRight = Chunk(textRight)
        val p = Paragraph(chunkLeft)
        p.add(Chunk(VerticalPositionMark()))
        p.add(chunkRight)
        document.add(p)
    }

    // Add spacing
    private fun addLineSpace(document: Document) {
        document.add(Paragraph(""))
    }
}