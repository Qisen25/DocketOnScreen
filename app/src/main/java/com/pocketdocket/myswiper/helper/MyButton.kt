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
            x = cWidth/2f - r.width()/2f - r.left.toFloat()
            y = cHeight/2f - r.height()/2f - r.bottom.toFloat()
            c.drawText(text, rectf.left + x, rectf.top + y, paint)
        }
        else {
            val d = ContextCompat.getDrawable(context!!, imageResId)
            val bitmap = drawableToBitmap(d)
            c.drawBitmap(bitmap, (rectf.left + rectf.right) / 2, (rectf.top + rectf.bottom) / 2, paint)
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