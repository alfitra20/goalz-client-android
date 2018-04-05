package emse.mobisocial.goalz.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.test.fragments.TestGoalFragment
import emse.mobisocial.goalz.test.fragments.TestRecommendationFragment
import emse.mobisocial.goalz.test.fragments.TestResourceFragment
import emse.mobisocial.goalz.test.fragments.TestUserFragment
import kotlinx.android.synthetic.main.activity_base.*

class ExploreActivity : BaseActivity() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Initialize the Navigation view and menu + floating button
        setFab()

        val actionbar = supportActionBar
        actionbar?.title = getString(R.string.app_bar_explore)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        container.adapter = mSectionsPagerAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_explore, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when (position) {
                0 -> return TestUserFragment()
                1 -> return TestResourceFragment()
                2 -> return TestGoalFragment()
                3 -> return TestRecommendationFragment()
            }
            return TestResourceFragment()
        }

        override fun getCount(): Int {
            // Show 4 total pages.
            return 4
        }
    }
}
