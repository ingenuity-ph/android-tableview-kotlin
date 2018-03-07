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

import android.support.v7.widget.LinearLayoutManager
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
class HorizontalRecyclerViewListener(
        tableView: ITableView
) : RecyclerView.OnScrollListener(), RecyclerView.OnItemTouchListener {

    private val verticalRecyclerViewListener = tableView.verticalRecyclerViewListener

    private val columnHeaderRecyclerView = tableView.columnHeaderRecyclerView

    private val cellLayoutManager = tableView.cellRecyclerView.layoutManager

    private var lastTouchedRecyclerView: RecyclerView? = null

    private var isMoved: Boolean = false

    private var xPosition: Int = 0

    var scrollPositionOffset = 0

    var scrollPosition: Int = 0

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        if (e.action == MotionEvent.ACTION_DOWN) {
            if (rv.scrollState == SCROLL_STATE_IDLE) {
                if (lastTouchedRecyclerView != null && rv != lastTouchedRecyclerView) {
                    if (lastTouchedRecyclerView == columnHeaderRecyclerView) {
                        columnHeaderRecyclerView.removeOnScrollListener(this)
                        columnHeaderRecyclerView.stopScroll()
                        Log.d(
                                LOG_TAG,
                                "Scroll listener has been moved to " +
                                        "columnHeaderRecyclerView at last touch control."
                        )
                    } else {
                        val lastTouchedIndex = getIndex(lastTouchedRecyclerView!!)
                        if (
                                lastTouchedIndex >= 0 &&
                                lastTouchedIndex < cellLayoutManager.childCount
                        ) {
                            if (!(lastTouchedRecyclerView as CellRecyclerView)
                                            .isHorizontalScrollListenerRemoved) {
                                (cellLayoutManager.getChildAt(lastTouchedIndex) as RecyclerView)
                                        .removeOnScrollListener(this)
                                Log.d(
                                        LOG_TAG,
                                        "Scroll listener has been moved to " +
                                                "${lastTouchedRecyclerView!!.id} " +
                                                "CellRecyclerView at last touch control."
                                )

                                (cellLayoutManager.getChildAt(lastTouchedIndex) as RecyclerView)
                                        .stopScroll()
                            }
                        }
                    }
                }

                xPosition = (rv as CellRecyclerView).scrolledX
                rv.addOnScrollListener(this)
                Log.d(
                        LOG_TAG,
                        "Scroll listener has been added to ${rv.id} at action down."
                )
            }
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            isMoved = true
        } else if (e.action == MotionEvent.ACTION_UP) {
            val nScrollX = (rv as CellRecyclerView).scrolledX
            if (xPosition == nScrollX && !isMoved) {
                rv.removeOnScrollListener(this)
                Log.d(
                        LOG_TAG,
                        "Scroll listener  has been removed to ${rv.id} at action up."
                )
            }

            lastTouchedRecyclerView = rv
        } else if (e.action == MotionEvent.ACTION_CANCEL) {
            renewScrollPosition(rv)
            rv.removeOnScrollListener(this)
            Log.d(
                    LOG_TAG,
                    "Scroll listener  has been removed to ${rv.id} at action cancel."
            )

            lastTouchedRecyclerView = rv
            isMoved = false
        }

        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        if (recyclerView == columnHeaderRecyclerView) {
            super.onScrolled(recyclerView, dx, dy)
            (0 until cellLayoutManager.childCount)
                    .map { cellLayoutManager.getChildAt(it) as CellRecyclerView }
                    .forEach { it.scrollBy(dx, 0) }
        } else {
            super.onScrolled(recyclerView, dx, dy)
            (0 until cellLayoutManager.childCount)
                    .map { cellLayoutManager.getChildAt(it) as CellRecyclerView }
                    .filter { it !== recyclerView }
                    .forEach { it.scrollBy(dx, 0) }
        }
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
        super.onScrollStateChanged(recyclerView, newState)
        if (newState == SCROLL_STATE_IDLE) {
            renewScrollPosition(recyclerView)
            recyclerView!!.removeOnScrollListener(this)
            Log.d(
                    LOG_TAG,
                    "Scroll listener has been moved to " +
                            "${recyclerView.id} at onScrollStateChanged."
            )

            isMoved = false
            val isNeeded = lastTouchedRecyclerView !== columnHeaderRecyclerView
            verticalRecyclerViewListener.removeLastTouchedRecyclerViewScrollListener(isNeeded)
        }
    }

    private fun getIndex(rv: RecyclerView): Int {
        (0 until cellLayoutManager.childCount).forEach { i ->
            val child = cellLayoutManager.getChildAt(i) as RecyclerView
            if (child == rv) {
                return i
            }
        }

        return -1
    }

    private fun renewScrollPosition(recyclerView: RecyclerView?) {
        val layoutManager = recyclerView!!.layoutManager as LinearLayoutManager
        scrollPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
        if (scrollPosition == -1) {
            scrollPosition = layoutManager.findFirstVisibleItemPosition()
            if (layoutManager.findFirstVisibleItemPosition() ==
                    layoutManager.findLastVisibleItemPosition()) {
            } else {
                scrollPosition += 1
            }
        }

        scrollPositionOffset = recyclerView.layoutManager.findViewByPosition(scrollPosition).left
    }

    companion object {
        private val LOG_TAG = HorizontalRecyclerViewListener::class.java.simpleName
    }
}
