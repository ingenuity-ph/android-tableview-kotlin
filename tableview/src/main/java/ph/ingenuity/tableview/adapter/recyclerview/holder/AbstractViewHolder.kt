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

package ph.ingenuity.tableview.adapter.recyclerview.holder

import android.support.annotation.ColorInt
import android.support.v7.widget.RecyclerView
import android.view.View
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder.SelectionState.*

/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.adapter.recyclerview.holder <android-tableview-kotlin>
 */
abstract class AbstractViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private var selectionState = UNSELECTED

    val isSelected: Boolean
        get() = selectionState == SELECTED

    val isShadowed: Boolean
        get() = selectionState == SHADOWED

    fun setSelected(selectionState: SelectionState) {
        this.selectionState = selectionState
        when (selectionState) {
            SELECTED -> itemView.isSelected = true
            UNSELECTED -> itemView.isSelected = false
            else -> {
                itemView.isSelected = false
            }
        }
    }

    fun setBackgroundColor(@ColorInt color: Int) {
        itemView.setBackgroundColor(color)
    }

    enum class SelectionState {
        SELECTED, UNSELECTED, SHADOWED
    }
}
