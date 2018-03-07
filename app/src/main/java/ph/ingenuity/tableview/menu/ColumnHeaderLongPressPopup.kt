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
import ph.ingenuity.tableview.R
import ph.ingenuity.tableview.TableView
import ph.ingenuity.tableview.feature.sort.SortState
import ph.ingenuity.tableview.viewholders.RandomDataColumnHeaderViewHolder

/**
 * Created by jeremypacabis on March 05, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.menu <android-tableview-kotlin>
 */
class ColumnHeaderLongPressPopup(
        viewHolder: RandomDataColumnHeaderViewHolder,
        private val tableView: TableView
) : PopupMenu(
        viewHolder.itemView.context,
        viewHolder.itemView
), PopupMenu.OnMenuItemClickListener {

    private val context = viewHolder.itemView.context

    private val xPosition = viewHolder.adapterPosition

    init {
        createMenuItems()
        changeMenuItemsVisibility()
        setOnMenuItemClickListener(this)
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        when (item?.itemId) {
            ASCENDING -> tableView.sortColumn(xPosition, SortState.ASCENDING)
            DESCENDING -> tableView.sortColumn(xPosition, SortState.DESCENDING)
            HIDE_ROW -> tableView.hideRow(3)
            SHOW_ROW -> tableView.showRow(3)
            SCROLL_ROW -> tableView.scrollToRow(100)
        }

        return true
    }

    private fun createMenuItems() {
        menu.add(Menu.NONE, ASCENDING, 0, context.getString(R.string.sort_ascending))
        menu.add(Menu.NONE, DESCENDING, 1, context.getString(R.string.sort_descending))
        menu.add(Menu.NONE, HIDE_ROW, 2, context.getString(R.string.hide_row))
        menu.add(Menu.NONE, SHOW_ROW, 3, context.getString(R.string.show_row))
        menu.add(Menu.NONE, SCROLL_ROW, 4, context.getString(R.string.scroll_to_row_position))
    }

    private fun changeMenuItemsVisibility() {
        val sortState = tableView.getColumnSortState(xPosition)
        when (sortState) {
            SortState.DESCENDING -> menu.getItem(1).isVisible = false
            SortState.ASCENDING -> menu.getItem(0).isVisible = false
            SortState.UNSORTED -> {
            }
        }
    }

    companion object {
        const val ASCENDING = 0
        const val DESCENDING = 1
        const val HIDE_ROW = 2
        const val SHOW_ROW = 3
        const val SCROLL_ROW = 4
    }
}