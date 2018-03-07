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

package ph.ingenuity.tableview

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.ViewGroup
import ph.ingenuity.tableview.adapter.AbstractTableAdapter
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView
import ph.ingenuity.tableview.feature.filter.Filter
import ph.ingenuity.tableview.feature.sort.SortState
import ph.ingenuity.tableview.handler.*
import ph.ingenuity.tableview.layoutmanager.CellLayoutManager
import ph.ingenuity.tableview.layoutmanager.ColumnHeaderLayoutManager
import ph.ingenuity.tableview.listener.ITableViewListener
import ph.ingenuity.tableview.listener.click.ColumnHeaderRecyclerViewItemClickListener
import ph.ingenuity.tableview.listener.click.RowHeaderRecyclerViewItemClickListener
import ph.ingenuity.tableview.listener.scroll.HorizontalRecyclerViewListener
import ph.ingenuity.tableview.listener.scroll.VerticalRecyclerViewListener

/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview <android-tableview-kotlin>
 */
interface ITableView {

    // Boolean attributes

    var showHorizontalSeparators: Boolean

    var showVerticalSeparators: Boolean

    var ignoreSelectionColors: Boolean

    var hasFixedWidth: Boolean

    var isSorted: Boolean

    // Dimensions attributes

    var columnHeaderHeight: Int

    var rowHeaderWidth: Int

    // Colors attributes

    var unselectedColor: Int

    var separatorColor: Int

    var selectedColor: Int

    var shadowColor: Int

    // Handlers

    var visibilityHandler: VisibilityHandler

    var columnSortHandler: ColumnSortHandler

    var selectionHandler: SelectionHandler

    var scrollHandler: ScrollHandler

    var filterHandler: FilterHandler

    // Listeners

    var columnHeaderRecyclerViewItemClickListener: ColumnHeaderRecyclerViewItemClickListener

    var rowHeaderRecyclerViewItemClickListener: RowHeaderRecyclerViewItemClickListener

    var horizontalRecyclerViewListener: HorizontalRecyclerViewListener

    var verticalRecyclerViewListener: VerticalRecyclerViewListener

    var tableViewListener: ITableViewListener?

    // TableView fields

    var columnHeaderRecyclerView: CellRecyclerView

    var rowHeaderRecyclerView: CellRecyclerView

    var cellRecyclerView: CellRecyclerView

    var columnHeaderLayoutManager: ColumnHeaderLayoutManager

    var rowHeaderLayoutManager: LinearLayoutManager

    var cellLayoutManager: CellLayoutManager

    var horizontalItemDecoration: DividerItemDecoration

    var verticalItemDecoration: DividerItemDecoration

    var adapter: AbstractTableAdapter?

    var rowHeaderSortState: SortState

    // Item selection

    var selectedColumn: Int?

    var selectedRow: Int?

    // TableView methods

    fun addView(child: View, params: ViewGroup.LayoutParams)

    fun scrollToColumn(column: Int)

    fun scrollToRow(row: Int)

    fun showColumn(column: Int)

    fun hideColumn(column: Int)

    fun showRow(row: Int)

    fun hideRow(row: Int)

    fun clearHiddenColumnList()

    fun clearHiddenRowList()

    fun showAllHiddenColumns()

    fun showAllHiddenRows()

    fun sortColumn(column: Int, sortState: SortState)

    fun sortRowHeader(sortState: SortState)

    fun remeasureColumnWidth(column: Int)

    fun filter(filter: Filter)

    fun getColumnSortState(column: Int): SortState

    fun isColumnVisible(column: Int): Boolean

    fun isRowVisible(row: Int): Boolean
}