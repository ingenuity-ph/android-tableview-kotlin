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

package ph.ingenuity.tableview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PorterDuff
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getColor
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearLayoutManager.VERTICAL
import android.util.AttributeSet
import android.widget.FrameLayout
import ph.ingenuity.tableview.adapter.AbstractTableAdapter
import ph.ingenuity.tableview.adapter.recyclerview.CellRecyclerView
import ph.ingenuity.tableview.adapter.recyclerview.holder.AbstractViewHolder
import ph.ingenuity.tableview.feature.filter.Filter
import ph.ingenuity.tableview.feature.sort.SortState
import ph.ingenuity.tableview.handler.*
import ph.ingenuity.tableview.layoutmanager.CellLayoutManager
import ph.ingenuity.tableview.layoutmanager.ColumnHeaderLayoutManager
import ph.ingenuity.tableview.listener.ITableViewListener
import ph.ingenuity.tableview.listener.click.ColumnHeaderRecyclerViewItemClickListener
import ph.ingenuity.tableview.listener.click.RowHeaderRecyclerViewItemClickListener
import ph.ingenuity.tableview.listener.layout.TableViewLayoutChangeListener
import ph.ingenuity.tableview.listener.scroll.HorizontalRecyclerViewListener
import ph.ingenuity.tableview.listener.scroll.VerticalRecyclerViewListener
import ph.ingenuity.tableview_kt.R

@SuppressLint("ViewConstructor")
/**
 * Created by jeremypacabis on February 23, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview <android-tableview-kotlin>
 */
class TableView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : FrameLayout(
        context,
        attrs,
        defStyleAttr
), ITableView {

    override var showHorizontalSeparators: Boolean = false

    override var showVerticalSeparators: Boolean = false

    override var ignoreSelectionColors: Boolean = false

    override var hasFixedWidth: Boolean = false

    override var isSorted: Boolean = false

    override var columnHeaderHeight: Int = -1

    override var rowHeaderWidth: Int = -1

    override var unselectedColor: Int = -1

    override var separatorColor: Int = -1

    override var selectedColor: Int = -1

    override var shadowColor: Int = -1

    override var selectedColumn: Int?
        get() = selectionHandler.selectedColumnPosition
        set(value) {
            val columnViewHolder = columnHeaderRecyclerView
                    .findViewHolderForAdapterPosition(value!!) as AbstractViewHolder
            selectionHandler.setSelectedColumnPosition(columnViewHolder, value)
        }

    override var selectedRow: Int?
        get() = selectionHandler.selectedRowPosition
        set(value) {
            val rowViewHolder = rowHeaderRecyclerView
                    .findViewHolderForAdapterPosition(value!!) as AbstractViewHolder
            selectionHandler.setSelectedRowPosition(rowViewHolder, value)
        }

    override lateinit var columnHeaderRecyclerViewItemClickListener:
            ColumnHeaderRecyclerViewItemClickListener

    override lateinit var rowHeaderRecyclerViewItemClickListener:
            RowHeaderRecyclerViewItemClickListener

    override lateinit var horizontalRecyclerViewListener: HorizontalRecyclerViewListener

    override lateinit var verticalRecyclerViewListener: VerticalRecyclerViewListener

    override lateinit var columnHeaderRecyclerView: CellRecyclerView

    override lateinit var rowHeaderRecyclerView: CellRecyclerView

    override lateinit var cellRecyclerView: CellRecyclerView

    override lateinit var columnHeaderLayoutManager: ColumnHeaderLayoutManager

    override lateinit var rowHeaderLayoutManager: LinearLayoutManager

    override lateinit var cellLayoutManager: CellLayoutManager

    override lateinit var horizontalItemDecoration: DividerItemDecoration

    override lateinit var verticalItemDecoration: DividerItemDecoration

    override lateinit var visibilityHandler: VisibilityHandler

    override lateinit var columnSortHandler: ColumnSortHandler

    override lateinit var selectionHandler: SelectionHandler

    override lateinit var scrollHandler: ScrollHandler

    override lateinit var filterHandler: FilterHandler

    override lateinit var rowHeaderSortState: SortState

    override var tableViewListener: ITableViewListener? = null
        set(value) {
            field = value

            // Column and row header item click listeners
            columnHeaderRecyclerViewItemClickListener =
                    ColumnHeaderRecyclerViewItemClickListener(columnHeaderRecyclerView, this)
            rowHeaderRecyclerViewItemClickListener =
                    RowHeaderRecyclerViewItemClickListener(rowHeaderRecyclerView, this)
            columnHeaderRecyclerView.addOnItemTouchListener(columnHeaderRecyclerViewItemClickListener)
            rowHeaderRecyclerView.addOnItemTouchListener(rowHeaderRecyclerViewItemClickListener)
        }

    override var adapter: AbstractTableAdapter? = null
        set(value) {
            if (value != null) {
                field = value
                field!!.rowHeaderWidth = rowHeaderWidth
                field!!.columnHeaderHeight = columnHeaderHeight
                field!!.tableView = this

                columnHeaderRecyclerView.adapter = value.columnHeaderRecyclerViewAdapter
                rowHeaderRecyclerView.adapter = value.rowHeaderRecyclerViewAdapter
                cellRecyclerView.adapter = value.cellRecyclerViewAdapter

                columnSortHandler = ColumnSortHandler(this)
                filterHandler = FilterHandler(this)
            }
        }

    init {
        // Getting the default attributes for the TableView
        rowHeaderWidth = resources.getDimension(R.dimen.default_row_header_width).toInt()
        columnHeaderHeight = resources.getDimension(R.dimen.default_column_header_height).toInt()
        selectedColor = getColor(
                context,
                R.color.table_view_default_selected_background_color
        )
        unselectedColor = getColor(
                context,
                R.color.table_view_default_unselected_background_color
        )
        shadowColor = getColor(
                context,
                R.color.table_view_default_shadow_background_color
        )

        // Setting the customized attributes for the TableView
        if (attrs != null) {
            val a = context.theme.obtainStyledAttributes(attrs, R.styleable.TableView, 0, 0)
            try {
                rowHeaderWidth = a.getDimension(
                        R.styleable.TableView_table_row_header_width,
                        rowHeaderWidth.toFloat()
                ).toInt()
                columnHeaderHeight = a.getDimension(
                        R.styleable.TableView_table_column_header_height,
                        columnHeaderHeight.toFloat()
                ).toInt()
                unselectedColor = a.getColor(R.styleable.TableView_table_unselected_color, unselectedColor)
                separatorColor = a.getColor(R.styleable.TableView_table_separator_color, separatorColor)
                selectedColor = a.getColor(R.styleable.TableView_table_selected_color, selectedColor)
                shadowColor = a.getColor(R.styleable.TableView_table_shadow_color, shadowColor)
                showVerticalSeparators = a.getBoolean(
                        R.styleable.TableView_table_show_vertical_separator,
                        showVerticalSeparators
                )
                showHorizontalSeparators = a.getBoolean(
                        R.styleable.TableView_table_show_horizontal_separator,
                        showHorizontalSeparators
                )
            } finally {
                a.recycle()
            }
        }

        // Initialize needed TableView properties
        horizontalItemDecoration = createItemDecoration(DividerItemDecoration.HORIZONTAL)
        verticalItemDecoration = createItemDecoration(DividerItemDecoration.VERTICAL)

        // Initialize the TableView with the attributes

        // Layout managers
        columnHeaderLayoutManager = ColumnHeaderLayoutManager(context, this)
        columnHeaderRecyclerView = createColumnHeaderRecyclerView()
        rowHeaderLayoutManager = LinearLayoutManager(context, VERTICAL, false)
        rowHeaderRecyclerView = createRowHeaderRecyclerView()
        cellLayoutManager = CellLayoutManager(context, this)
        cellRecyclerView = createCellRecyclerView()

        // Adding the three created views
        addView(columnHeaderRecyclerView)
        addView(rowHeaderRecyclerView)
        addView(cellRecyclerView)

        // Initialize required handlers
        selectionHandler = SelectionHandler(this)
        visibilityHandler = VisibilityHandler(this)
        scrollHandler = ScrollHandler(this)

        // Initialize required listeners
        // Vertical scroll listener
        verticalRecyclerViewListener = VerticalRecyclerViewListener(this)

        // Assign the vertical scroll listener to cell and row header recycler views
        rowHeaderRecyclerView.addOnItemTouchListener(verticalRecyclerViewListener)
        cellRecyclerView.addOnItemTouchListener(verticalRecyclerViewListener)

        // Horizontal scroll listener
        horizontalRecyclerViewListener = HorizontalRecyclerViewListener(this)

        // Assign the horizontal scroll listener to column header recycler view
        columnHeaderRecyclerView.addOnItemTouchListener(horizontalRecyclerViewListener)

        // Layout change listener
        val layoutChangeListener = TableViewLayoutChangeListener(this)
        columnHeaderRecyclerView.addOnLayoutChangeListener(layoutChangeListener)
        cellRecyclerView.addOnLayoutChangeListener(layoutChangeListener)
    }

    private fun createCellRecyclerView(): CellRecyclerView {
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        val recyclerView = CellRecyclerView(context)
        recyclerView.isMotionEventSplittingEnabled = false
        recyclerView.layoutManager = cellLayoutManager
        layoutParams.leftMargin = rowHeaderWidth
        layoutParams.topMargin = columnHeaderHeight
        recyclerView.layoutParams = layoutParams
        if (showVerticalSeparators) {
            recyclerView.addItemDecoration(verticalItemDecoration)
        }

        return recyclerView
    }

    private fun createRowHeaderRecyclerView(): CellRecyclerView {
        val layoutParams = LayoutParams(rowHeaderWidth, LayoutParams.WRAP_CONTENT)
        val recyclerView = CellRecyclerView(context)
        recyclerView.layoutManager = rowHeaderLayoutManager
        layoutParams.topMargin = columnHeaderHeight
        recyclerView.layoutParams = layoutParams
        if (showVerticalSeparators) {
            recyclerView.addItemDecoration(verticalItemDecoration)
        }

        return recyclerView
    }

    private fun createColumnHeaderRecyclerView(): CellRecyclerView {
        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, columnHeaderHeight)
        val recyclerView = CellRecyclerView(context)
        recyclerView.layoutManager = columnHeaderLayoutManager
        layoutParams.leftMargin = rowHeaderWidth
        recyclerView.layoutParams = layoutParams
        if (showHorizontalSeparators) {
            recyclerView.addItemDecoration(horizontalItemDecoration)
        }

        return recyclerView
    }

    private fun createItemDecoration(orientation: Int): DividerItemDecoration {
        val divider = ContextCompat.getDrawable(context, R.drawable.cell_line_divider)
        if (separatorColor == -1) {
            divider?.setColorFilter(separatorColor, PorterDuff.Mode.SRC_ATOP)
        }
        val itemDecoration = DividerItemDecoration(context, orientation)
        itemDecoration.setDrawable(divider!!)
        return itemDecoration
    }

    override fun scrollToColumn(column: Int) {
        scrollHandler.scrollToColumnPosition(column)
    }

    override fun scrollToRow(row: Int) {
        scrollHandler.scrollToRowPosition(row)
    }

    override fun showColumn(column: Int) {
        visibilityHandler.showColumn(column)
    }

    override fun hideColumn(column: Int) {
        visibilityHandler.hideColumn(column)
    }

    override fun showRow(row: Int) {
        visibilityHandler.showRow(row)
    }

    override fun hideRow(row: Int) {
        visibilityHandler.hideRow(row)
    }

    override fun clearHiddenColumnList() {
        visibilityHandler.clearHiddenColumnsList()
    }

    override fun clearHiddenRowList() {
        visibilityHandler.clearHiddenRowList()
    }

    override fun showAllHiddenColumns() {
        visibilityHandler.showAllHiddenColumns()
    }

    override fun showAllHiddenRows() {
        visibilityHandler.showAllHiddenRows()
    }

    override fun sortColumn(column: Int, sortState: SortState) {
        isSorted = true
        columnSortHandler.sort(column, sortState)
    }

    override fun sortRowHeader(sortState: SortState) {
        isSorted = true
        columnSortHandler.sortByRowHeader(sortState)
    }

    override fun remeasureColumnWidth(column: Int) {
        columnHeaderLayoutManager.removeCachedWidth(column)
        cellLayoutManager.fitWidthSize(column, false)
    }

    override fun filter(filter: Filter) {
        filterHandler.filter(filter)
    }

    override fun getColumnSortState(column: Int): SortState =
            columnSortHandler.getSortingStatus(column)

    override fun isColumnVisible(column: Int): Boolean = visibilityHandler.isColumnVisible(column)

    override fun isRowVisible(row: Int): Boolean = visibilityHandler.isRowVisible(row)

    fun selectCell(column: Int, row: Int) {
        val cellViewHolder = cellLayoutManager.getCellViewHolder(column, row)
        selectionHandler.setSelectedCellPositions(cellViewHolder!!, column, row)
    }
}
