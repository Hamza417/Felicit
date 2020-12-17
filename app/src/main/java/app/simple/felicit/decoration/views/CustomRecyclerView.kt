package app.simple.felicit.decoration.views

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.decoration.fastscroll.fastScroller
import app.simple.felicit.util.StatusBarHeight

/**
 * [CustomRecyclerView] extends [RecyclerView] and applies the current
 * device's status bar as the top padding offset and setup the fast scroller
 * as well - see [setFastScroller]
 */
class CustomRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {
        setFastScroller()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        setFastScroller()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        setFastScroller()
    }

    override fun isPaddingOffsetRequired(): Boolean {
        return true
    }

    override fun getTopPaddingOffset(): Int {
        return StatusBarHeight.getStatusBarHeight(resources)
    }

    override fun getBottomPaddingOffset(): Int {
        return 0
    }

    override fun getPaddingTop(): Int {
        return StatusBarHeight.getStatusBarHeight(resources)
    }

    override fun getPaddingBottom(): Int {
        return 0
    }

    private fun setFastScroller() {
        fastScroller(this)
    }
}