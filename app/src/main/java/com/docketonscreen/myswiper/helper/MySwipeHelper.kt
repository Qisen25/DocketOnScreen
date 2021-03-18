package com.docketonscreen.myswiper.helper

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

abstract class MySwipeHelper(context: Context?, private val recycView: RecyclerView,
                    internal var buttonWidth: Int) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private lateinit var buttonList: MutableList<MyButton>
    lateinit var gestureDetector: GestureDetector
    var swipePos = -1
    var swipeThreshold = 0.5f
    lateinit var buttonBuffer: MutableMap<Int, MutableList<MyButton>>
    lateinit var removeQueue: LinkedList<Int>

    private val gestureListener = object: GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent?): Boolean {
            for (button in buttonList) {
                if (button.onClick(e!!.x, e!!.y)) {
                    break
                }
            }

            return true
        }
    }

    private val onTouchLister = View.OnTouchListener{_, motionEvent ->
        if (swipePos < 0) return@OnTouchListener false
        val point = Point(motionEvent.rawX.toInt(), motionEvent.rawY.toInt())
        val swipeViewHolder = recycView.findViewHolderForAdapterPosition(swipePos)
        val swipedItem = swipeViewHolder!!.itemView
        val rect = Rect()
        swipedItem.getGlobalVisibleRect(rect)

        if (motionEvent.action == MotionEvent.ACTION_DOWN ||
                motionEvent.action == MotionEvent.ACTION_MOVE ||
                motionEvent.action == MotionEvent.ACTION_UP) {

            if(rect.top < point.y && rect.bottom > point.y) {
                gestureDetector.onTouchEvent(motionEvent)
            }
            else {
                removeQueue.add(swipePos)
                swipePos = -1
                recoverSwipeItem()
            }
        }

        false
    }

    @Synchronized
    private fun recoverSwipeItem() {
        while (!removeQueue.isEmpty()) {
            val pos = removeQueue.poll().toInt()

            if (pos > -1) {
                recycView.adapter!!.notifyItemChanged(pos)
            }
        }
    }

    init {
        this.buttonList = ArrayList()
        this.gestureDetector = GestureDetector(context, gestureListener)
        this.recycView.setOnTouchListener(onTouchLister)
        this.buttonBuffer = HashMap()
        this.removeQueue = IntLinkedList()

        attachSwipe()
    }

    private fun attachSwipe() {
        val itemTouchHelper = ItemTouchHelper(this)
        itemTouchHelper.attachToRecyclerView(recycView)
    }

    class IntLinkedList : LinkedList<Int>() {
        override fun contains(element: Int): Boolean {
            return false
        }

        override fun lastIndexOf(element: Int): Int {
            return element
        }

        override fun remove(element: Int): Boolean {
            return false
        }

        override fun indexOf(element: Int): Int {
            return element
        }

        override fun add(element: Int): Boolean {
            return if (contains(element))
                false
            else super.add(element)
        }
    }

    abstract fun instantiateMyButton(viewHolder: RecyclerView.ViewHolder, buffer: MutableList<MyButton>)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition

        if (swipePos != pos) {
            removeQueue.add(swipePos)
        }
        swipePos = pos

        if (buttonBuffer.containsKey(swipePos)) {
            buttonList = buttonBuffer[swipePos]!!
        }
        else {
            buttonList.clear()
        }
        buttonBuffer.clear()
        swipeThreshold = 0.5f * buttonList!!.size.toFloat()

        recoverSwipeItem()
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        val pos = viewHolder.adapterPosition
        var translationX = dX
        var itemView = viewHolder.itemView

        if (pos < 0) {
            swipePos = pos
            return
        }
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                var buffer: MutableList<MyButton> = ArrayList()
                if(!buttonBuffer.containsKey(pos)) {
                    instantiateMyButton(viewHolder, buffer)
                    // reverse order since drawing of swipe buttons will be right to left
                    // thus we try to show order buttons ordered in the list left to right
                    buffer.reverse()
                    buttonBuffer[pos] = buffer
                }
                else {
                    buffer = buttonBuffer[pos]!!
                }
                translationX = dX * buffer.size.toFloat() * buttonWidth.toFloat() / itemView.width
                drawButton(c, itemView, buffer, pos, translationX)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, translationX, dY, actionState, isCurrentlyActive)
    }

    private fun drawButton(c: Canvas, itemView: View, buffer: MutableList<MyButton>, pos: Int, translationX: Float) {
        var right = itemView.right.toFloat()
        var dButtonWidth = -1 * translationX / buffer.size

        for (button in buffer) {
            val left = right - dButtonWidth
            button.onDraw(c, RectF(left, itemView.top.toFloat(), right, itemView.bottom.toFloat()), pos)
            right = left
        }
    }
}


