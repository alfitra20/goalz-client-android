package emse.mobisocial.goalz.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.ExploreGoalsViewModel
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup
import com.nex3z.togglebuttongroup.button.LabelToggle

class ExploreGoalsFragment : Fragment() {

    private lateinit var model : ExploreGoalsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    // filter variables and views
    private var filterOpen: Boolean = false
    private lateinit var filterView: MultiSelectToggleGroup
    private lateinit var proximityFilter: LabelToggle
    private lateinit var topicFilter: LabelToggle
    private lateinit var statusFilter: LabelToggle

    // Location
    private lateinit var client: FusedLocationProviderClient
    private lateinit var currentLocation: Location

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_explore_goals, container, false)
        setHasOptionsMenu(true)

        // Get current location
        client = LocationServices.getFusedLocationProviderClient(activity)
        client.lastLocation.addOnSuccessListener(activity) { location: Location? ->
            if (location != null) {
                currentLocation = location
            }
        }

        // Initialize data
        filterView = view.findViewById(R.id.explore_goals_filters) as MultiSelectToggleGroup
        recyclerView = view.findViewById(R.id.explore_goals_recycler_view) as RecyclerView
        model = ViewModelProviders.of(this).get(ExploreGoalsViewModel::class.java)
        proximityFilter = view.findViewById(R.id.order_explore_goals_proximity)
        topicFilter = view.findViewById(R.id.order_explore_goals_topic)
        statusFilter = view.findViewById(R.id.order_explore_goals_status)

        setupRecyclerView()
        initializeObservers()
        initializeListeners()

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = RecyclerViewAdapter(ArrayList<Goal>())
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun initializeObservers() {
        model.goalsList.observe(this, Observer<List<Goal>> { goals ->
            if (goals != null) {
                recyclerViewAdapter.addItems(goals)
            }
        })
    }

    private fun initializeListeners() {
        filterView.setOnCheckedChangeListener(object: MultiSelectToggleGroup.OnCheckedStateChangeListener {
            override fun onCheckedStateChanged(single: MultiSelectToggleGroup?, checkedId: Int, isChecked: Boolean) {
                  if (isChecked) {
                    uncheckOthers(checkedId)
                    recyclerViewAdapter.filterRecyclerView()
                } else {
                    model.reset()
                }
            }
        })
    }

    private fun uncheckOthers(checkId: Int) {
        for (id: Int in filterView.getCheckedIds()) {
            if (id != checkId) {
                when(id) {
                    proximityFilter.id -> proximityFilter.setChecked(false)
                    topicFilter.id -> topicFilter.setChecked(false)
                    statusFilter.id -> statusFilter.setChecked(false)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        inflater?.inflate(R.menu.menu_explore, menu)

        // Setup the behaviour of the search menu item
        val searchItem = menu!!.findItem(R.id.exploreSearch)
        searchView = searchItem.actionView as SearchView
        searchView.setIconified(false)
        searchView.setOnCloseListener(object: SearchView.OnCloseListener {
            override fun onClose(): Boolean {
                searchItem.collapseActionView()
                return true
            }
        })
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{

            override fun onQueryTextSubmit(searchQuery: String?): Boolean {
                model.searchGoals(searchQuery!!)
                return true
            }

            override fun onQueryTextChange(searchQuery: String?): Boolean {
                model.searchGoals(searchQuery!!)
                return true
            }
        })

        // Set up the behaviour of the filter menu item
        val filterItem = menu!!.findItem(R.id.exploreFilter)
        val r = context.resources
        val topMarginPx = r.getDimensionPixelSize(R.dimen.explore_fragment_top_margin)
        val params = recyclerView.getLayoutParams() as ViewGroup.MarginLayoutParams
        filterItem.setOnMenuItemClickListener(object: MenuItem.OnMenuItemClickListener {
            override fun onMenuItemClick(p0: MenuItem?): Boolean {
                if (!filterOpen) {
                    filterView.visibility = View.VISIBLE
                    params.setMargins(0, 0, 0, 0)
                    recyclerView.setLayoutParams(params)
                } else {
                    filterView.visibility = View.GONE
                    params.setMargins(0, topMarginPx, 0, 0)
                    recyclerView.setLayoutParams(params)
                }
                filterOpen = !filterOpen
                return true
            }
        })

        super.onCreateOptionsMenu(menu,inflater)
    }

    inner class RecyclerViewAdapter(goalsParam: ArrayList<Goal>) : RecyclerView.Adapter<RecyclerViewAdapter.GoalViewHolder>() {
        private var mGoals: List<Goal> = goalsParam

        override fun getItemCount(): Int {
            return mGoals.size
        }

        fun addItems(newGoalsList: List<Goal>) {
            this.mGoals = newGoalsList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GoalViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.goal_card, viewGroup, false)
            return GoalViewHolder(v)
        }


        override fun onBindViewHolder(goalViewHolder: GoalViewHolder, i: Int) {
            // The data from the goal model is retrieved and bound to the card View here.
            goalViewHolder.goalTitle.text = mGoals[i].title
            goalViewHolder.goalTopic.text = mGoals[i].topic
            if (mGoals[i].status == 0){
                goalViewHolder.goalStatusImage.setImageResource(R.drawable.incomplete)
            }else{
                goalViewHolder.goalStatusImage.setImageResource(R.drawable.completed2)
            }
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            super.onAttachedToRecyclerView(recyclerView)
        }

        fun filterRecyclerView() {
            val checkedIds = filterView.getCheckedIds()
            for (id: Int in checkedIds) {
                when (id) {
                    proximityFilter.id -> filterByProximity()
                    topicFilter.id -> filterByTopic()
                    statusFilter.id -> filterByStatus()
                }
            }
        }

        fun filterByTopic() {
            val filterdList = ArrayList<Goal>(mGoals)
            filterdList.sortBy { goal -> goal.topic }
            this.mGoals = filterdList
            notifyDataSetChanged()
        }

        fun filterByProximity() {
            val filterdList = ArrayList<Goal>(mGoals)
            filterdList.sortBy { goal -> goal.location.distanceTo(currentLocation) }
            this.mGoals = filterdList
            notifyDataSetChanged()
        }

        fun filterByStatus() {
            val filterdList = ArrayList<Goal>(mGoals)
            filterdList.sortBy { goal -> -goal.status }
            this.mGoals = filterdList
            notifyDataSetChanged()
        }

        inner class GoalViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var goalCard: CardView
            internal var goalTitle: TextView
            internal var goalTopic: TextView
            internal var goalStatusImage: ImageView

            init {
                goalCard = itemView.findViewById<View>(R.id.goal_card_view) as CardView
                goalTitle = itemView.findViewById<View>(R.id.goal_title) as TextView
                goalTopic = itemView.findViewById<View>(R.id.goal_topic) as TextView
                goalStatusImage = itemView.findViewById(R.id.status_image) as ImageView

                itemView.setOnClickListener {
                    val goalId = mGoals[adapterPosition].id
                    val intent = Intent(activity, GoalActivity::class.java)
                    intent.putExtra("goal_id", goalId)
                    startActivity(intent)
                }
            }
        }


    }
}