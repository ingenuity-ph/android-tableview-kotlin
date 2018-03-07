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

import java.util.*

/**
 * Created by jeremypacabis on March 01, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.feature.sort <android-tableview-kotlin>
 */
abstract class AbstractSortComparator {

    protected var sortState: SortState? = null

    @Suppress("UNCHECKED_CAST")
    fun compareContent(o1: Any?, o2: Any?): Int {
        if (o1 == null && o2 == null) {
            return 0
        } else if (o1 == null) {
            return -1
        } else if (o2 == null) {
            return 1
        } else {
            val type = o1.javaClass
            return when {
                Comparable::class.java.isAssignableFrom(type) -> {
                    (o1 as Comparable<Any>).compareTo(o2)
                }

                Number::class.java.isAssignableFrom(type.superclass) -> {
                    compare(
                            o1 as Number,
                            o2 as Number
                    )
                }

                type == String::class.java -> {
                    (o1 as String).compareTo((o2 as String?)!!)
                }

                type == Date::class.java -> {
                    compare(
                            o1 as Date,
                            o2 as Date
                    )
                }

                type == Boolean::class.java -> {
                    compare(
                            o1 as Boolean?,
                            o2 as Boolean?
                    )
                }

                else -> {
                    (o1 as String).compareTo((o2 as String?)!!)
                }
            }
        }
    }

    private fun compare(o1: Number, o2: Number): Int {
        val n1 = o1.toDouble()
        val n2 = o2.toDouble()
        return when {
            n1 < n2 -> -1
            n1 > n2 -> 1
            else -> 0
        }
    }

    private fun compare(o1: Date, o2: Date): Int {
        val n1 = o1.time
        val n2 = o2.time
        return when {
            n1 < n2 -> -1
            n1 > n2 -> 1
            else -> 0
        }
    }

    private fun compare(o1: Boolean?, o2: Boolean?): Int {
        val b1 = o1!!
        val b2 = o2!!
        return when {
            b1 == b2 -> 0
            b1 -> 1
            else -> -1
        }
    }
}
