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
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseArray
import android.view.View
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.util.TableViewUtils

/**
 * Created by jeremypacabis on February 28, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.layoutmanager <android-tableview-kotlin>
 */
class ColumnLayoutManager(
        context: Context,
        private val tableView: ITableView,
        private val cellRowRecyclerView: CellRecyclerView
) : LinearLayoutManager(context) {

    private val cachedWidthList = SparseArray<SparseArray<Int>>()

    private val columnHeaderRecyclerView = tableView.columnHeaderRecyclerView

    private val columnHeaderLayoutManager = tableView.columnHeaderLayoutManager

    private val cellLayoutManager = tableView.cellLayoutManager

    var lastDx = 0
        private set

    var isNeedFit: Boolean = false
        private set

    private val rowPosition: Int
        get() = cellLayoutManager.getPosition(cellRowRecyclerView)

    val visibleViewHolders: Array<AbstractViewHolder?>
        get() {
            val visibleChildCount = findLastVisibleItemPosition() - findFirstVisibleItemPosition() + 1

            val views = arrayOfNulls<AbstractViewHolder>(visibleChildCount)
            for ((index, i) in (findFirstVisibleItemPosition()
                    until findLastVisibleItemPosition() + 1).withIndex()) {
                views[index] = cellRowRecyclerView
                        .findViewHolderForAdapterPosition(i) as AbstractViewHolder

            }

            return views
        }

    init {
        orientation = HORIZONTAL
    }

    override fun measureChildWithMargins(child: View, widthUsed: Int, heightUsed: Int) {
        super.measureChildWithMargins(child, widthUsed, heightUsed)
        if (tableView.hasFixedWidth) {
            return
        }

        measureChild(child, widthUsed, heightUsed)
    }

    override fun measureChild(child: View, widthUsed: Int, heightUsed: Int) {
        if (tableView.hasFixedWidth) {
            super.measureChild(child, widthUsed, heightUsed)
            return
        }

        val position = getPosition(child)
        val cacheWidth = getCacheWidth(position)
        val columnCacheWidth = columnHeaderLayoutManager.getCacheWidth(position)

        if (cacheWidth != -1 && cacheWidth == columnCacheWidth) {
            TableViewUtils.setWidth(child, cacheWidth)
        } else {
            fitWidthSize(child, position, cacheWidth, columnCacheWidth)
        }

        if (shouldFitColumns(position)) {
            if (lastDx < 0) {
                Log.e(LOG_TAG, "x: $position y: $rowPosition fitWidthSize left side.")
                cellLayoutManager.fitWidthSize(position, true)
            } else {
                cellLayoutManager.fitWidthSize(position, false)
                Log.e(LOG_TAG, "x: $position y: $rowPosition fitWidthSize right side.")
            }

            isNeedFit = false
        }
    }

    override fun scrollHorizontallyBy(
            dx: Int,
            recycler: RecyclerView.Recycler?,
            state: RecyclerView.State?
    ): Int {
        if (
                columnHeaderRecyclerView.scrollState == RecyclerView.SCROLL_STATE_IDLE &&
                cellRowRecyclerView.isScrollOthers
        ) {
            columnHeaderRecyclerView.scrollBy(dx, 0)
        }

        lastDx = dx
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    private fun shouldFitColumns(xPosition: Int): Boolean {
        if (isNeedFit) {
            val yPosition = cellLayoutManager.getPosition(cellRowRecyclerView)
            if (cellLayoutManager.shouldFitColumns(yPosition)) {
                if (lastDx > 0) {
                    if (xPosition == findLastVisibleItemPosition()) {
                        return true
                    }
                } else if (lastDx < 0) {
                    if (xPosition == findFirstVisibleItemPosition()) {
                        return true
                    }
                }
            }
        }

        return false
    }

    private fun fitWidthSize(
            child: View,
            position: Int,
            cellCacheWidth: Int,
            columnCacheWidth: Int
    ) {
        var cellWidth = cellCacheWidth
        if (cellWidth == -1) {
            cellWidth = child.measuredWidth
        }

        if (position > -1) {
            val columnHeaderChild = columnHeaderLayoutManager.findViewByPosition(position)
            if (columnHeaderChild != null) {
                var columnHeaderWidth = columnCacheWidth
                if (columnHeaderWidth == -1) {
                    columnHeaderWidth = columnHeaderChild.measuredWidth
                }

                if (cellWidth != 0) {
                    if (columnHeaderWidth > cellWidth) {
                        cellWidth = columnHeaderWidth
                    } else if (cellWidth > columnHeaderWidth) {
                        columnHeaderWidth = cellWidth
                    }

                    if (columnHeaderWidth != columnHeaderChild.width) {
                        TableViewUtils.setWidth(columnHeaderChild, columnHeaderWidth)
                        isNeedFit = true
                    }

                    columnHeaderLayoutManager.setCacheWidth(position, columnHeaderWidth)
                }
            }
        }

        TableViewUtils.setWidth(child, cellWidth)
        setCacheWidth(position, cellWidth)
    }

    fun setCacheWidth(position: Int, width: Int) {
        val yPosition = rowPosition
        var cellRowCaches: SparseArray<Int>? = cachedWidthList.get(yPosition)
        if (cellRowCaches == null) {
            cellRowCaches = SparseArray()
        }

        cellRowCaches.put(position, width)
        cachedWidthList.put(yPosition, cellRowCaches)
    }

    fun getCacheWidth(position: Int): Int {
        val yPosition = rowPosition
        val cellRowCaches = cachedWidthList.get(yPosition)
        if (cellRowCaches != null) {
            val cachedWidth = cellRowCaches.get(position)
            if (cachedWidth != null) {
                return cellRowCaches.get(position)
            }
        }

        return -1
    }

    fun clearNeedFit() {
        isNeedFit = false
    }

    companion object {
        private val LOG_TAG = ColumnLayoutManager::class.java.simpleName
    }
}
