# Android TableView (Kotlin)

_Android's missing TableView component._ This Kotlin version is based on the original
[TableView](https://github.com/evrencoskun/TableView) implementation by
[Evren Coşkun](https://github.com/evrencoskun/). This component uses RecyclerViews for displaying
column headers, row headers and cells.

## Features

- [x] Calculated fixed width based on the largest cell.
- [x] Different ViewHolders for the headers and cells.
- [x] Action listeners for interacting with the headers and cells.
- [x] Visibility control for rows and columns.
- [x] Sorting for column data.
- [x] Filtering/query for data.
- [x] Pagination functionality.

## Latest version

You can check the [releases page](https://github.com/ingenuity-ph/android-tableview-kotlin/releases)
for the latest version and changelog.

## Documentation
- [Integration](#project-integration)
- [Creating the TableView](#creating-the-tableview)
- [Creating the TableViewAdapter](#creating-the-tableviewadapter)
- [Setting the TableViewAdapter to the TableView](#setting-the-tableviewadapter-to-the-tableview)
- [Setting an ActionListener to the TableView](#setting-an-actionlistener-to-the-tableview)
- [Updating TableView data](#updating-tableview-data)
- [Visibility controls](#tableview-visibility-controls)
- [Advanced properties and methods](#tableview-advanced-properties-and-methods)
- [Sorting](#sorting)
- [Filtering](#filtering)
- [Pagination](#pagination)
- [Sample Applications](#sample-applications)
- [Articles](#articles)
- [Communication](#communication)
- [Contributors](#contributors)
- [License](#license)

### Project integration

To use this library in your Android project, add this dependency line
`implementation 'ph.ingenuity.tableview_kt:tableview_kt:0.1.0-alpha'`
to your application's `build.gradle` file.

```
    dependencies {
        ...
        implementation 'ph.ingenuity.tableview_kt:tableview_kt:0.1.0-alpha'
        ...
    }
```

### 1. Creating the TableView

#### Programmatically

```kotlin
    val tableView = TableView(context)
```

#### XML layout

##### Basic

```xml
...
    <ph.ingenuity.tableview.TableView
        android:id="@+id/table_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
...
```

##### Customized

```xml
...
    <ph.ingenuity.tableview.TableView
        android:id="@+id/table_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:table_column_header_height="@dimen/default_column_header_height"
        app:table_row_header_width="@dimen/row_header_width"
        app:table_selected_color="@color/colorPrimary"
        app:table_separator_color="@color/colorAccent"
        app:table_shadow_color="@color/table_view_default_shadow_background_color"
        app:table_show_horizontal_separator="true"
        app:table_show_vertical_separator="true"
        app:table_unselected_color="@color/table_view_default_unselected_background_color" />
...
```

```kotlin
...
    val tableView = findViewById(R.id.table_view)
...
```

###### Customizable Attributes

**Note:** To use these attributes on XML layout, the **xmlns:** namespace line
`xmlns:app="http://schemas.android.com/apk/res-auto"` should be added on the layout root view.
Otherwise, Android Studio gives you compile error.

- `table_column_header_height` - height of the column header
- `table_row_header_width` - width of the row header
- `table_selected_color` - color for selected cells
- `table_separator_color` - separator color
- `table_shadow_color` - shadow color
- `table_show_horizontal_separator` - visibility control for horizontal separator
- `table_show_vertical_separator` - visibility control for vertical separator
- `table_unselected_color` - color for not selected cells

### 2. Creating the TableViewAdapter

A custom TableViewAdapter must be created to handle the ViewHolders for the column header, row
header and cells. Since this library uses `RecyclerView` component, `onCreateViewHolder` and
`onBindViewHolder` methods are called for the column header, row header and cells. The custom
TableViewAdapter should extend the `AbstractTableAdapter` class. Also create your `ViewHolder`
classes to extend `AbstractViewHolder` class.

###### Example TableViewAdapter

```kotlin
    class RandomDataTableViewAdapter(
        private val context: Context
    ) : AbstractTableAdapter(context) {
    
        override fun getColumnHeaderItemViewType(column: Int): Int = 0
    
        override fun getRowHeaderItemViewType(row: Int): Int = 0
    
        override fun getCellItemViewType(column: Int): Int = 0
    
        override fun onCreateCellViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): RecyclerView.ViewHolder {
            val cellView = LayoutInflater.from(context).inflate(
                    // Replace this with your cell view layout
                    R.layout.table_cell_text_data,
                    parent,
                    false
            )
    
            return RandomDataCellViewHolder(cellView)
        }
    
        override fun onBindCellViewHolder(
                holder: AbstractViewHolder,
                cellItem: Any,
                column: Int,
                row: Int
        ) {
            val cell = cellItem as RandomDataCell
            val cellViewHolder = holder as RandomDataCellViewHolder
            cellViewHolder.cellTextView.text = cell.content.toString()
        }
    
        override fun onCreateColumnHeaderViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): RecyclerView.ViewHolder {
            val columnHeaderView = LayoutInflater.from(context).inflate(
                    // Replace this with your column header view layout
                    R.layout.table_column_header_text_data,
                    parent,
                    false
            )
    
            return RandomDataColumnHeaderViewHolder(columnHeaderView)
        }
    
        override fun onBindColumnHeaderViewHolder(
                holder: AbstractViewHolder,
                columnHeaderItem: Any,
                column: Int
        ) {
            val columnHeaderCell = columnHeaderItem as RandomDataCell
            val columnHeaderViewHolder = holder as RandomDataColumnHeaderViewHolder
            columnHeaderViewHolder.cellTextView.text = columnHeaderCell.content.toString()
        }
    
        override fun onCreateRowHeaderViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): RecyclerView.ViewHolder {
            val rowHeaderView = LayoutInflater.from(context).inflate(
                    // Replace this with your row header view layout
                    R.layout.table_row_header_text_data,
                    parent,
                    false
            )
    
            return RandomDataRowHeaderViewHolder(rowHeaderView)
        }
    
        override fun onBindRowHeaderViewHolder(
                holder: AbstractViewHolder,
                rowHeaderItem: Any,
                row: Int
        ) {
            val rowHeaderCell = rowHeaderItem as RandomDataCell
            val rowHeaderViewHolder = holder as RandomDataRowHeaderViewHolder
            rowHeaderViewHolder.cellTextView.text = rowHeaderCell.content.toString()
        }
    
        override fun onCreateCornerView(): View? {
            // Replace this with your corner view layout
            val cornerView = LayoutInflater.from(context).inflate(R.layout.table_corner_view, null)
            cornerView.setOnClickListener {
                Toast.makeText(context, "CornerView has been clicked.", Toast.LENGTH_SHORT).show()
            }
    
            return cornerView
        }
        
        class RandomDataCellViewHolder(itemView: View) : AbstractViewHolder(itemView) {
            val cellTextView: TextView
                get() = itemView.findViewById(R.id.random_data_cell_data)
        }
        
        class RandomDataColumnHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
            val cellTextView: TextView
                get() = itemView.findViewById(R.id.column_header_text)
        }
        
        class RandomDataRowHeaderViewHolder(itemView: View) : AbstractViewHolder(itemView) {
            val cellTextView: TextView
                get() = itemView.findViewById(R.id.row_header_text)
        }
    }
```

### 3. Setting the TableViewAdapter to the TableView

`AbstractTableAdapter` class requires three lists for the column header, row header and cell items.

```kotlin
...
    // Retrieve your data from local storage or API
    val cellsList = randomDataFactory.randomCellsList as List<List<Any>>
    val rowHeadersList = randomDataFactory.randomRowHeadersList as List<Any>
    val columnHeadersList = randomDataFactory.randomColumnHeadersList as List<Any>
            
    // Create an instance of our custom TableViewAdapter
    val tableAdapter = RandomDataTableViewAdapter(mainView!!.context)
    
    // Set the adapter to the created TableView
    tableView.adapter = tableAdapter
    
    // Set the data to the adapter
    tableAdapter.setAllItems(cellsList, columnHeadersList, rowHeadersList)
...
```

### 4. Setting an ActionListener to the TableView

A custom TableViewListener must be created to handle column header, row header and
cell click and long pressed actions. The custom TableViewListener must implement the
`ITableViewListener` interface.

###### Example TableViewListener

```kotlin
    class TableViewListener(private val tableView: TableView) : ITableViewListener {
    
        private var toast: Toast? = null
    
        private val context: Context = tableView.context
    
        override fun onCellClicked(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
            showToast("Cell $column $row has been clicked.")
        }
    
        override fun onCellLongPressed(cellView: RecyclerView.ViewHolder, column: Int, row: Int) {
            showToast("Cell $column, $row has been long pressed.")
        }
    
        override fun onColumnHeaderClicked(columnHeaderView: RecyclerView.ViewHolder, column: Int) {
            showToast("Column header $column has been clicked.")
        }
    
        override fun onColumnHeaderLongPressed(
            columnHeaderView: RecyclerView.ViewHolder, 
            column: Int
        ) {
            if (columnHeaderView is RandomDataColumnHeaderViewHolder) {
                val popup = ColumnHeaderLongPressPopup(columnHeaderView, tableView)
                popup.show()
            }
        }
    
        override fun onRowHeaderClicked(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
            showToast("Row header $row has been clicked.")
        }
    
        override fun onRowHeaderLongPressed(rowHeaderView: RecyclerView.ViewHolder, row: Int) {
            if (rowHeaderView is RandomDataRowHeaderViewHolder) {
                val popup = RowHeaderLongPressPopup(rowHeaderView, tableView)
                popup.show()
            }
        }
    
        private fun showToast(message: String) {
            if (toast == null) {
                toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
            }
    
            toast!!.setText(message)
            toast!!.show()
        }
    }
```

###### Setting the TableViewListener to the TableView

```kotlin
...
    tableView.tableViewListener = TableViewListener(tableView)
...
```

> By now, we should have a working TableView with displayed data and action listeners.
The next sections are for some TableView features such as changing data sets, hiding/showing
columns and rows and scrolling to position, as well as advanced features such as
sorting, filtering and pagination.

### Updating TableView data

The data set in the TableView can be updated by updating the TableViewAdapter.

- To **add a row**:

```kotlin
...
    tableView.adapter.addRow(position, rowHeaderItem, cellItems)
...
```

- To **add a multiple rows**:

```kotlin
...
    tableView.adapter.addRowRange(position, rowHeaderItems, cellItems)
...
```

- To **remove a row**:

```kotlin
...
    tableView.adapter.removeRow(position)
...
``` 

- To **remove multiple rows**:

```kotlin
...
    tableView.adapter.removeRow(position, count)
...
``` 

- To **update a row header**:

```kotlin
...
    tableView.adapter.changeRowHeaderItem(position, rowHeaderItem)
...
```

- To **update multiple row headers**:

```kotlin
...
    tableView.adapter.changeRowHeaderItemRange(position, rowHeaderItems)
...
``` 

- To **update a column header**:

```kotlin
...
    tableView.adapter.changeColumnHeader(position, columnHeaderItem)
...
```

- To **update multiple column headers**:

```kotlin
...
    tableView.adapter.changeColumnHeaderRange(position, columnHeaderItems)
...
``` 

- To **update a cell item**:

```kotlin
...
    tableView.adapter.changeCellItem(column, row, cellItem)
...
``` 

### TableView visibility controls

The visibility of the the rows and columns in the TableView can be controlled using the visibility
controls in the TableView instance.

- To **show** a row:

```kotlin
...
    tableView.showRow(row)
...
```

- To **hide** the row:

```kotlin
...
    tableView.hideRow(row)
...
```

- To **show all hidden** rows:

```kotlin
...
    tableView.showAllHiddenRows()
...
```

- TableView store a map that contains all hidden rows.
This method will **clear the list of hidden rows**:

```kotlin
...
    tableView.clearHiddenRowList()
...
```

- To **check the visibility state** of a row:

```kotlin
...
    tableView.isRowVisible(row)
...
```

- To **show** a column:

```kotlin
...
    tableView.showColumn(column)
...
```

- To **hide** a column:

```kotlin
...
    tableView.hideColumn(column)
...
```

- To **show all hidden** columns

```kotlin
...
    tableView.showAllHiddenColumns()
...
```

- TableView store a map that contains all hidden columns.
This method will **clear the list of hidden columns**:

```kotlin
...
    tableView.clearHiddenColumnList()
...
```

- To **check the visibility state** of a column:

```kotlin
...
    tableView.isColumnVisible(column)
...
```

### TableView advanced properties and methods

- To recalculate column width:

```kotlin
...
    tableView.remeasureColumnWidth(column)
...
``` 

- To ignore column width calculation for better performance:
  
```kotlin
...
    tableView.hasFixedWidth = false
...
``` 

- To ignore setting selection colors that are displayed by user interaction:

```kotlin
...
    tableView.ignoreSelectionColors = false
...
``` 

- To show or hide separators of the TableView:

```kotlin
...
    tableView.showHorizontalSeparators = Boolean
...
```

```kotlin
...
    tableView.showVerticalSeparators = Boolean
...
```

- To programmatically scroll to a row or column position:

```kotlin
...
    tableView.scrollToColumn(column)
...
```

```kotlin
...
    tableView.scrollToRow(row)
...
```

### Sorting

> **Sorting**, by definition and usage in this context is the rearrangement of values in a data set
according to a property in ascending or descending manner.

#### Implementation

To use this feature in the TableView, the `Sortable` interface must be implemented in the data
classes or the models. The `Sortable` interface requires you to provide a unique `id` and
`content` value for your data. Sorting can be done on the following data types:

- **Number**
- **String**
- **Date**
- **Boolean**
- **Comparable**

###### Example data class implementing `Sortable` interface

```kotlin
    class RandomDataCell(
            _data: Any,
            _id: String = _data.hashCode().toString()
    ) : Sortable {
        
        override var id: String = _id
    
        override var content: Any = _data
    }
```

###### Sorting controls

- To **sort the TableView according to a specified column**:

```kotlin
...
    tableView.sortColumn(column, sortState)
...
```

- To **get the current sorting state of a column**:

```kotlin
...
    tableView.getColumnSortState(column)
...
```

###### Sorting states

- `SortState.ASCENDING`
- `SortState.DESCENDING`
- `SortState.UNSORTED`

###### Sorting state updates

Listening to sorting state changes can be done by implementing the `AbstractSorterViewHolder`
interface to your `ColumnHeaderViewHolder` class.

- This method is called after every sorting process:

```kotlin
...
    onSortingStatusChanged(SortState sortState)
...
```

- To get the sort state of a column header, just access the `sortState` object:

```kotlin
...
    columnHeaderViewHolder.sortState
...
```

### Filtering

> **Filtering**, by definition and usage in this context, is displaying a subset of data into the
TableView based on a given filter globally. on a specified column or combination.

#### Implementation

To use this feature in the TableView, the `Filterable` interface must be implemented in the data
classes or the models. The `Filterable` interface requires you to provide a unique
`filterableKeyword` string value for your data. This `filterableKeyword` will be used for filtering
the cell data based on a filter query.

###### Example data class implementing `Filterable` interface

```kotlin
    class RandomDataCell(
            _filter: String = _data.toString()
    ) : Filterable {
    
        override var filterableKeyword: String = _filter
    }
```

###### Creating an instance of the `Filter` class

An instance of the `Filter` class must be created and pass the TableView to be filtered.

```kotlin
...
    ...
    private lateinit var filter: Filter
    ...
    
    initialize() {
        ...
        setUpTableView()
        filter = Filter(tableView)
        ...
    }
...
```

###### Filtering process

Filtering can be done by calling the `set()` method of the `Filter` instance which can be used to
filter the whole tabele data, a column or combination. Clearing a filter can be simply done by
passing an **empty** string as filterKeyword (`""` and not `null`).

```kotlin
...
    // filtering whole table data
    fun filterWholeTable(filterKeyword: String) = filter.set(filterKeyword)
    
    // filtering a specific column
    fun filterThisColumn(column: Int, filterKeyword: String) = filter.set(column, filterKeyword)
    
    // clear filter for whole table
    fun clearTableFilter() = filter.set("")
    
    // clear filter for a specific column
    fun clearFilterForThisColumn(column: Int) = filter.set(column, "")
...
```

###### Adding a `FilterChangedListener`

A `FilterChangedListener` object can be added to the TableView for handling data changes during
filtering process.

```kotlin
...
    ...
    private val filterChangedListener = object : FilterChangedListener {

        fun onFilterChanged(
            filteredCellItems: List<List<Any>>,
            filteredRowHeaderItems: List<Any>
        ) {
            // do something here...
        }

        fun onFilterCleared(
            originalCellItems: List<List<Any>>,
            originalRowHeaderItems: List<Any>
        ) {
            // do something here...
        }
    }
    ...
    
    initialize() {
        ...
        setUpTableView()
        filter = Filter(tableView)
        tableView.filterHandler.addFilterChangedListener(filterChangedListener)
        ...
    }
...
```

### Pagination

> **Pagination**, by definition and usage in this context, is the division of the whole set of data
into subsets called pages and loading the data into the TableView page-by-page and not the whole
data directly. This is useful if you have a large amount of data to be displayed.

#### Implementation

###### Creating views to control the Pagination

_Depending on your preference, you may not follow the following and create your own implementation._
1. Create a layout with the following components: Two **Button** views to control next and
previous page, a **Spinner** if you want to have a customized number of pagination
(e.g. 10, 20, 50, All), an **EditText** to have a user input on which page s/he wants to go to,
a **TextView** to display details (e.g. _Showing page X, items Y-Z_).
2. Assign the views with the controls and methods which are discussed below.

###### Creating an instance of the `Pagination` class

- The `Pagination` class has three possible constructors: (1) passing the `TableView` instance only,
(2) `TableView` and the initial `ITEMS_PER_PAGE` and (3) `TableView`, initial `ITEMS_PER_PAGE` and
the `OnTableViewPageTurnedListener`. By default, if no ITEMS_PER_PAGE specified, the TableView will
be paginated into **10** items per page.

```kotlin
...
    ...
    private lateinit var pagination: Pagination
    ...
    
    initialize() {
        ...
        setUpTableView()
        pagination = Pagination(tableView)
        ...
    }
...
```

- **Loading the next page of items** into the TableView using the `loadNextPage()` method.
You can assign this to your implementation of nextPageButton onClick action:

```kotlin
...
    fun nextTablePage() = pagination.loadNextPage()
...
```

- **Loading the previous page of items** into the TableView using the `loadPreviousPage()` method.
You can assign this to your implementation of previousPageButton onClick action:

```kotlin
...
    fun previousTablePage() = pagination.loadPreviousPage()
...
```

- You can navigate through the pages by **going to a specific page directly** using the
`loadPage(page: Int)` method. You can assign this to the EditText field TextChanged
action (using TextWatcher):

```kotlin
...
    fun loadTablePage(page: Int) = pagination.loadPage(page)
...
```

- You can customize and **set the number of items to be displayed per page** of the TableView
using the `itemsPerPage` property of the pagination. You can assign this to your Spinner
with the number of items per page list:

```kotlin
...
    fun setTableItemsPerPage(numItems: Int) {
        pagination.itemsPerPage = numItems
    }
...
```

###### Adding an `OnTableViewPageTurnedListener`

- A listener interface (**Pagination.OnTableViewPageTurnedListener**) can also be implemented
if you want to do something each time _a page is turned_ (e.g. previous, next, goToPage or
change items per page action is called):

```kotlin
...
    ...
    private val onTableViewPageTurnedListener =
            object : Pagination.OnTableViewPageTurnedListener {
            
                override fun onPageTurned(numItems: Int, itemsStart: Int, itemsEnd: Int) {
                    // do something here...
                }
            }
    ...
    
    initialize() {
        ...
        setUpTableView()
        pagination = Pagination(tableView)
        pagination.onTableViewPageTurnedListener = onTableViewPageTurnedListener
        ...
    }
...
```

## Sample Applications

- This repository contains a Kotlin demo application implementing the TableView library.
- [Contact me](mailto:jeremy@ingenuity.ph)if you want your app to be listed here.

## Articles

- [TODO](TODO)

## Communication

- If you **need help**, please use
[Stack Overflow](https://stackoverflow.com/questions/tagged/tableview+android+kotlin).
(Tag 'TableView', 'Android', 'Kotlin')
- If you'd like to **ask a general question**, please use
[Stack Overflow](https://stackoverflow.com/questions/tagged/tableview+android+kotlin).
- If you **found a bug**, please open an issue.
- If you **have a feature request**, please open an issue.
- If you **want to contribute**, please submit a pull request.
- You can [contact me](mailto:jeremy@ingenuity.ph) if you want to discuss privately.

## Contributors

**Original author, idea, implementation and code** by
[Evren Coşkun](https://github.com/evrencoskun/).

Contributions of any kind are welcome!

## License

    Copyright 2018 Jeremy Patrick Pacabis
    Copyright 2017-2018 Evren Coşkun
    
    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:
    
    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
    
    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
    SOFTWARE.