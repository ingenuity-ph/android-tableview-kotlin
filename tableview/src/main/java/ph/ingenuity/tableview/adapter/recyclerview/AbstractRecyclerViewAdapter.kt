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

package ph.ingenuity.tableview.adapter.recyclerview

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.NO_POSITION
import java.util.*

/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.adapter.recyclerview <android-tableview-kotlin>
 */
abstract class AbstractRecyclerViewAdapter(
        protected var context: Context,
        items: List<Any>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var items: MutableList<Any>? = mutableListOf()
        set(value) {
            if (value != null) {
                field = ArrayList(value)
                notifyDataSetChanged()
            }
        }

    init {
        if (items!!.isNotEmpty()) {
            this.items = items as MutableList<Any>?
        }
    }

    override fun getItemCount(): Int = items!!.size

    fun setItems(itemList: List<Any>, notifyDataSet: Boolean) {
        items = ArrayList(itemList)
        if (notifyDataSet) {
            notifyDataSetChanged()
        }
    }

    fun getItem(position: Int): Any? {
        return if (
                items == null ||
                items!!.isEmpty() ||
                position < 0 ||
                position >= items!!.size
        ) {
            null
        } else {
            items!![position]
        }
    }

    fun deleteItem(position: Int) {
        if (position != NO_POSITION) {
            items!!.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun deleteItemRange(position: Int, itemCount: Int) {
        (position until position + itemCount + 1)
                .filter { it != NO_POSITION }
                .forEach { items!!.removeAt(it) }
        notifyItemRangeRemoved(position, itemCount)
    }

    fun addItem(position: Int, item: Any?) {
        if (position != NO_POSITION && item != null) {
            items!!.add(position, item)
            notifyItemInserted(position)
        }
    }

    fun addItemRange(position: Int, items: List<*>?) {
        if (items != null) {
            items.indices.forEach { i ->
                if (i != NO_POSITION) {
                    items[i]?.let { this.items!!.add(i + position, it) }
                }
            }

            notifyItemRangeInserted(
                    position,
                    items.size
            )
        }
    }

    fun changeItem(position: Int, item: Any?) {
        if (position != NO_POSITION && item != null) {
            items!![position] = item
            notifyItemChanged(position)
        }
    }

    fun changeItemRange(position: Int, itemList: List<Any>?) {
        if (items!!.size > position + itemList!!.size) {
            itemList.indices
                    .filter { it != NO_POSITION }
                    .forEach { items!![it + position] = itemList[it] }

            notifyItemRangeChanged(
                    position,
                    itemList.size
            )
        }
    }
}