package emse.mobisocial.goalz.ui

import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_goal.*

class GoalActivity : AppCompatActivity() {


    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        setSupportActionBar(toolbar)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)


        container.adapter = mSectionsPagerAdapter

        var new_goal_id = intent.getStringExtra("new_goal_id")

        Log.d("newID", new_goal_id)


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_goal, menu)
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
            // Temporary
            return GoalsFragment()
        }

        override fun getCount(): Int {
            // Show 2 total pages.
            return 2
        }
    }
}
