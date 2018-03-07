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

package ph.ingenuity.tableview.listeners

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.widget.Toast
import ph.ingenuity.tableview.TableView
import ph.ingenuity.tableview.listener.ITableViewListener
import ph.ingenuity.tableview.menu.ColumnHeaderLongPressPopup
import ph.ingenuity.tableview.menu.RowHeaderLongPressPopup
import ph.ingenuity.tableview.viewholders.RandomDataColumnHeaderViewHolder
import ph.ingenuity.tableview.viewholders.RandomDataRowHeaderViewHolder

/**
 * Created by jeremypacabis on March 05, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.listeners <android-tableview-kotlin>
 */
class TableViewListener(private val tableView: TableView) : ITableViewListener {

    private var toast: Toast? = null

    private val context: Context = tableView.context

    override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
        showToast("Cell $column $row has been clicked.")
    }

    override fun onCellLongPressed(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
        showToast("Cell $column, $row has been long pressed.")
    }

    override fun onColumnHeaderClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
        showToast("Column header $column has been clicked.")
    }

    override fun onColumnHeaderLongPressed(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
        if (columnHeaderView is RandomDataColumnHeaderViewHolder) {
            val popup = ColumnHeaderLongPressPopup(columnHeaderView, tableView)
            popup.show()
        }
    }

    override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
        showToast("Row header $row has been clicked.")
    }

    override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
        if (rowHeaderView is RandomDataRowHeaderViewHolder) {
            val popup = RowHeaderLongPressPopup(rowHeaderView, tableView)
            popup.show()
        }
    }

    private fun showToast(message: String) {
        if (toast == null) {
            toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
        }

        toast!!.setText(message)
        toast!!.show()
    }
}
