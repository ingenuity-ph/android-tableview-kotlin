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
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.*
import ph.ingenuity.tableview.R
import ph.ingenuity.tableview.TableView
import ph.ingenuity.tableview.adapters.RandomDataTableViewAdapter
import ph.ingenuity.tableview.data.RandomDataFactory
import ph.ingenuity.tableview.feature.filter.Filter
import ph.ingenuity.tableview.feature.pagination.Pagination

/**
 * Created by jeremypacabis on March 05, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.fragments <android-tableview-kotlin>
 */
class RandomDataTableViewWithControlsFragment : Fragment() {

    private lateinit var tableView: TableView

    private lateinit var pageNumberField: EditText

    private lateinit var searchField: EditText

    private lateinit var genderFilter: Spinner

    private lateinit var itemsPerPage: Spinner

    private lateinit var moodFilter: Spinner

    private lateinit var previousButton: ImageButton

    private lateinit var nextButton: ImageButton

    private lateinit var tablePaginationDetails: TextView

    private lateinit var pagination: Pagination

    private lateinit var filter: Filter

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

        mainView = inflater.inflate(R.layout.fragment_random_data_table_with_controls, container, false)
        initializeViews()
        initializeData()
        initializeListeners()
        return mainView as View
    }

    private fun initializeViews() {
        tableView = mainView!!.findViewById(R.id.random_data_tableview)
        itemsPerPage = mainView!!.findViewById(R.id.items_per_page_spinner)
        genderFilter = mainView!!.findViewById(R.id.gender_spinner)
        moodFilter = mainView!!.findViewById(R.id.mood_spinner)
        searchField = mainView!!.findViewById(R.id.query_string)
        previousButton = mainView!!.findViewById(R.id.previous_button)
        nextButton = mainView!!.findViewById(R.id.next_button)
        pageNumberField = mainView!!.findViewById(R.id.page_number_text)
        tablePaginationDetails = mainView!!.findViewById(R.id.table_details)
    }

    private fun initializeListeners() {
        itemsPerPage.onItemSelectedListener = onItemsPerPageSelectedListener
        genderFilter.onItemSelectedListener = onGenderSelectedListener
        moodFilter.onItemSelectedListener = onMoodSelectedListener
        searchField.addTextChangedListener(onSearchTextChange)
        previousButton.setOnClickListener(onPreviousPageButtonClicked)
        nextButton.setOnClickListener(onNextPageButtonClicked)
        pageNumberField.addTextChangedListener(onPageTextChanged)
        pagination.onTableViewPageTurnedListener = onTableViewPageTurnedListener
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeData() {
        val randomDataFactory = RandomDataFactory(500, 500)
        val tableAdapter = RandomDataTableViewAdapter(mainView!!.context)
        val cellsList = randomDataFactory.randomCellsList as List<List<Any>>
        val rowHeadersList = randomDataFactory.randomRowHeadersList as List<Any>
        val columnHeadersList = randomDataFactory.randomColumnHeadersList as List<Any>
        tableView.adapter = tableAdapter
        tableAdapter.setAllItems(cellsList, columnHeadersList, rowHeadersList)
        pagination = Pagination(tableView)
        filter = Filter(tableView)
    }

    private val onSearchTextChange = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            filter.set(s.toString())
        }
    }

    private val onPageTextChanged = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val page: Int = if (TextUtils.isEmpty(s)) 1 else Integer.valueOf(s.toString())
            pagination.loadPage(page)
        }
    }

    private val onGenderSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val genderFilter = when (parent?.getItemAtPosition(position) as String) {
                "" -> ""
                "Male" -> "boy"
                "Female" -> "girl"
                else -> ""
            }

            filter.set(1, genderFilter)
        }
    }

    private val onMoodSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            filter.set(2, parent?.getItemAtPosition(position) as String)
        }
    }

    private val onItemsPerPageSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val numItems = when (parent!!.getItemAtPosition(position) as String) {
                "All" -> 0
                else -> Integer.valueOf(parent.getItemAtPosition(position) as String)
            }

            pagination.itemsPerPage = numItems
        }
    }

    private val onPreviousPageButtonClicked = View.OnClickListener {
        pagination.loadPreviousPage()
    }

    private val onNextPageButtonClicked = View.OnClickListener {
        pagination.loadNextPage()
    }

    private val onTableViewPageTurnedListener =
            object : Pagination.OnTableViewPageTurnedListener {
                override fun onPageTurned(numItems: Int, itemsStart: Int, itemsEnd: Int) {
                    val currentPage = pagination.currentPage
                    val pageCount = pagination.pageCount
                    previousButton.visibility = VISIBLE
                    nextButton.visibility = VISIBLE

                    if (currentPage == 1 && pageCount == 1) {
                        previousButton.visibility = INVISIBLE
                        nextButton.visibility = INVISIBLE
                    }

                    if (currentPage == 1) {
                        previousButton.visibility = INVISIBLE
                    }

                    if (currentPage == pageCount) {
                        nextButton.visibility = INVISIBLE
                    }

                    tablePaginationDetails.text = getString(
                            R.string.table_pagination_details,
                            currentPage,
                            itemsStart,
                            itemsEnd
                    )
                }
            }

    companion object {
        fun newInstance(context: Context): RandomDataTableViewWithControlsFragment {
            val bundle = Bundle()
            val fragment = RandomDataTableViewWithControlsFragment()
            bundle.putString("title", context.resources.getStringArray(R.array.table_view_demos)[1])
            fragment.arguments = bundle
            return fragment
        }
    }
}