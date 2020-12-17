package app.simple.felicit.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import android.view.Window
import app.simple.felicit.R

object StatusBarHeight {
    fun getStatusBarHeight(window: Window): Int {
        val rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        return rectangle.top - window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
    }

    fun getStatusBarHeight(resources: Resources): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = resources.getDimensionPixelSize(resourceId)
        }
        return result
    }

    fun getNavigationBarHeight(resources: Resources): Int {
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    fun getToolBarHeight(context: Context, resources: Resources): Int {
        // Calculate ActionBar height
        var i = 0
        val tv = TypedValue()
        if (context.theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
            i = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        return i
    }
}