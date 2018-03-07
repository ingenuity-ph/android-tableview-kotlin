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

package ph.ingenuity.tableview.layoutmanager

import android.content.Context
import android.os.Handler
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.listener.scroll.HorizontalRecyclerViewListener
import ph.ingenuity.tableview.util.TableViewUtils

/**
 * Created by jeremypacabis on February 28, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.layoutmanager <android-tableview-kotlin>
 */
class CellLayoutManager(
        context: Context,
        private val tableView: ITableView
) : LinearLayoutManager(context) {

    private val columnHeaderLayoutManager = tableView.columnHeaderLayoutManager

    private val rowHeaderLayoutManager = tableView.rowHeaderLayoutManager

    private val rowHeaderRecyclerView = tableView.rowHeaderRecyclerView

    private var cellRecyclerView: CellRecyclerView? = null

    private var horizontalListener: HorizontalRecyclerViewListener? = null

    private var lastDy = 0

    private var needSetLeft: Boolean = false

    private var needFit: Boolean = false

    val visibleCellRowRecyclerViews: Array<CellRecyclerView?>
        get() {
            val length = findLastVisibleItemPosition() - findFirstVisibleItemPosition() + 1
            val recyclerViews = arrayOfNulls<CellRecyclerView>(length)

            for ((index, i) in (findFirstVisibleItemPosition()
                    until findLastVisibleItemPosition() + 1).withIndex()) {
                recyclerViews[index] = findViewByPosition(i) as CellRecyclerView
            }

            return recyclerViews
        }

    init {
        orientation = VERTICAL
    }

    override fun onAttachedToWindow(view: RecyclerView?) {
        super.onAttachedToWindow(view)
        if (cellRecyclerView == null) {
            cellRecyclerView = tableView.cellRecyclerView
        }

        if (horizontalListener == null) {
            horizontalListener = tableView.horizontalRecyclerViewListener
        }
    }

    override fun scrollVerticallyBy(
            dy: Int,
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
    ): Int {
        // CellRecyclerView should be scrolled after the RowHeaderRecyclerView
        // because it will be the basis of each column fit
        if (
                rowHeaderRecyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                !rowHeaderRecyclerView.isScrollOthers
        ) {
            rowHeaderRecyclerView.scrollBy(0, dy)
        }

        val scroll = super.scrollVerticallyBy(dy, recycler, state)
        lastDy = dy
        return scroll
    }

    override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (state == RecyclerView.SCROLL_STATE_IDLE) {
            lastDy = 0
        }
    }

    override fun measureChildWithMargins(child: View, widthUsed: Int, heightUsed: Int) {
        super.measureChildWithMargins(child, widthUsed, heightUsed)

        // If TableView fixed width is true, then calculation of the column width is not necessary.
        if (tableView.hasFixedWidth) {
            super.measureChildWithMargins(child, widthUsed, heightUsed)
            return
        }

        val position = getPosition(child)
        val childLayoutManager = (child as CellRecyclerView).layoutManager as ColumnLayoutManager
        if (
                cellRecyclerView!!.scrollState !=
                RecyclerView.SCROLL_STATE_IDLE
        ) {
            if (childLayoutManager.isNeedFit) {
                if (lastDy < 0) {
                    Log.e(LOG_TAG, "$position fitWidthSize all vertically up.")
                    fitWidthSize(true)
                } else {
                    Log.e(LOG_TAG, "$position fitWidthSize all vertically down.")
                    fitWidthSize(false)
                }

                childLayoutManager.clearNeedFit()
            }
        } else if (
                cellRecyclerView!!.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                childLayoutManager.lastDx == 0
        ) {
            if (childLayoutManager.isNeedFit) {
                needFit = true
                childLayoutManager.clearNeedFit()
            }

            if (needFit) {
                if (rowHeaderLayoutManager.findLastVisibleItemPosition() == position) {
                    Log.e(LOG_TAG, "$position fitWidthSize populating data for the first time.")
                    fitWidthSize2(false)
                    needFit = false
                }
            }
        }
    }

    private fun fitWidthSize(scrollingUp: Boolean) {
        var left = columnHeaderLayoutManager.firstItemLeft
        for (i in columnHeaderLayoutManager.findFirstVisibleItemPosition()
                until columnHeaderLayoutManager.findLastVisibleItemPosition() + 1) {
            left = fitSize(i, left, scrollingUp)
        }

        needSetLeft = false
    }

    private fun fitSize(position: Int, left: Int, scrollingUp: Boolean): Int {
        var cellRight = -1
        val columnCacheWidth = columnHeaderLayoutManager.getCacheWidth(position)
        val column = columnHeaderLayoutManager.findViewByPosition(position)
        if (column != null) {
            cellRight = column.left + columnCacheWidth + 1
            if (scrollingUp) {
                for (i in findLastVisibleItemPosition() downTo findFirstVisibleItemPosition()) {
                    cellRight = fit(position, i, left, cellRight, columnCacheWidth)
                }
            } else {
                for (j in findFirstVisibleItemPosition() until findLastVisibleItemPosition() + 1) {
                    cellRight = fit(position, j, left, cellRight, columnCacheWidth)
                }
            }
        }

        return cellRight
    }

    private fun fit(
            xPosition: Int,
            yPosition: Int,
            left: Int,
            right: Int,
            columnCachedWidth: Int
    ): Int {
        var nRight = right
        val child = findViewByPosition(yPosition) as CellRecyclerView
        val childLayoutManager = child.layoutManager as ColumnLayoutManager
        var cellCacheWidth = childLayoutManager.getCacheWidth(xPosition)
        val cell = childLayoutManager.findViewByPosition(xPosition)

        if (cell != null) {
            if (cellCacheWidth != columnCachedWidth || needSetLeft) {
                if (cellCacheWidth != columnCachedWidth) {
                    cellCacheWidth = columnCachedWidth
                    TableViewUtils.setWidth(cell, cellCacheWidth)
                    childLayoutManager.setCacheWidth(xPosition, cellCacheWidth)
                }

                if (left != IGNORE_LEFT && cell.left != left) {
                    val scrollX = Math.max(cell.left, left) - Math.min(cell.left, left)
                    cell.left = left
                    if (
                            horizontalListener!!.scrollPositionOffset > 0 &&
                            xPosition == childLayoutManager.findFirstVisibleItemPosition() &&
                            cellRecyclerView!!.scrollState != RecyclerView.SCROLL_STATE_IDLE) {

                        horizontalListener!!.scrollPositionOffset =
                                horizontalListener!!.scrollPositionOffset + scrollX
                        childLayoutManager.scrollToPositionWithOffset(
                                horizontalListener!!.scrollPosition,
                                horizontalListener!!.scrollPositionOffset
                        )
                    }
                }

                if (cell.width != cellCacheWidth) {
                    if (left != IGNORE_LEFT) {
                        nRight = cell.left + cellCacheWidth + 1
                        cell.right = nRight

                        childLayoutManager.layoutDecoratedWithMargins(
                                cell,
                                cell.left,
                                cell.top,
                                cell.right,
                                cell.bottom
                        )
                    }

                    needSetLeft = true
                }
            }
        }

        return nRight
    }

    private fun fitWidthSize2(scrollingLeft: Boolean) {
        columnHeaderLayoutManager.customRequestLayout()
        val columnHeaderScrollPosition = tableView.columnHeaderRecyclerView.scrolledX
        val columnHeaderOffset = columnHeaderLayoutManager.firstItemLeft
        val columnHeaderFirstItem = columnHeaderLayoutManager.findFirstVisibleItemPosition()
        for (i in columnHeaderLayoutManager.findFirstVisibleItemPosition()
                until columnHeaderLayoutManager.findLastVisibleItemPosition() + 1) {
            fitSize2(
                    i,
                    scrollingLeft,
                    columnHeaderScrollPosition,
                    columnHeaderOffset,
                    columnHeaderFirstItem
            )
        }

        needSetLeft = false
    }

    private fun fitSize2(
            position: Int,
            scrollingLeft: Boolean,
            columnHeaderScrollPosition: Int,
            columnHeaderOffset: Int,
            columnHeaderFirstItem: Int
    ) {
        val columnCacheWidth = columnHeaderLayoutManager.getCacheWidth(position)
        val column = columnHeaderLayoutManager.findViewByPosition(position)
        if (column != null) {
            for (j in findFirstVisibleItemPosition() until findLastVisibleItemPosition() + 1) {
                val child = findViewByPosition(j) as CellRecyclerView
                if (!scrollingLeft && columnHeaderScrollPosition != child.scrolledX) {
                    (child.layoutManager as LinearLayoutManager)
                            .scrollToPositionWithOffset(columnHeaderFirstItem, columnHeaderOffset)
                }

                fit2(position, j, columnCacheWidth, column)
            }
        }
    }

    private fun fit2(xPosition: Int, yPosition: Int, columnCachedWidth: Int, column: View) {
        val child = findViewByPosition(yPosition) as CellRecyclerView
        val childLayoutManager = child.layoutManager as ColumnLayoutManager
        var cellCacheWidth = childLayoutManager.getCacheWidth(xPosition)
        val cell = childLayoutManager.findViewByPosition(xPosition)
        if (cell != null) {
            if (cellCacheWidth != columnCachedWidth || needSetLeft) {
                if (cellCacheWidth != columnCachedWidth) {
                    cellCacheWidth = columnCachedWidth
                    TableViewUtils.setWidth(cell, cellCacheWidth)
                    childLayoutManager.setCacheWidth(xPosition, cellCacheWidth)
                }

                if (column.left != cell.left || column.right != cell.right) {
                    cell.left = column.left
                    cell.right = column.right + 1
                    childLayoutManager.layoutDecoratedWithMargins(
                            cell,
                            cell.left,
                            cell.top,
                            cell.right,
                            cell.bottom
                    )

                    needSetLeft = true
                }
            }
        }
    }

    fun fitWidthSize(position: Int, scrollingLeft: Boolean) {
        fitSize(position, IGNORE_LEFT, false)
        if (needSetLeft and scrollingLeft) {
            val handler = Handler()
            handler.post { fitWidthSize2(true) }
        }
    }

    fun shouldFitColumns(yPosition: Int): Boolean {
        if (cellRecyclerView!!.scrollState == RecyclerView.SCROLL_STATE_IDLE) {
            val cellRecyclerView = findViewByPosition(yPosition) as CellRecyclerView
            if (!cellRecyclerView.isScrollOthers) {
                val lastVisiblePosition = findLastVisibleItemPosition()
                val lastCellRecyclerView =
                        findViewByPosition(lastVisiblePosition) as CellRecyclerView?
                if (lastCellRecyclerView != null) {
                    if (yPosition == lastVisiblePosition) {
                        return true
                    } else if (
                            lastCellRecyclerView.isScrollOthers &&
                            yPosition == lastVisiblePosition - 1
                    ) {
                        return true
                    }
                }
            }
        }

        return false
    }

    fun getVisibleCellViewsByColumnPosition(xPosition: Int): Array<AbstractViewHolder?> {
        val visibleChildCount = findLastVisibleItemPosition() - findFirstVisibleItemPosition() + 1
        val viewHolders = arrayOfNulls<AbstractViewHolder>(visibleChildCount)
        (findFirstVisibleItemPosition() until findLastVisibleItemPosition() + 1)
                .map { findViewByPosition(it) as CellRecyclerView? }
                .map { it?.findViewHolderForAdapterPosition(xPosition) as AbstractViewHolder? }
                .forEachIndexed { index, holder -> viewHolders[index] = holder }
        return viewHolders
    }

    fun getCellViewHolder(xPosition: Int, yPosition: Int): AbstractViewHolder? {
        val cellRowRecyclerView = findViewByPosition(yPosition) as CellRecyclerView?
        return cellRowRecyclerView?.findViewHolderForAdapterPosition(xPosition) as AbstractViewHolder?
    }

    fun remeasureAllChild() {
        for (j in 0 until childCount) {
            val recyclerView = getChildAt(j) as CellRecyclerView
            recyclerView.layoutParams.width = WRAP_CONTENT
            recyclerView.requestLayout()
        }
    }

    companion object {
        private val LOG_TAG = CellLayoutManager::class.java.simpleName
        private const val IGNORE_LEFT = -99999
    }
}
