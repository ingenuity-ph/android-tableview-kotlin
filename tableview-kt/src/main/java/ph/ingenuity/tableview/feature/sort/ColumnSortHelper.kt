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

package ph.ingenuity.tableview.feature.sort

import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractSorterViewHolder
import ph.ingenuity.tableview.feature.sort.SortState.UNSORTED
import ph.ingenuity.tableview.layoutmanager.ColumnHeaderLayoutManager
import java.util.*

/**
 * Created by jeremypacabis on March 01, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.feature.sort <android-tableview-kotlin>
 */
class ColumnSortHelper(private val columnHeaderLayoutManager: ColumnHeaderLayoutManager) {

    private val sortedColumns = ArrayList<Directive>()

    val isSorted: Boolean
        get() = sortedColumns.size != 0

    private fun sortingStatusChanged(column: Int, sortState: SortState) {
        val holder = columnHeaderLayoutManager.getViewHolder(column)
        when (holder) {
            is AbstractSorterViewHolder -> {
                holder.onSortingStatusChanged(sortState)
            }

            else -> {
//                throw TableViewSortException()
            }
        }
    }

    private fun getDirective(column: Int): Directive {
        return sortedColumns.indices
                .map { sortedColumns[it] }
                .firstOrNull { it.column == column }
                ?: EMPTY_DIRECTIVE
    }

    fun setSortingStatus(column: Int, status: SortState) {
        val directive = getDirective(column)
        if (directive !== EMPTY_DIRECTIVE) {
            sortedColumns.remove(directive)
        }

        if (status !== UNSORTED) {
            sortedColumns.add(Directive(column, status))
        }

        sortingStatusChanged(column, status)
    }

    fun clearSortingStatus() {
        sortedColumns.clear()
    }

    fun getSortingStatus(column: Int): SortState {
        return getDirective(column).direction
    }

    private data class Directive(val column: Int, val direction: SortState)

    companion object {
        private val EMPTY_DIRECTIVE = Directive(-1, UNSORTED)
    }
}
