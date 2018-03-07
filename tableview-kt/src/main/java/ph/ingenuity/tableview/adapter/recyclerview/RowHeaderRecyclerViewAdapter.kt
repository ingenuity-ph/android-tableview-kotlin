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
import android.view.ViewGroup
import ph.ingenuity.tableview.adapter.ITableAdapter
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.feature.sort.RowHeaderSortHelper

/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.adapter.recyclerview <android-tableview-kotlin>
 */
class RowHeaderRecyclerViewAdapter(
        context: Context,
        items: List<Any>?,
        private val tableAdapter: ITableAdapter
) : AbstractRecyclerViewAdapter(context, items) {

    var rowHeaderSortHelper: RowHeaderSortHelper? = null
        get() {
            if (field == null) {
                field = RowHeaderSortHelper()
            }

            return field
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            tableAdapter.onCreateRowHeaderViewHolder(parent, viewType)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as AbstractViewHolder
        val value = getItem(position)
        tableAdapter.onBindRowHeaderViewHolder(viewHolder, value!!, position)
    }

    override fun getItemViewType(position: Int): Int =
            tableAdapter.getRowHeaderItemViewType(position)

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        val viewHolder = holder as AbstractViewHolder
        val selectionState = tableAdapter.tableView!!.selectionHandler
                .getRowSelectionState(holder.adapterPosition)

        // Update selection colors
        if (!tableAdapter.tableView!!.ignoreSelectionColors) {
            tableAdapter.tableView!!.selectionHandler
                    .changeRowBackgroundColorBySelectionStatus(viewHolder, selectionState)
        }

        // Update selection status
        viewHolder.setSelected(selectionState)
    }
}
