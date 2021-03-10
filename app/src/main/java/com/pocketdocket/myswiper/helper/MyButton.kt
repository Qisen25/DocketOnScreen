package com.pocketdocket.myswiper.helper

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.pocketdocket.myswiper.listeners.MyButtonClickListener

class MyButton(private val context: Context?, private val text: String,
               private val textSize: Int,
               private val imageResId: Int,
               private val color: Int,
               private val listener: MyButtonClickListener) {

    private var pos: Int = 0
    private lateinit var clickRegion: RectF
    private lateinit var resources: Resources

    init{
        resources = context!!.resources
    }

    fun onClick(x: Float, y: Float): Boolean {
        if(clickRegion!!.contains(x, y))
        {
            listener.onClick(pos)
            return true
        }

        return false
    }

    fun onDraw(c: Canvas, rectf: RectF, pos: Int) {
        val paint = Paint()

        paint.color = color
        c.drawRect(rectf, paint)

        // For text
        paint.color = Color.WHITE
        paint.textSize = textSize.toFloat()

        val r = Rect()
        val cHeight = rectf.height()
        val cWidth = rectf.width()
        paint.textAlign = Paint.Align.LEFT
        paint.getTextBounds(text, 0, text.length, r)
        var x = 0f
        var y = 0f

        if(imageResId == 0) {
            // Align center text, since alig.LEFT we only need a small margin left to make it center
            x = (cWidth - r.width()) / 2 - r.left.toFloat()
            // Get middle point height base on height then subtract the bottom point of text to
            // get accurate center alignment
            y = (cHeight + r.height()) / 2 - r.bottom.toFloat()
            c.drawText(text, rectf.left + x, rectf.top + y, paint)
//            println("cW $cWidth cH $cHeight rW ${r.width()} rH ${r.height()}")
//            println("rLeft ${r.left.toFloat()} rBot ${r.bottom.toFloat()}")
//            println("X $x Y $y")
        }
        else {
            val d = ContextCompat.getDrawable(context!!, imageResId)
            val bitmap = drawableToBitmap(d)
            // Get total of rectF width and subtract by bitmap width to ensure img is centered
            // and divide by 2 to get actual position
            val centerX = ((rectf.left + rectf.right) - bitmap.width) / 2
            // similar to above
            val centerY = ((rectf.top + rectf.bottom) - bitmap.height) / 2
            c.drawBitmap(bitmap, centerX, centerY, paint)
        }

        clickRegion = rectf
        this.pos = pos
    }

    private fun drawableToBitmap(draw: Drawable?): Bitmap {
        if (draw is BitmapDrawable) return draw.bitmap
        val bitmap = Bitmap.createBitmap(draw!!.intrinsicWidth, draw!!.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        draw.setBounds(0, 0, canvas.width, canvas.height)
        draw.draw(canvas)

        return bitmap
    }
}