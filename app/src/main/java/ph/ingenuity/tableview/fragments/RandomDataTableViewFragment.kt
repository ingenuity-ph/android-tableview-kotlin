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

package ph.ingenuity.tableview.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ph.ingenuity.tableview.R
import ph.ingenuity.tableview.TableView
import ph.ingenuity.tableview.adapters.RandomDataTableViewAdapter
import ph.ingenuity.tableview.data.RandomDataFactory
import ph.ingenuity.tableview.listeners.TableViewListener

/**
 * Created by jeremypacabis on March 05, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.fragments <android-tableview-kotlin>
 */
class RandomDataTableViewFragment : Fragment() {

    private lateinit var tableView: TableView

    private var mainView: View? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        if (mainView != null) {
            var parent = mainView!!.parent as ViewGroup?
            if (parent == null) {
                parent = container
            }

            parent!!.removeView(mainView)
            return mainView as View
        }

        mainView = inflater.inflate(R.layout.fragment_random_data_table, container, false)
        initializeViews()
        initializeData()
        return mainView as View
    }

    private fun initializeViews() {
        tableView = mainView!!.findViewById(R.id.random_data_tableview)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeData() {
        val randomDataFactory = RandomDataFactory(500, 500)
        val tableAdapter = RandomDataTableViewAdapter(mainView!!.context)
        val cellsList = randomDataFactory.randomCellsList as List<List<Any>>
        val rowHeadersList = randomDataFactory.randomRowHeadersList as List<Any>
        val columnHeadersList = randomDataFactory.randomColumnHeadersList as List<Any>
        tableView.adapter = tableAdapter
        tableView.tableViewListener = TableViewListener(tableView)
        tableAdapter.setAllItems(cellsList, columnHeadersList, rowHeadersList)
    }

    companion object {
        fun newInstance(context: Context): RandomDataTableViewFragment {
            val bundle = Bundle()
            val fragment = RandomDataTableViewFragment()
            bundle.putString("title", context.resources.getStringArray(R.array.table_view_demos)[0])
            fragment.arguments = bundle
            return fragment
        }
    }
}