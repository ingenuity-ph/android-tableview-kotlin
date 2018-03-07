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

package ph.ingenuity.tableview.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ph.ingenuity.tableview.R
import ph.ingenuity.tableview.adapter.AbstractTableAdapter
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.models.RandomDataCell
import ph.ingenuity.tableview.viewholders.RandomDataCellViewHolder
import ph.ingenuity.tableview.viewholders.RandomDataColumnHeaderViewHolder
import ph.ingenuity.tableview.viewholders.RandomDataRowHeaderViewHolder

/**
 * Created by jeremypacabis on March 02, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.adapters <android-tableview-kotlin>
 */
class RandomDataTableViewAdapter(private val context: Context) : AbstractTableAdapter(context) {

    override fun getColumnHeaderItemViewType(column: Int): Int = 0

    override fun getRowHeaderItemViewType(row: Int): Int = 0

    override fun getCellItemViewType(column: Int): Int = 0

    override fun onCreateCellViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): RecyclerView.ViewHolder {
        val cellView = LayoutInflater.from(context).inflate(
                R.layout.table_cell_text_data,
                parent,
                false
        )

        return RandomDataCellViewHolder(cellView)
    }

    override fun onBindCellViewHolder(
            holder: AbstractViewHolder,
            cellItem: Any,
            column: Int,
            row: Int
    ) {
        val cell = cellItem as RandomDataCell
        val cellViewHolder = holder as RandomDataCellViewHolder
        cellViewHolder.cellTextView.text = cell.content.toString()
    }

    override fun onCreateColumnHeaderViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): RecyclerView.ViewHolder {
        val columnHeaderView = LayoutInflater.from(context).inflate(
                R.layout.table_column_header_text_data,
                parent,
                false
        )

        return RandomDataColumnHeaderViewHolder(columnHeaderView)
    }

    override fun onBindColumnHeaderViewHolder(
            holder: AbstractViewHolder,
            columnHeaderItem: Any,
            column: Int
    ) {
        val columnHeaderCell = columnHeaderItem as RandomDataCell
        val columnHeaderViewHolder = holder as RandomDataColumnHeaderViewHolder
        columnHeaderViewHolder.cellTextView.text = columnHeaderCell.content.toString()
    }

    override fun onCreateRowHeaderViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): RecyclerView.ViewHolder {
        val rowHeaderView = LayoutInflater.from(context).inflate(
                R.layout.table_row_header_text_data,
                parent,
                false
        )

        return RandomDataRowHeaderViewHolder(rowHeaderView)
    }

    override fun onBindRowHeaderViewHolder(
            holder: AbstractViewHolder,
            rowHeaderItem: Any,
            row: Int
    ) {
        val rowHeaderCell = rowHeaderItem as RandomDataCell
        val rowHeaderViewHolder = holder as RandomDataRowHeaderViewHolder
        rowHeaderViewHolder.cellTextView.text = rowHeaderCell.content.toString()
    }

    override fun onCreateCornerView(): View? {
        val cornerView = LayoutInflater.from(context).inflate(R.layout.table_corner_view, null)
        cornerView.setOnClickListener {
            Toast.makeText(context, "CornerView has been clicked.", Toast.LENGTH_SHORT).show()
        }

        return cornerView
    }
}