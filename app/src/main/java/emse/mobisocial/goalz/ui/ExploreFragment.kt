package emse.mobisocial.goalz.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import emse.mobisocial.goalz.R
import kotlinx.android.synthetic.main.activity_base.*
import kotlinx.android.synthetic.main.activity_base.view.*
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import emse.mobisocial.goalz.test.fragments.TestGoalFragment
import emse.mobisocial.goalz.test.fragments.TestRecommendationFragment
import emse.mobisocial.goalz.test.fragments.TestResourceFragment
import emse.mobisocial.goalz.test.fragments.TestUserFragment


class ExploreFragment : Fragment() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mContext: AppCompatActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_explore, container, false)

        val tabLayout = view.findViewById(R.id.exploreTabs) as TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Goals"))
        tabLayout.addTab(tabLayout.newTab().setText("Users"))
        tabLayout.addTab(tabLayout.newTab().setText("Resources"))
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        var viewPager = view.findViewById<ViewPager>(R.id.explorePager)
        mSectionsPagerAdapter = SectionsPagerAdapter(mContext!!.supportFragmentManager)
        viewPager.adapter = mSectionsPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context as AppCompatActivity
    }

    override fun onDetach() {
        super.onDetach()
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            when(position){
                0 -> return ExploreGoalsFragment()
                1 -> return ExploreUsersFragment()
                2 -> return ExploreResourcesFragment()
                else -> return ExploreGoalsFragment()
            }
        }

        override fun getCount(): Int {
            return 3
        }
    }
}