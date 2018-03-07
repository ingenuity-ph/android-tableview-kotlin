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
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ph.ingenuity.tableview.adapter.ITableAdapter
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.layoutmanager.ColumnLayoutManager
import ph.ingenuity.tableview.listener.click.CellRecyclerViewItemClickListener
import ph.ingenuity.tableview.listener.scroll.HorizontalRecyclerViewListener
import java.util.*

/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.adapter.recyclerview <android-tableview-kotlin>
 */

@Suppress("UNCHECKED_CAST")
class CellRecyclerViewAdapter(
        context: Context,
        items: List<Any>?,
        private val tableAdapter: ITableAdapter
) : AbstractRecyclerViewAdapter(context, items) {

    private var horizontalListener: HorizontalRecyclerViewListener? = null

    private var recyclerViewId = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        // Get the TableView instance
        val tableView = tableAdapter.tableView

        // Create a RecyclerView to be a row of the CellRecyclerView
        val recyclerView = CellRecyclerView(context)

        // Add horizontal separators if enabled
        if (tableView!!.showHorizontalSeparators) {
            recyclerView.addItemDecoration(tableView.horizontalItemDecoration)
        }

        // Set RecyclerView to have fixed width
        recyclerView.setHasFixedSize(tableView.hasFixedWidth)

        // Set the horizontal RecyclerView listener
        if (horizontalListener == null) {
            horizontalListener = tableView.horizontalRecyclerViewListener
        }

        // Add the horizontal RecyclerView listener
        recyclerView.addOnItemTouchListener(horizontalListener)

        // Add an item click listener for each RecyclerView cell item
        recyclerView.addOnItemTouchListener(CellRecyclerViewItemClickListener(
                recyclerView,
                tableView
        ))

        // Set the column LayoutManager for the RecyclerView
        val layoutManager = ColumnLayoutManager(context, tableView, recyclerView)
        recyclerView.layoutManager = layoutManager

        // Set the RecyclerView ID
        recyclerView.id = recyclerViewId++

        return CellRowViewHolder(recyclerView)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, yPosition: Int) {
        if (holder !is CellRowViewHolder) {
            return
        }

        // Set the adapter for the RecyclerView with the list of items
        val rowList = items!![yPosition] as List<Any>
        val viewAdapter = CellRowRecyclerViewAdapter(context, rowList, tableAdapter, yPosition)
        holder.recyclerView.adapter = viewAdapter
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)

        // Display the newly attached RecyclerView on scrolled position
        val viewHolder = holder as CellRowViewHolder
        (viewHolder.recyclerView.layoutManager as ColumnLayoutManager)
                .scrollToPositionWithOffset(
                        horizontalListener!!.scrollPosition,
                        horizontalListener!!.scrollPositionOffset
                )

        val selectionHandler = tableAdapter.tableView!!.selectionHandler
        if (selectionHandler.anyColumnSelected) {
            val cellViewHolder = holder.recyclerView.findViewHolderForAdapterPosition(
                    selectionHandler.selectedColumnPosition) as AbstractViewHolder?
            if (!tableAdapter.tableView!!.ignoreSelectionColors) {
                cellViewHolder?.setBackgroundColor(tableAdapter.tableView!!.selectedColor)
            }

            cellViewHolder?.setSelected(AbstractViewHolder.SelectionState.SELECTED)
        } else if (selectionHandler.isRowSelected(holder.getAdapterPosition())) {
            viewHolder.recyclerView.setSelected(
                    AbstractViewHolder.SelectionState.SELECTED,
                    tableAdapter.tableView!!.selectedColor,
                    tableAdapter.tableView!!.ignoreSelectionColors
            )
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)

        // Clear selection status of the ViewHolder
        (holder as CellRowViewHolder).recyclerView.setSelected(
                AbstractViewHolder.SelectionState.UNSELECTED,
                tableAdapter.tableView!!.unselectedColor,
                tableAdapter.tableView!!.ignoreSelectionColors
        )
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)

        val viewHolder = holder as CellRowViewHolder
        viewHolder.recyclerView.clearScrolledX()
    }

    internal class CellRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: CellRecyclerView = itemView as CellRecyclerView
    }

    fun notifyCellDataSetChanged() {
        val visibleRecyclerViews =
                tableAdapter.tableView!!.cellLayoutManager.visibleCellRowRecyclerViews
        if (visibleRecyclerViews.isNotEmpty()) {
            for (cellRowRecyclerView in visibleRecyclerViews) {
                cellRowRecyclerView?.adapter?.notifyDataSetChanged()
            }
        } else {
            notifyDataSetChanged()
        }
    }

    fun getColumnItems(columnPosition: Int): List<Any> = (0 until items!!.size)
            .map { items!![it] as List<Any> }
            .filter { it.size > columnPosition }
            .mapTo(ArrayList()) { it[columnPosition] }

    fun removeColumnItems(column: Int) {
        // Remove columns from visible RecyclerViews
        val visibleRecyclerViews =
                tableAdapter.tableView!!.cellLayoutManager.visibleCellRowRecyclerViews
        visibleRecyclerViews.forEach { cellRowRecyclerView ->
            (cellRowRecyclerView?.adapter as AbstractRecyclerViewAdapter).deleteItem(column)
        }

        // Update the list with column items removed
        val cellItems = ArrayList<List<Any>>()
        (0 until items!!.size).forEach { i ->
            val rowList = ArrayList(items!![i] as List<Any>)
            if (rowList.size > column) {
                rowList.removeAt(column)
            }

            cellItems.add(rowList)
        }

        setItems(cellItems as List<Any>, false)
    }

    fun addColumnItems(column: Int, cellColumnItems: List<Any?>) {
        if (cellColumnItems.size != items!!.size || (cellColumnItems as List<*>).contains(null)) {
            return
        }

        // Add columns from visible RecyclerViews
        val layoutManager = tableAdapter.tableView!!.cellLayoutManager
        for (i in layoutManager.findFirstVisibleItemPosition() until
                layoutManager.findLastVisibleItemPosition() + 1) {
            val cellRowRecyclerView = layoutManager.findViewByPosition(i) as RecyclerView
            (cellRowRecyclerView.adapter as AbstractRecyclerViewAdapter)
                    .addItem(column, (cellColumnItems[i]))
        }

        // Update the list with column items added
        val cellItems = ArrayList<List<Any>>()
        (0 until items!!.size).forEach { i ->
            val rowList = ArrayList(items!![i] as List<Any>)

            if (rowList.size > column) {
                rowList.add(column, cellColumnItems[i])
            }

            cellItems.add(rowList)
        }

        setItems(cellItems as List<Any>, false)
    }
}
