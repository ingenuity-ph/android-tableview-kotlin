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

import ph.ingenuity.tableview.feature.sort.SortState.DESCENDING
import java.util.*

/**
 * Created by jeremypacabis on March 01, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.feature.sort <android-tableview-kotlin>
 */
class ColumnSortComparator(
        private val xPosition: Int,
        sortState: SortState
) : AbstractSortComparator(), Comparator<List<Sortable>> {

    init {
        this.sortState = sortState
    }

    override fun compare(t1: List<Sortable>, t2: List<Sortable>): Int {
        val o1 = t1[xPosition].content
        val o2 = t2[xPosition].content
        return when (sortState) {
            DESCENDING -> compareContent(o2, o1)
            else -> compareContent(o1, o2)
        }
    }
}