/*
 * Copyright 2018 Jeremy Patrick Pacabis
 * Copyright 2017-2018 Evren Coşkun
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ph.ingenuity.tableview.adapter.recyclerview

import android.content.Context
import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.util.Log
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder.SelectionState
import ph.ingenuity.tableview.listener.scroll.HorizontalRecyclerViewListener
import ph.ingenuity.tableview.listener.scroll.VerticalRecyclerViewListener

/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.adapter.recyclerview.holder <android-tableview-kotlin>
 */
class CellRecyclerView(context: Context) : RecyclerView(context) {

    var scrolledX = 0
        private set

    var scrolledY = 0
        private set

    var isHorizontalScrollListenerRemoved = true
        private set

    var isVerticalScrollListenerRemoved = true
        private set

    val isScrollOthers: Boolean
        get() = !isHorizontalScrollListenerRemoved

    init {
        setHasFixedSize(false)
        isNestedScrollingEnabled = false
    }

    override fun onScrolled(dx: Int, dy: Int) {
        scrolledX += dx
        scrolledY += dy
        super.onScrolled(dx, dy)
    }

    override fun addOnScrollListener(listener: RecyclerView.OnScrollListener) {
        when (listener) {
            is HorizontalRecyclerViewListener -> {
                if (isHorizontalScrollListenerRemoved) {
                    isHorizontalScrollListenerRemoved = false
                    super.addOnScrollListener(listener)
                } else {
                    Log.w(LOG_TAG, "HorizontalRecyclerViewListener has tried to add itself " +
                            "before removing the old one.")
                }
            }

            is VerticalRecyclerViewListener -> {
                if (isVerticalScrollListenerRemoved) {
                    isVerticalScrollListenerRemoved = false
                    super.addOnScrollListener(listener)
                } else {
                    Log.w(LOG_TAG, "VerticalRecyclerViewListener has tried to add itself " +
                            "before removing the old one.")
                }
            }

            else -> {
                super.addOnScrollListener(listener)
            }
        }
    }

    override fun removeOnScrollListener(listener: RecyclerView.OnScrollListener) {
        when (listener) {
            is HorizontalRecyclerViewListener -> {
                if (isHorizontalScrollListenerRemoved) {
                    Log.e(LOG_TAG, "HorizontalRecyclerViewListener has tried to remove " +
                            "itself before adding new one.")
                } else {
                    isHorizontalScrollListenerRemoved = true
                    super.removeOnScrollListener(listener)
                }
            }

            is VerticalRecyclerViewListener -> {
                if (isVerticalScrollListenerRemoved) {
                    Log.e(LOG_TAG, "VerticalRecyclerViewListener has tried to remove " +
                            "itself before adding new one.")
                } else {
                    isVerticalScrollListenerRemoved = true
                    super.removeOnScrollListener(listener)
                }
            }

            else -> {
                super.removeOnScrollListener(listener)
            }
        }
    }

    fun setSelected(
            selectionState: SelectionState,
            @ColorInt backgroundColor: Int,
            ignoreSelectionColors: Boolean
    ) {
        (0 until adapter.itemCount).forEach { i ->
            val viewHolder = findViewHolderForAdapterPosition(i) as AbstractViewHolder?
            if (!ignoreSelectionColors) {
                viewHolder?.setBackgroundColor(backgroundColor)
            }

            viewHolder?.setSelected(selectionState)
        }
    }

    fun clearScrolledX() {
        scrolledX = 0
    }

    companion object {
        private val LOG_TAG = CellRecyclerView::class.java.simpleName
    }
}