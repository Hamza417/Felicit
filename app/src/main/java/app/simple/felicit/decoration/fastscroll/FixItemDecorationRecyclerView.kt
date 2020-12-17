/*
 * Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package app.simple.felicit.decoration.fastscroll

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import androidx.annotation.AttrRes
import androidx.recyclerview.widget.RecyclerView

class FixItemDecorationRecyclerView : RecyclerView {
    constructor(context: Context) : super(context) {
        fastScroller(this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        fastScroller(this)
    }

    constructor(context: Context, attrs: AttributeSet?,
                @AttrRes defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        fastScroller(this)
    }

    override fun dispatchDraw(canvas: Canvas) {
        run {
            var i = 0
            val count = itemDecorationCount
            while (i < count) {
                val decor = super.getItemDecorationAt(i) as FixItemDecoration
                decor.itemDecoration.onDraw(canvas, this, decor.state!!)
                ++i
            }
        }
        super.dispatchDraw(canvas)
        var i = 0
        val count = itemDecorationCount
        while (i < count) {
            val decor = super.getItemDecorationAt(i) as FixItemDecoration
            decor.itemDecoration.onDrawOver(canvas, this, decor.state!!)
            ++i
        }
    }

    override fun addItemDecoration(decor: ItemDecoration, index: Int) {
        super.addItemDecoration(FixItemDecoration(decor), index)
    }

    override fun getItemDecorationAt(index: Int): ItemDecoration {
        return (super.getItemDecorationAt(index) as FixItemDecoration).itemDecoration
    }

    override fun removeItemDecoration(decor_: ItemDecoration) {
        var decor = decor_
        if (decor !is FixItemDecoration) {
            var i = 0
            val count = itemDecorationCount
            while (i < count) {
                val fixDecor = super.getItemDecorationAt(i) as FixItemDecoration
                if (fixDecor.itemDecoration === decor) {
                    decor = fixDecor
                    break
                }
                ++i
            }
        }
        super.removeItemDecoration(decor)
    }

    override fun isPaddingOffsetRequired(): Boolean {
        return true
    }

    private class FixItemDecoration(val itemDecoration: ItemDecoration) : ItemDecoration() {
        var state: State? = null
            private set

        override fun onDraw(c: Canvas, parent: RecyclerView, state: State) {
            this.state = state
        }

        override fun onDraw(c: Canvas, parent: RecyclerView) {}
        override fun onDrawOver(c: Canvas, parent: RecyclerView,
                                state: State) {
        }

        override fun onDrawOver(c: Canvas, parent: RecyclerView) {}
        override fun getItemOffsets(outRect: Rect, itemPosition: Int,
                                    parent: RecyclerView) {
            itemDecoration.getItemOffsets(outRect, itemPosition, parent)
        }

        override fun getItemOffsets(outRect: Rect, view: View,
                                    parent: RecyclerView, state: State) {
            itemDecoration.getItemOffsets(outRect, view, parent, state)
        }
    }
}