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

package ph.ingenuity.tableview.menu

import android.view.Menu
import android.view.MenuItem
import android.widget.PopupMenu
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.R
import ph.ingenuity.tableview.viewholders.RandomDataRowHeaderViewHolder

/**
 * Created by jeremypacabis on March 06, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.menu <android-tableview-kotlin>
 */
class RowHeaderLongPressPopup(
        viewHolder: RandomDataRowHeaderViewHolder,
        private val tableView: ITableView
) : PopupMenu(
        viewHolder.itemView.context,
        viewHolder.itemView
), PopupMenu.OnMenuItemClickListener {

    private val context = viewHolder.itemView.context

    init {
        createMenuItems()
        setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            SCROLL_COLUMN -> tableView.scrollToColumn(50)
            SHOW_HIDE_COLUMN -> {
                if (tableView.isColumnVisible(SAMPLE_SHOW_HIDE_COLUMN)) {
                    tableView.hideColumn(SAMPLE_SHOW_HIDE_COLUMN)
                } else {
                    tableView.showColumn(SAMPLE_SHOW_HIDE_COLUMN)
                }
            }
        }

        return true
    }

    private fun createMenuItems() {
        menu.add(Menu.NONE, SCROLL_COLUMN, 0, context.getString(R.string.scroll_to_column_position))
        menu.add(Menu.NONE, SHOW_HIDE_COLUMN, 1, context.getString(R.string.show_hide_column))
    }

    companion object {
        const val SCROLL_COLUMN = 0
        const val SHOW_HIDE_COLUMN = 1
        const val SAMPLE_SHOW_HIDE_COLUMN = 5
    }
}