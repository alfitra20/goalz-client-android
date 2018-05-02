package emse.mobisocial.goalz.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.ColorStateList
import android.location.Location
import android.os.Bundle
import android.support.constraint.Guideline
import android.support.v4.app.Fragment

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.ExploreGoalsViewModel
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.text.Layout
import android.util.Log
import android.view.*
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup
import com.nex3z.togglebuttongroup.button.LabelToggle

class ExploreGoalsFragment : Fragment() {

    enum class Filter {NONE, TOPIC, PROXIMITY, STATUS}

    private lateinit var model : ExploreGoalsViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    // filter variables and views
    private var filterOpen: Boolean = false
    private lateinit var filterViewLayout : LinearLayout
    private lateinit var filterView: MultiSelectToggleGroup
    private lateinit var proximityFilter: LabelToggle
    private lateinit var topicFilter: LabelToggle
    private lateinit var statusFilter: LabelToggle
    private var selectedFilter = Filter.NONE

    // Location
    private lateinit var client: FusedLocationProviderClient
    private lateinit var currentLocation: Location

    @SuppressLint("MissingPermission")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_explore_goals, container, false)
        setHasOptionsMenu(true)

        // Get current user
        model = ViewModelProviders.of(this).get(ExploreGoalsViewModel::class.java)
        model.userId = FirebaseAuth.getInstance().currentUser?.uid

        // Get current location
        client = LocationServices.getFusedLocationProviderClient(activity)
        client.lastLocation.addOnSuccessListener(activity) { location: Location? ->
            if (location != null) {
                currentLocation = location
            }
        }

        // Initialize data
        filterViewLayout = view.findViewById(R.id.explore_goals_filters_layout) as LinearLayout
        filterView = view.findViewById(R.id.explore_goals_filters) as MultiSelectToggleGroup
        filterViewLayout.visibility = View.GONE
        recyclerView = view.findViewById(R.id.explore_goals_recycler_view) as RecyclerView
        proximityFilter = view.findViewById(R.id.order_explore_goals_proximity)
        topicFilter = view.findViewById(R.id.order_explore_goals_topic)
        statusFilter = view.findViewById(R.id.order_explore_goals_status)
        statusFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        proximityFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        topicFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        proximityFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)
        statusFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)
        topicFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)



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
                recyclerViewAdapter.addItems(goals, selectedFilter)
            }
        })
    }

    private fun initializeListeners() {
        filterView.setOnCheckedChangeListener { _, checkedId, isChecked ->
            if (isChecked){
                when(checkedId) {
                    proximityFilter.id -> {
                        if(topicFilter.isChecked) {topicFilter.isChecked = false}
                        if(statusFilter.isChecked) {statusFilter.isChecked = false}
                        selectedFilter = Filter.PROXIMITY
                    }
                    topicFilter.id -> {
                        if(proximityFilter.isChecked) {proximityFilter.isChecked = false}
                        if(statusFilter.isChecked) {statusFilter.isChecked = false}
                        selectedFilter = Filter.TOPIC
                    }
                    statusFilter.id -> {
                        if(topicFilter.isChecked) {topicFilter.isChecked = false}
                        if(proximityFilter.isChecked) {proximityFilter.isChecked = false}
                        selectedFilter = Filter.STATUS
                    }
                }
                recyclerViewAdapter.sortRecyclerView(selectedFilter)
            } else if (!isChecked && !topicFilter.isChecked && !statusFilter.isChecked && !proximityFilter.isChecked) {
                selectedFilter = Filter.NONE
                recyclerViewAdapter.sortRecyclerView(selectedFilter)
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
                    filterViewLayout.visibility = View.VISIBLE
                    params.setMargins(0, 0, 0, 0)
                    recyclerView.setLayoutParams(params)
                } else {
                    filterViewLayout.visibility = View.GONE
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
        private var unsortedList: List<Goal> = goalsParam

        override fun getItemCount(): Int {
            return mGoals.size
        }

        fun addItems(newGoalsList: List<Goal>, filter: ExploreGoalsFragment.Filter) {
            this.unsortedList = newGoalsList
            this.mGoals = sortItems(filter)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GoalViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.goal_card, viewGroup, false)
            return GoalViewHolder(v, mGoals[i])
        }

        override fun onBindViewHolder(goalViewHolder: GoalViewHolder, i: Int) {
            // The data from the goal model is retrieved and bound to the card View here.
            val goal = mGoals[i]

            goalViewHolder.goalTitle.text = goal.title
            goalViewHolder.goalTopic.text = goal.topic
            goalViewHolder.goalDescription.text = goal.description
            goalViewHolder.goalProgressBar.progress = goal.status
            goalViewHolder.goalStatusTw.text = goal.status.toString() + getString(R.string.goal_activity_status_template)

            goalViewHolder.cloneBtn.visibility = View.GONE
            goalViewHolder.contributeBtn.visibility = View.GONE
            goalViewHolder.guideline.visibility = View.INVISIBLE
            goalViewHolder.goalDescription.maxLines = 2
            goalViewHolder.expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)

            goalViewHolder.itemView.setOnClickListener {
                val goalId = goal.id
                val intent = Intent(activity, GoalActivity::class.java)
                intent.putExtra("goal_id", goalId)
                startActivity(intent)
            }
            goalViewHolder.cloneBtn.setOnClickListener {
                val intent = Intent(activity, CreateGoalActivity::class.java)
                intent.putExtra("title", goal.title)
                intent.putExtra("topic", goal.topic)
                intent.putExtra("description", goal.description)
                startActivity(intent)
            }
            goalViewHolder.contributeBtn.setOnClickListener {
                val goalId = goal.id
                val intent = Intent(activity, CreateRecommendationActivity::class.java)
                intent.putExtra("goal_id", goalId)
                startActivity(intent)
            }
        }

        fun sortRecyclerView(filter: Filter){
            mGoals = sortItems(filter)
            notifyDataSetChanged()
        }

        private fun sortItems(filter: Filter) : List<Goal> {
            val newList = ArrayList<Goal>(unsortedList)
            when (filter) {
                Filter.PROXIMITY -> newList.sortBy { goal -> goal.location.distanceTo(currentLocation) }
                Filter.TOPIC -> newList.sortBy { goal -> goal.topic }
                Filter.STATUS -> newList.sortBy { goal -> -goal.status }
            }
            return newList
        }

        inner class GoalViewHolder internal constructor(itemView: View, goal : Goal) : RecyclerView.ViewHolder(itemView) {
            private var isExpanded : Boolean = false

            //internal var goalCard: CardView = itemView.findViewById<View>(R.id.goal_card_view) as CardView
            internal var goalTitle: TextView = itemView.findViewById<View>(R.id.goal_card_title) as TextView
            internal var goalTopic: TextView = itemView.findViewById<View>(R.id.goal_card_topic) as TextView
            internal var goalDescription: TextView = itemView.findViewById<View>(R.id.goal_card_description) as TextView
            internal var goalProgressBar: ProgressBar = itemView.findViewById(R.id.goal_card_status_pb) as ProgressBar
            internal var expandBtn: ImageButton = itemView.findViewById(R.id.goal_card_expand_btn) as ImageButton
            internal var cloneBtn: Button = itemView.findViewById(R.id.goal_card_clone_btn) as Button
            internal var contributeBtn: Button = itemView.findViewById(R.id.goal_card_contribute_btn) as Button
            internal var goalStatusTw: TextView = itemView.findViewById(R.id.goal_card_status_tw) as TextView
            internal var guideline : Guideline = itemView.findViewById(R.id.goal_card_guideline) as Guideline

            init {
                goalDescription.maxLines = 2
                cloneBtn.visibility = View.GONE
                contributeBtn.visibility = View.GONE
                guideline.visibility = View.INVISIBLE
                expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)

                expandBtn.setOnClickListener {
                    isExpanded = !isExpanded

                    if (isExpanded) {
                        goalDescription.maxLines = 50
                        guideline.visibility = View.GONE
                        expandBtn.setImageResource(R.drawable.ic_expand_less_black_36dp)

                        if(model.userId != null) {
                            cloneBtn.visibility = View.VISIBLE
                            contributeBtn.visibility = View.VISIBLE
                        }
                    }
                    else{
                        goalDescription.maxLines = 2
                        cloneBtn.visibility = View.GONE
                        contributeBtn.visibility = View.GONE
                        guideline.visibility = View.INVISIBLE
                        expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)
                    }
                }
            }
        }
    }
}