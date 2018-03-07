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

package ph.ingenuity.tableview.listener.click

import android.support.v7.widget.RecyclerView
import android.view.GestureDetector
import android.view.MotionEvent
import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView

/**
 * Created by jeremypacabis on February 28, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.listener.click <android-tableview-kotlin>
 */
abstract class AbstractItemClickListener(
        protected val recyclerView: CellRecyclerView,
        protected val tableView: ITableView
) : RecyclerView.OnItemTouchListener {

    protected var tableViewListener = tableView.tableViewListener

    protected var selectionHandler = tableView.selectionHandler

    protected var gestureDetector: GestureDetector

    init {
        gestureDetector = GestureDetector(
                recyclerView.context,
                object : GestureDetector.SimpleOnGestureListener() {

                    internal var start: MotionEvent? = null

                    override fun onSingleTapUp(e: MotionEvent): Boolean = true

                    override fun onDown(e: MotionEvent): Boolean {
                        start = e
                        return false
                    }

                    override fun onLongPress(e: MotionEvent) {
                        if (
                                start != null &&
                                Math.abs(start!!.rawX - e.rawX) < 20 &&
                                Math.abs(start!!.rawY - e.rawY) < 20
                        ) {
                            longPressAction(e)
                        }
                    }
                })
    }

    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean =
            clickAction(view, e)

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    protected abstract fun clickAction(view: RecyclerView, e: MotionEvent): Boolean

    protected abstract fun longPressAction(e: MotionEvent)
}
