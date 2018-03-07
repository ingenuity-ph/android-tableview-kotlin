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

package ph.ingenuity.tableview.listener.scroll

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.SCROLL_STATE_IDLE
import android.util.Log
import android.view.MotionEvent
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView

/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.listener.scroll <android-tableview-kotlin>
 */
class VerticalRecyclerViewListener(
        tableView: ITableView
) : RecyclerView.OnScrollListener(), RecyclerView.OnItemTouchListener {

    private val rowHeaderRecyclerView: CellRecyclerView = tableView.rowHeaderRecyclerView

    private val cellRecyclerView: CellRecyclerView = tableView.cellRecyclerView

    private var lastTouchedRecyclerView: RecyclerView? = null

    private var isMoved: Boolean = false

    private var yPosition: Int = 0

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            if (rv.scrollState == SCROLL_STATE_IDLE) {
                if (lastTouchedRecyclerView != null && rv != lastTouchedRecyclerView) {
                    removeLastTouchedRecyclerViewScrollListener(false)
                }

                yPosition = (rv as CellRecyclerView).scrolledY
                rv.addOnScrollListener(this)
                if (rv == cellRecyclerView) {
                    Log.d(LOG_TAG, "CellRecyclerView scroll listener has been added.")
                } else if (rv == rowHeaderRecyclerView) {
                    Log.d(LOG_TAG, "RowHeaderRecyclerView scroll listener has been added.")
                }

                isMoved = false
            }
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            isMoved = true
        } else if (e.action == MotionEvent.ACTION_UP) {
            val nScrollY = (rv as CellRecyclerView).scrolledY
            if (yPosition == nScrollY && !isMoved && rv.scrollState == SCROLL_STATE_IDLE) {
                rv.removeOnScrollListener(this)
                if (rv == cellRecyclerView) {
                    Log.d(LOG_TAG, "CellRecyclerView scroll listener removed from up.")
                } else if (rv == rowHeaderRecyclerView) {
                    Log.d(LOG_TAG, "RowHeaderRecyclerView scroll listener removed from up.")
                }
            }

            lastTouchedRecyclerView = rv
        }

        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        if (recyclerView == cellRecyclerView) {
            super.onScrolled(recyclerView, dx, dy)
        } else if (recyclerView == rowHeaderRecyclerView) {
            super.onScrolled(recyclerView, dx, dy)
            cellRecyclerView.scrollBy(0, dy)
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == SCROLL_STATE_IDLE) {
            recyclerView!!.removeOnScrollListener(this)
            isMoved = false
            if (recyclerView == cellRecyclerView) {
                Log.d(
                        LOG_TAG,
                        "CellRecyclerView scroll listener removed from onScrollStateChanged."
                )
            } else if (recyclerView == rowHeaderRecyclerView) {
                Log.d(
                        LOG_TAG,
                        "RowHeaderRecyclerView scroll listener removed from onScrollStateChanged."
                )
            }
        }
    }

    fun removeLastTouchedRecyclerViewScrollListener(isNeeded: Boolean) {
        if (lastTouchedRecyclerView == cellRecyclerView) {
            cellRecyclerView.removeOnScrollListener(this)
            cellRecyclerView.stopScroll()
            Log.d(LOG_TAG, "CellRecyclerView scroll listener removed from last touched.")
        } else {
            rowHeaderRecyclerView.removeOnScrollListener(this)
            rowHeaderRecyclerView.stopScroll()
            Log.d(LOG_TAG, "RowHeaderRecyclerView scroll listener removed from last touched.")
            if (isNeeded) {
                cellRecyclerView.removeOnScrollListener(this)
                cellRecyclerView.stopScroll()
                Log.d(LOG_TAG, "CellRecyclerView scroll listener removed from last touched.")
            }
        }
    }

    companion object {
        private val LOG_TAG = VerticalRecyclerViewListener::class.java.simpleName
    }
}
