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

package ph.ingenuity.tableview.feature.filter

import android.text.TextUtils
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.feature.filter.FilterType.ALL
import ph.ingenuity.tableview.feature.filter.FilterType.COLUMN

/**
 * Created by jeremypacabis on March 01, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.feature.filter <android-tableview-kotlin>
 */
class Filter(
        private val tableView: ITableView
) {

    var filterItems: MutableList<FilterItem> = ArrayList()
        private set

    fun set(filter: String) {
        set(-1, filter)
    }

    fun set(column: Int, filter: String) {
        val filterItem = FilterItem(
                if (column == -1) ALL else COLUMN,
                column,
                filter
        )

        if (isAlreadyFiltering(column, filterItem)) {
            if (TextUtils.isEmpty(filter)) {
                remove(column, filterItem)
            } else {
                update(column, filterItem)
            }
        } else if (!TextUtils.isEmpty(filter)) {
            add(filterItem)
        }
    }

    private fun add(filterItem: FilterItem) {
        filterItems.add(filterItem)
        tableView.filter(this)
    }

    private fun remove(column: Int, filterItem: FilterItem) {
        val filterItemIterator = filterItems.iterator()
        while (filterItemIterator.hasNext()) {
            val item = filterItemIterator.next()
            if (column == -1 && item.filterType == filterItem.filterType) {
                filterItemIterator.remove()
                break
            } else if (item.column == filterItem.column) {
                filterItemIterator.remove()
                break
            }
        }

        tableView.filter(this)
    }

    private fun update(column: Int, filterItem: FilterItem) {
        val filterItemIterator = filterItems.iterator()
        while (filterItemIterator.hasNext()) {
            val item = filterItemIterator.next()
            if (column == -1 && item.filterType == filterItem.filterType) {
                filterItems[filterItems.indexOf(item)] = filterItem
                break
            } else if (item.column == filterItem.column) {
                filterItems[filterItems.indexOf(item)] = filterItem
                break
            }
        }

        tableView.filter(this)
    }

    private fun isAlreadyFiltering(column: Int, filterItem: FilterItem): Boolean {
        for (item in filterItems) {
            if (column == -1 && item.filterType == filterItem.filterType) {
                return true
            } else if (item.column == filterItem.column) {
                return true
            }
        }

        return false
    }
}
