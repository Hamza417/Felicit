package app.simple.felicit.decoration.fastscroll

import androidx.recyclerview.widget.RecyclerView
import app.simple.felicit.decoration.fastscroll.FastScrollerBuilder

fun fastScroller(recyclerView: RecyclerView) {
    FastScrollerBuilder(recyclerView)
            .setPadding(0, recyclerView.paddingTop, 0, recyclerView.paddingBottom)
            .useDefaultStyle()
            .build()
}