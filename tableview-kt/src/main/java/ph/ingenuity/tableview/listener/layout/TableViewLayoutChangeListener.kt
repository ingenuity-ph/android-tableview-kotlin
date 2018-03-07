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

package ph.ingenuity.tableview.listener.layout

import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView
import ph.ingenuity.tableview.layoutmanager.CellLayoutManager

/**
 * Created by jeremypacabis on February 28, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.listener.layout <android-tableview-kotlin>
 */
class TableViewLayoutChangeListener(tableView: ITableView) : View.OnLayoutChangeListener {

    private val cellRecyclerView: CellRecyclerView = tableView.cellRecyclerView

    private val columnHeaderRecyclerView: CellRecyclerView = tableView.columnHeaderRecyclerView

    private val cellLayoutManager: CellLayoutManager = tableView.cellLayoutManager

    override fun onLayoutChange(
            v: View,
            left: Int,
            top: Int,
            right: Int,
            bottom: Int,
            oldLeft: Int,
            oldTop: Int,
            oldRight: Int,
            oldBottom: Int
    ) {
        if (v.isShown && right - left != oldRight - oldLeft) {
            if (columnHeaderRecyclerView.width > cellRecyclerView.width) {
                cellLayoutManager.remeasureAllChild()
            } else if (cellRecyclerView.width > columnHeaderRecyclerView.width) {
                columnHeaderRecyclerView.layoutParams.width = WRAP_CONTENT
                columnHeaderRecyclerView.requestLayout()
            }
        }
    }
}
