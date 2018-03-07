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

package ph.ingenuity.tableview.data

import ph.ingenuity.tableview.models.RandomDataCell
import java.util.*

/**
 * Created by jeremypacabis on March 02, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.data <android-tableview-kotlin>
 */
class RandomDataFactory(
        numColumns: Int,
        numRows: Int
) {

    var randomCellsList = mutableListOf<Any>()
        private set

    var randomColumnHeadersList = mutableListOf<Any>()
        private set

    var randomRowHeadersList = mutableListOf<Any>()
        private set

    init {
        (0 until numRows).forEach {
            randomRowHeadersList.add(RandomDataCell("""Row $it"""))
        }

        (0 until numColumns).forEach {
            randomColumnHeadersList.add(RandomDataCell("""COLUMN $it"""))
        }

        (0 until numRows).forEach { row ->
            val cellList = mutableListOf<Any>()
            (0 until numColumns).forEach { column ->
                val data: Any
                val random = Random().nextInt()
                data = when (column) {
                    0 -> column
                    1 -> random
                    else -> """Cell $column $row"""
                }

                cellList.add(RandomDataCell(data, """$column-$row"""))
            }

            randomCellsList.add(cellList)
        }
    }
}