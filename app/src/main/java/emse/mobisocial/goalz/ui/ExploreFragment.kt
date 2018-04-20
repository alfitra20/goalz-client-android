package emse.mobisocial.goalz.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager

import emse.mobisocial.goalz.R
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.*


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
        mSectionsPagerAdapter = SectionsPagerAdapter(childFragmentManager)
        viewPager.adapter = mSectionsPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position;
            }
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab!!.position;
            }
        })
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