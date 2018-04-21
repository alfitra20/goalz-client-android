package emse.mobisocial.goalz.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import emse.mobisocial.goalz.R
import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.*


class ExploreFragment : Fragment() {

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null
    private var mContext: AppCompatActivity? = null
    private  var menuInflater: MenuInflater? = null
    private var appBar: Menu? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_explore, container, false)
        var viewPager = view.findViewById<ViewPager>(R.id.explorePager)

        setHasOptionsMenu(true)

        val tabLayout = view.findViewById(R.id.exploreTabs) as TabLayout
        tabLayout.addTab(tabLayout.newTab()
                //.setText("Goals")
                .setIcon(R.drawable.goal_white)
        )

        tabLayout.addTab(tabLayout.newTab()
               // .setText("Users")
                .setIcon(R.drawable.users_grey)
        )
        tabLayout.addTab(tabLayout.newTab()
                //.setText("Resources")
                .setIcon(R.drawable.resource_grey)
        )
        tabLayout.tabGravity = TabLayout.GRAVITY_FILL

        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                var position = tab?.position
                when (position){
                    0 -> tab?.setIcon(R.drawable.goal_white)
                    1 -> tab?.setIcon(R.drawable.users_white)
                    2 -> tab?.setIcon(R.drawable.resource_white)
                }
                viewPager.currentItem = tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                var position = tab?.position
                when (position){
                    0 -> tab?.setIcon(R.drawable.goal_grey)
                    1 -> tab?.setIcon(R.drawable.users_grey)
                    2 -> tab?.setIcon(R.drawable.resource_grey)
                }
            }
        })

        mSectionsPagerAdapter = SectionsPagerAdapter(mContext!!.supportFragmentManager)
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menuInflater = inflater
        appBar = menu
        super.onCreateOptionsMenu(menu,inflater)
    }

    override fun onDetach() {
        super.onDetach()
        appBar?.clear()
        menuInflater?.inflate(R.menu.base, appBar)
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