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
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder.SelectionState
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder.SelectionState.*

/**
 * Created by jeremypacabis on February 28, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.handler <android-tableview-kotlin>
 */
class SelectionHandler(private val tableView: ITableView) {

    private val columnHeaderRecyclerView: CellRecyclerView = tableView.columnHeaderRecyclerView

    private val rowHeaderRecyclerView: CellRecyclerView = tableView.rowHeaderRecyclerView

    private var previousSelectedViewHolder: AbstractViewHolder? = null

    var selectedRowPosition = UNSELECTED_POSITION
        private set

    var selectedColumnPosition = UNSELECTED_POSITION
        private set

    var shadowEnabled = true

    val anyColumnSelected: Boolean
        get() = selectedColumnPosition != UNSELECTED_POSITION &&
                selectedRowPosition == UNSELECTED_POSITION

    fun setSelectedCellPositions(selectedViewHolder: AbstractViewHolder, column: Int, row: Int) {
        setPreviousSelectedView(selectedViewHolder)
        selectedColumnPosition = column
        selectedRowPosition = row
        if (shadowEnabled) {
            selectedCellView()
        }
    }

    fun setSelectedColumnPosition(selectedViewHolder: AbstractViewHolder, column: Int) {
        setPreviousSelectedView(selectedViewHolder)
        selectedColumnPosition = column
        selectedColumnHeader()
        selectedRowPosition = UNSELECTED_POSITION
    }

    fun setSelectedRowPosition(selectedViewHolder: AbstractViewHolder, row: Int) {
        setPreviousSelectedView(selectedViewHolder)
        selectedRowPosition = row
        selectedRowHeader()
        selectedColumnPosition = UNSELECTED_POSITION
    }

    private fun setPreviousSelectedView(viewHolder: AbstractViewHolder) {
        restorePreviousSelectedView()
        if (previousSelectedViewHolder != null) {
            previousSelectedViewHolder!!.setBackgroundColor(tableView.unselectedColor)
            previousSelectedViewHolder!!.setSelected(UNSELECTED)
        }

        val oldViewHolder = tableView
                .cellLayoutManager.getCellViewHolder(selectedColumnPosition, selectedRowPosition)

        if (oldViewHolder != null) {
            oldViewHolder.setBackgroundColor(tableView.unselectedColor)
            oldViewHolder.setSelected(UNSELECTED)
        }

        previousSelectedViewHolder = viewHolder
        previousSelectedViewHolder!!.setBackgroundColor(tableView.selectedColor)
        previousSelectedViewHolder!!.setSelected(SELECTED)
    }

    private fun restorePreviousSelectedView() {
        if (
                selectedColumnPosition != UNSELECTED_POSITION &&
                selectedRowPosition != UNSELECTED_POSITION
        ) {
            unselectedCellView()
        } else if (selectedColumnPosition != UNSELECTED_POSITION) {
            unselectedColumnHeader()
        } else if (selectedRowPosition != UNSELECTED_POSITION) {
            unselectedRowHeader()
        }
    }

    private fun selectedRowHeader() {
        changeVisibleCellViewsBackgroundForRow(selectedRowPosition, true)
        if (shadowEnabled) {
            tableView.columnHeaderRecyclerView
                    .setSelected(
                            SHADOWED,
                            tableView.shadowColor,
                            false
                    )
        }
    }

    private fun unselectedRowHeader() {
        changeVisibleCellViewsBackgroundForRow(selectedRowPosition, false)
        tableView.columnHeaderRecyclerView.setSelected(
                UNSELECTED,
                tableView.unselectedColor,
                false
        )
    }

    private fun selectedCellView() {
        val shadowColor = tableView.shadowColor
        val rowHeader = rowHeaderRecyclerView
                .findViewHolderForAdapterPosition(selectedRowPosition) as AbstractViewHolder?
        val columnHeader = columnHeaderRecyclerView
                .findViewHolderForAdapterPosition(selectedColumnPosition) as AbstractViewHolder?

        columnHeader?.setBackgroundColor(shadowColor)
        rowHeader?.setBackgroundColor(shadowColor)
        columnHeader?.setSelected(SHADOWED)
        rowHeader?.setSelected(SHADOWED)
    }

    private fun unselectedCellView() {
        val unSelectedColor = tableView.unselectedColor
        val rowHeader = rowHeaderRecyclerView
                .findViewHolderForAdapterPosition(selectedRowPosition) as AbstractViewHolder?
        val columnHeader = columnHeaderRecyclerView
                .findViewHolderForAdapterPosition(selectedColumnPosition) as AbstractViewHolder?

        columnHeader?.setBackgroundColor(unSelectedColor)
        rowHeader?.setBackgroundColor(unSelectedColor)
        columnHeader?.setSelected(UNSELECTED)
        rowHeader?.setSelected(UNSELECTED)
    }

    private fun selectedColumnHeader() {
        changeVisibleCellViewsBackgroundForColumn(selectedColumnPosition, true)
        tableView.rowHeaderRecyclerView.setSelected(
                SHADOWED,
                tableView.shadowColor,
                false
        )
    }

    private fun unselectedColumnHeader() {
        changeVisibleCellViewsBackgroundForColumn(selectedColumnPosition, false)
        tableView.rowHeaderRecyclerView.setSelected(
                UNSELECTED,
                tableView.unselectedColor,
                false
        )
    }

    private fun isCellSelected(column: Int, row: Int): Boolean =
            selectedColumnPosition == column &&
                    selectedRowPosition == row ||
                    isColumnSelected(column) ||
                    isRowSelected(row)

    private fun isColumnShadowed(column: Int): Boolean =
            selectedColumnPosition == column &&
                    selectedRowPosition != UNSELECTED_POSITION ||
                    selectedColumnPosition == UNSELECTED_POSITION &&
                    selectedRowPosition != UNSELECTED_POSITION

    private fun isColumnSelected(column: Int): Boolean =
            selectedColumnPosition == column &&
                    selectedRowPosition == UNSELECTED_POSITION

    private fun isRowShadowed(row: Int): Boolean =
            selectedRowPosition == row &&
                    selectedColumnPosition != UNSELECTED_POSITION ||
                    selectedRowPosition == UNSELECTED_POSITION &&
                    selectedColumnPosition != UNSELECTED_POSITION

    fun isRowSelected(row: Int): Boolean =
            selectedRowPosition == row &&
                    selectedColumnPosition == UNSELECTED_POSITION

    fun getCellSelectionState(column: Int, row: Int): SelectionState =
            if (isCellSelected(column, row)) {
                SELECTED
            } else UNSELECTED

    fun getColumnSelectionState(column: Int): SelectionState = when {
        isColumnShadowed(column) -> SHADOWED
        isColumnSelected(column) -> SELECTED
        else -> UNSELECTED
    }

    fun getRowSelectionState(row: Int): SelectionState = when {
        isRowShadowed(row) -> SHADOWED
        isRowSelected(row) -> SELECTED
        else -> UNSELECTED
    }

    private fun changeVisibleCellViewsBackgroundForRow(row: Int, isSelected: Boolean) {
        val selectedColor = tableView.selectedColor
        val unSelectedColor = tableView.unselectedColor
        val recyclerView = tableView.cellLayoutManager.findViewByPosition(row) as CellRecyclerView?
        recyclerView?.setSelected(
                if (isSelected) SELECTED else UNSELECTED,
                if (isSelected) selectedColor else unSelectedColor,
                false
        )
    }

    private fun changeVisibleCellViewsBackgroundForColumn(column: Int, isSelected: Boolean) {
        val selectedColor = tableView.selectedColor
        val unSelectedColor = tableView.unselectedColor
        val visibleCellViews = tableView.cellLayoutManager.getVisibleCellViewsByColumnPosition(column)
        visibleCellViews.forEach { viewHolder ->
            if (viewHolder != null) {
                viewHolder.setBackgroundColor(if (isSelected) selectedColor else unSelectedColor)
                viewHolder.setSelected(if (isSelected) SELECTED else UNSELECTED)
            }
        }
    }

    fun changeRowBackgroundColorBySelectionStatus(
            viewHolder: AbstractViewHolder,
            selectionState: SelectionState
    ) {
        if (shadowEnabled && selectionState == SHADOWED) {
            viewHolder.setBackgroundColor(tableView.shadowColor)
        } else if (selectionState == SELECTED) {
            viewHolder.setBackgroundColor(tableView.selectedColor)
        } else {
            viewHolder.setBackgroundColor(tableView.unselectedColor)
        }
    }

    fun changeColumnBackgroundColorBySelectionStatus(
            viewHolder: AbstractViewHolder,
            selectionState: SelectionState
    ) {
        if (shadowEnabled && selectionState == SHADOWED) {
            viewHolder.setBackgroundColor(tableView.shadowColor)
        } else if (selectionState == SELECTED) {
            viewHolder.setBackgroundColor(tableView.selectedColor)
        } else {
            viewHolder.setBackgroundColor(tableView.unselectedColor)
        }
    }

    fun clearSelection() {
        unselectedColumnHeader()
        unselectedRowHeader()
        unselectedCellView()
    }

    companion object {
        const val UNSELECTED_POSITION = -1
    }
}
