package com.pocketdocket.behaviour

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FABScrollBehaviour(private val context: Context, private val attrs: AttributeSet) : FloatingActionButton.Behavior(context, attrs) {

    // Prevent swiping of non scrollable situation from hiding/showing button independently from
    // anchored to something if true
    private var isAnchored = false

    companion object {
        val TAG: String = "FABScrollBehaviour"
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton,
                                     directTargetChild: View, target: View, axes: Int, type: Int): Boolean {
        if (target is RecyclerView) {
            println("Recyc view height " + target.adapter!!.itemCount)
        }

        return true
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: FloatingActionButton, dependency: View): Boolean {
        if (dependency is RecyclerView || dependency is CardView) {
            return true
        }

        return false
    }

    private val hideListener = object: FloatingActionButton.OnVisibilityChangedListener() {
        override fun onHidden(fab: FloatingActionButton?) {
            super.onShown(fab)
            fab!!.visibility = View.INVISIBLE
        }
    }

    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: FloatingActionButton, target: View,
                                dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int, type: Int, consumed: IntArray) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)

        // Make sure view is not currently taking keyboard input
        val imm = this.context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        if(!imm.isAcceptingText) {
            if (dyConsumed > 0 && child.visibility == View.VISIBLE) {
                // Hide scrolling down
                child.hide(hideListener)
            } else if (dyConsumed < 0 && child.visibility != View.VISIBLE) {
                // User scrolled up and the FAB is currently not visible -> show the FAB
                child.show()
            } else if (dyUnconsumed < 0 && child.visibility != View.VISIBLE && !isAnchored) {
                // Force FAB to show up if list view can't be scrolled and FAB is invisible
                child.show()
                println("2nd Last else")
            } else if (dyUnconsumed > 0 && child.visibility == View.VISIBLE && !isAnchored) {
                // Force FAB to hide if list view can't be scrolled and FAB is visible
                child.hide(hideListener)
                println("Last else")
            }
        }

        println("Child visible?: " + child.visibility + ", dyCons: " + dyConsumed + " dyUncons " + dyUnconsumed)
    }

    fun setIsAnchored(bool: Boolean) {
        isAnchored = bool
    }

}