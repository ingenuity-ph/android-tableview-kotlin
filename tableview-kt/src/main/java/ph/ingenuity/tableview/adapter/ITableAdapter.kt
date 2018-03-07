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

package ph.ingenuity.tableview.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerViewAdapter
import ph.ingenuity.tableview.adapter.recyclerview.ColumnHeaderRecyclerViewAdapter
import ph.ingenuity.tableview.adapter.recyclerview.RowHeaderRecyclerViewAdapter
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.listener.data.AdapterDataSetChangedListener

/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.adapter <android-tableview-kotlin>
 */
interface ITableAdapter {

    var cellItems: List<List<Any>>?

    var columnHeaderItems: List<Any>?

    var rowHeaderItems: List<Any>?

    var cellRecyclerViewAdapter: CellRecyclerViewAdapter?

    var columnHeaderRecyclerViewAdapter: ColumnHeaderRecyclerViewAdapter?

    var rowHeaderRecyclerViewAdapter: RowHeaderRecyclerViewAdapter?

    var cornerView: View?

    var tableView: ITableView?

    var columnHeaderHeight: Int?

    var rowHeaderWidth: Int?

    var dataSetChangedListeners: MutableList<AdapterDataSetChangedListener>

    fun getColumnHeaderItemViewType(column: Int): Int

    fun getRowHeaderItemViewType(row: Int): Int

    fun getCellItemViewType(column: Int): Int

    fun onCreateCellViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun onBindCellViewHolder(
            holder: AbstractViewHolder,
            cellItem: Any,
            column: Int,
            row: Int
    )

    fun onCreateColumnHeaderViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun onBindColumnHeaderViewHolder(
            holder: AbstractViewHolder,
            columnHeaderItem: Any,
            column: Int
    )

    fun onCreateRowHeaderViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    fun onBindRowHeaderViewHolder(
            holder: AbstractViewHolder,
            rowHeaderItem: Any,
            row: Int
    )

    fun onCreateCornerView(): View?

    fun addAdapterDataSetChangedListener(listener: AdapterDataSetChangedListener)
}
