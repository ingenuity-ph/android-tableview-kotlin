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

package ph.ingenuity.tableview.handler

import android.support.v7.util.DiffUtil
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerViewAdapter
import ph.ingenuity.tableview.adapter.recyclerview.ColumnHeaderRecyclerViewAdapter
import ph.ingenuity.tableview.adapter.recyclerview.RowHeaderRecyclerViewAdapter
import ph.ingenuity.tableview.feature.sort.*
import ph.ingenuity.tableview.feature.sort.SortState.UNSORTED
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by jeremypacabis on March 01, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.handler <android-tableview-kotlin>
 */
class ColumnSortHandler(tableView: ITableView) {

    private val cellRecyclerViewAdapter =
            tableView.cellRecyclerView.adapter as CellRecyclerViewAdapter

    private val rowHeaderRecyclerViewAdapter =
            tableView.rowHeaderRecyclerView.adapter as RowHeaderRecyclerViewAdapter

    private val columnHeaderRecyclerViewAdapter =
            tableView.columnHeaderRecyclerView.adapter as ColumnHeaderRecyclerViewAdapter

    val rowHeaderSortingStatus: SortState?
        get() = rowHeaderRecyclerViewAdapter.rowHeaderSortHelper!!.sortingStatus

    @Suppress("UNCHECKED_CAST")
    fun sortByRowHeader(sortState: SortState) {
        val originalList = rowHeaderRecyclerViewAdapter.items as MutableList<Sortable>
        val sortedList = ArrayList(originalList) as MutableList<Sortable>
        if (sortState != UNSORTED) {
            Collections.sort(sortedList, RowHeaderSortComparator(sortState))
        }

        rowHeaderRecyclerViewAdapter.rowHeaderSortHelper!!.sortingStatus = sortState
        swapItems(originalList, sortedList)
    }

    @Suppress("UNCHECKED_CAST")
    fun sort(column: Int, sortState: SortState) {
        val originalList = cellRecyclerViewAdapter.items as MutableList<Sortable>
        val sortedList = originalList as ArrayList<List<Sortable>>
        val originalRowHeaderList = rowHeaderRecyclerViewAdapter.items as MutableList<Sortable>
        val sortedRowHeaderList = ArrayList(originalRowHeaderList)
        if (sortState !== UNSORTED) {
            Collections.sort<List<Sortable>>(sortedList, ColumnSortComparator(column, sortState))
            val columnForRowHeaderSortComparator = ColumnForRowHeaderSortComparator(
                    originalRowHeaderList,
                    originalList,
                    column,
                    sortState
            )

            Collections.sort(sortedRowHeaderList, columnForRowHeaderSortComparator)
        }

        columnHeaderRecyclerViewAdapter.columnSortHelper!!.setSortingStatus(column, sortState)
        swapItems(originalList, sortedList, column, sortedRowHeaderList)
    }

    private fun swapItems(oldItems: List<Sortable>, newItems: List<Sortable>) {
        val diffCallback = RowHeaderSortCallback(oldItems, newItems)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        rowHeaderRecyclerViewAdapter.setItems(newItems, false)
        diffResult.dispatchUpdatesTo(rowHeaderRecyclerViewAdapter)
        diffResult.dispatchUpdatesTo(cellRecyclerViewAdapter)
    }

    private fun swapItems(
            oldItems: List<List<Sortable>>,
            newItems: List<List<Sortable>>,
            column: Int,
            newRowHeader: List<Sortable>
    ) {
        val diffCallback = ColumnSortCallback(oldItems, newItems, column)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        rowHeaderRecyclerViewAdapter.setItems(newRowHeader, false)
        cellRecyclerViewAdapter.setItems(newItems, false)
        diffResult.dispatchUpdatesTo(rowHeaderRecyclerViewAdapter)
        diffResult.dispatchUpdatesTo(cellRecyclerViewAdapter)
    }

    @Suppress("UNCHECKED_CAST")
    fun swapItems(newItems: List<List<Sortable>>, column: Int) {
        val oldItems = cellRecyclerViewAdapter.items as List<List<Sortable>>
        val diffCallback = ColumnSortCallback(oldItems, newItems, column)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        cellRecyclerViewAdapter.setItems(newItems, false)
        diffResult.dispatchUpdatesTo(rowHeaderRecyclerViewAdapter)
        diffResult.dispatchUpdatesTo(cellRecyclerViewAdapter)
    }

    fun getSortingStatus(column: Int): SortState {
        return columnHeaderRecyclerViewAdapter.columnSortHelper!!.getSortingStatus(column)
    }
}
