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

package ph.ingenuity.tableview.handler

import ph.ingenuity.tableview.ITableView
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerViewAdapter
import ph.ingenuity.tableview.adapter.recyclerview.RowHeaderRecyclerViewAdapter
import ph.ingenuity.tableview.feature.filter.Filter
import ph.ingenuity.tableview.feature.filter.FilterChangedListener
import ph.ingenuity.tableview.feature.filter.FilterType
import ph.ingenuity.tableview.feature.filter.Filterable
import ph.ingenuity.tableview.listener.data.AdapterDataSetChangedListener
import java.util.*

/**
 * Created by jeremypacabis on March 01, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.handler <android-tableview-kotlin>
 */
class FilterHandler(tableView: ITableView) {

    private val rowHeaderRecyclerViewAdapter: RowHeaderRecyclerViewAdapter

    private val cellRecyclerViewAdapter: CellRecyclerViewAdapter

    private var filteredCellList: MutableList<List<Filterable>>? = null

    private var originalCellDataStore: List<List<Filterable>>? = null

    private var originalCellData: List<List<Filterable>>? = null

    private var filteredRowList: MutableList<Any>? = null

    private var originalRowDataStore: List<Any>? = null

    private var originalRowData: List<Any>? = null

    private var filterChangedListeners: MutableList<FilterChangedListener> = ArrayList()

    @Suppress("UNCHECKED_CAST")
    private val adapterDataSetChangedListener = object : AdapterDataSetChangedListener() {
        override fun onRowHeaderItemsChanged(rowHeaderItems: List<Any>?) {
            if (rowHeaderItems != null) {
                originalRowDataStore = ArrayList(rowHeaderItems)
            }
        }

        override fun onCellItemsChanged(cellItems: List<Any>?) {
            if (cellItems != null) {
                originalCellDataStore = ArrayList(cellItems) as List<List<Filterable>>
            }
        }
    }

    init {
        tableView.adapter!!.addAdapterDataSetChangedListener(adapterDataSetChangedListener)
        cellRecyclerViewAdapter = tableView.cellRecyclerView.adapter as CellRecyclerViewAdapter
        rowHeaderRecyclerViewAdapter =
                tableView.rowHeaderRecyclerView.adapter as RowHeaderRecyclerViewAdapter
    }

    fun filter(filter: Filter) {
        if (originalCellDataStore == null || originalRowDataStore == null) {
            return
        }

        originalCellData = ArrayList<List<Filterable>>(originalCellDataStore!!)
        originalRowData = ArrayList(originalRowDataStore!!)
        filteredCellList = ArrayList()
        filteredRowList = ArrayList()

        if (filter.filterItems.isEmpty()) {
            filteredCellList = ArrayList(originalCellDataStore!!)
            filteredRowList = ArrayList(originalRowDataStore!!)
            dispatchFilterClearedToListeners(
                    originalCellDataStore as List<List<Filterable>>,
                    originalRowDataStore as List<Any>
            )
        } else {
            var x = 0
            while (x < filter.filterItems.size) {
                val filterItem = filter.filterItems[x]
                if (filterItem.filterType == FilterType.ALL) {
                    for (itemsList in originalCellData!!) {
                        for (item in itemsList) {
                            if (item
                                            .filterableKeyword
                                            .toLowerCase()
                                            .contains(filterItem
                                                    .filter
                                                    .toLowerCase())) {
                                filteredCellList!!.add(itemsList)
                                filteredRowList!!.add(
                                        originalRowData!![filteredCellList!!.indexOf(itemsList)]
                                )
                                break
                            }
                        }
                    }
                } else {
                    for (itemsList in originalCellData!!) {
                        if (itemsList[filterItem.column]
                                        .filterableKeyword
                                        .toLowerCase()
                                        .contains(filterItem
                                                .filter
                                                .toLowerCase())) {
                            filteredCellList!!.add(itemsList)
                            filteredRowList!!.add(
                                    originalRowData!![filteredCellList!!.indexOf(itemsList)]
                            )
                        }
                    }
                }

                // If this is the last filter to be processed,
                // the filtered lists will not be cleared
                if (++x < filter.filterItems.size) {
                    originalCellData = ArrayList<List<Filterable>>(filteredCellList!!)
                    originalRowData = ArrayList(filteredRowList!!)
                    filteredCellList!!.clear()
                    filteredRowList!!.clear()
                }
            }
        }

        // Sets the filtered data to the TableView
        rowHeaderRecyclerViewAdapter.setItems(filteredRowList as List<Any>, true)
        cellRecyclerViewAdapter.setItems(filteredCellList as List<Any>, true)

        // Tells the listeners that the TableView has been filtered
        dispatchFilterChangedToListeners(
                filteredCellList as List<List<Filterable>>,
                filteredRowList as List<Any>
        )
    }

    private fun dispatchFilterChangedToListeners(
            filteredCellItems: List<List<Filterable>>,
            filteredRowHeaderItems: List<Any>
    ) {
        filterChangedListeners.forEach {
            it.onFilterChanged(
                    filteredCellItems,
                    filteredRowHeaderItems
            )
        }
    }

    private fun dispatchFilterClearedToListeners(
            originalCellItems: List<List<Filterable>>,
            originalRowHeaderItems: List<Any>
    ) {
        filterChangedListeners.forEach {
            it.onFilterCleared(
                    originalCellItems,
                    originalRowHeaderItems
            )
        }
    }

    fun addFilterChangedListener(listener: FilterChangedListener) {
        filterChangedListeners.add(listener)
    }
}
