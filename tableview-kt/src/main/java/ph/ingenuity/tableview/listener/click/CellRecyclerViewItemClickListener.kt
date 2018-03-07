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

package ph.ingenuity.tableview.listener.click

import android.support.v7.widget.RecyclerView
import android.view.MotionEvent
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView
import ph.ingenuity.tableview.adapter.recyclerview.CellRowRecyclerViewAdapter
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder

/**
 * Created by jeremypacabis on February 28, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.listener.click <android-tableview-kotlin>
 */
class CellRecyclerViewItemClickListener(
        recyclerView: CellRecyclerView,
        tableView: ITableView
) : AbstractItemClickListener(recyclerView, tableView) {

    private val cellRecyclerView = tableView.cellRecyclerView

    override fun clickAction(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        if (childView != null && gestureDetector.onTouchEvent(e)) {
            val holder = recyclerView.getChildViewHolder(childView) as AbstractViewHolder
            val adapter = recyclerView.adapter as CellRowRecyclerViewAdapter
            val column = holder.adapterPosition
            val row = adapter.yPosition
            if (!tableView.ignoreSelectionColors) {
                selectionHandler.setSelectedCellPositions(holder, column, row)
            }

            tableViewListener?.onCellClicked(holder, column, row)
            return true
        }

        return false
    }

    override fun longPressAction(e: MotionEvent) {
        if (
                recyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE ||
                cellRecyclerView.scrollState != RecyclerView.SCROLL_STATE_IDLE
        ) {
            return
        }

        val child = recyclerView.findChildViewUnder(e.x, e.y)
        if (child != null && tableViewListener != null) {
            val holder = recyclerView.getChildViewHolder(child)
            val adapter = recyclerView.adapter as CellRowRecyclerViewAdapter
            tableViewListener!!.onCellLongPressed(holder, holder.adapterPosition, adapter.yPosition)
        }
    }
}