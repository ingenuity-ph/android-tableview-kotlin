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

package ph.ingenuity.tableview.activities

import android.app.ProgressDialog
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.widget.*
import ph.ingenuity.tableview.R
import ph.ingenuity.tableview.fragments.RandomDataTableViewFragment
import ph.ingenuity.tableview.fragments.RandomDataTableViewWithControlsFragment

/**
 * Created by jeremypacabis on March 02, 2018.
 * @author Jeremy Patrick Pacabis <jeremy@ingenuity.ph>
 * ph.ingenuity.tableview.activities <android-tableview-kotlin>
 */
class MainActivity : AppCompatActivity() {

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var drawerLayout: DrawerLayout

    private lateinit var contentLayout: LinearLayout

    private lateinit var listView: ListView

    private lateinit var selectText: TextView

    private var actionBar: ActionBar? = null

    private var handler = Handler()

    private var progressDialog: ProgressDialog? = null

    private var fragmentTransactionRunnable: Runnable? = null

    private val drawerMenuItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        loadFragment(position)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeViews()
    }

    override fun onPostCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onPostCreate(savedInstanceState, persistentState)
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when {
            drawerToggle.onOptionsItemSelected(item) -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun initializeViews() {
        val listAdapter = ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                resources.getStringArray(R.array.table_view_demos)
        )

        progressDialog = ProgressDialog(this)
        contentLayout = findViewById(R.id.content_layout)
        drawerLayout = findViewById(R.id.drawer_layout)
        listView = findViewById(R.id.sidebar_lv_menu)
        selectText = findViewById(R.id.select_text)
        listView.onItemClickListener = drawerMenuItemClickListener
        listView.adapter = listAdapter
        progressDialog!!.setCancelable(false)
        progressDialog!!.setMessage(getString(R.string.loading))
        actionBar = supportActionBar
        selectText.setOnClickListener { drawerLayout.openDrawer(Gravity.START) }

        if (actionBar != null) {
            actionBar!!.setDisplayHomeAsUpEnabled(true)
            drawerToggle = object : ActionBarDrawerToggle(
                    this,
                    drawerLayout,
                    R.string.open,
                    R.string.close
            ) {
                override fun onDrawerClosed(drawerView: View) {
                    super.onDrawerClosed(drawerView)
                    if (fragmentTransactionRunnable != null) {
                        progressDialog?.show()
                        handler.post(fragmentTransactionRunnable)
                        fragmentTransactionRunnable = null
                    }
                }

                override fun onDrawerOpened(drawerView: View) {
                    super.onDrawerOpened(drawerView)
                    actionBar!!.title = getString(R.string.app_name)
                }

                override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                    super.onDrawerSlide(drawerView, slideOffset)
                    val slideX = drawerView.width * slideOffset
                    contentLayout.translationX = slideX
                    contentLayout.scaleX = 1 - (slideOffset / scaleFactor)
                    contentLayout.scaleY = 1 - (slideOffset / scaleFactor)
                }
            }

            drawerToggle.isDrawerIndicatorEnabled = true
            drawerLayout.addDrawerListener(drawerToggle)
            drawerLayout.setScrimColor(Color.TRANSPARENT)
            drawerToggle.syncState()
        }
    }

    private fun loadFragment(position: Int) {
        fragmentTransactionRunnable = Runnable {
            val fragment = getFragment(position)
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            fragmentTransaction.replace(R.id.content_fragment, fragment, fragment.javaClass.simpleName)
            fragmentTransaction.runOnCommit { progressDialog?.cancel() }
            fragmentTransaction.commitAllowingStateLoss()
            actionBar!!.title = fragment.arguments?.getString("title")
        }

        drawerLayout.closeDrawers()
        if (selectText.visibility != GONE) {
            selectText.visibility = GONE
        }
    }

    private fun getFragment(position: Int): Fragment {
        return when (position) {
            0 -> RandomDataTableViewFragment.newInstance(this)
            1 -> RandomDataTableViewWithControlsFragment.newInstance(this)
            else -> RandomDataTableViewFragment.newInstance(this)
        }
    }

    companion object {
        const val scaleFactor = 6f
    }
}