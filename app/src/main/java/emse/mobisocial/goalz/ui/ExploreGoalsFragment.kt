package emse.mobisocial.goalz.ui

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.support.constraint.Guideline
import android.support.v4.app.Fragment
import android.support.v7.content.res.AppCompatResources
import android.support.v7.widget.CardView

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.ExploreGoalsViewModel
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.*
import android.widget.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
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
        filterView = view.findViewById(R.id.explore_goals_filters) as MultiSelectToggleGroup
        recyclerView = view.findViewById(R.id.explore_goals_recycler_view) as RecyclerView
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

                      setCheckedColor(checkedId)
                      uncheckOthers(checkedId)
                      recyclerViewAdapter.filterRecyclerView()
                } else {
                    model.reset()
                }
            }
        })
    }


    private fun setCheckedColor(checkId: Int){
        for (id: Int in filterView.checkedIds) {
            if (id == checkId) {
                when(id) {
                    proximityFilter.id -> proximityFilter.setTextColor(R.color.colorSecondary)
                    topicFilter.id -> topicFilter.setTextColor(R.color.colorSecondary)
                    statusFilter.id -> statusFilter.setTextColor(R.color.colorSecondary)
                }
            }
        }
    }

    private fun uncheckOthers(checkId: Int) {
        for (id: Int in filterView.checkedIds) {
            if (id != checkId) {
                when(id) {
                    proximityFilter.id -> {
                        proximityFilter.isChecked = false
                        proximityFilter.setTextColor(R.color.colorPrimary)
                    }
                    topicFilter.id -> {
                        topicFilter.isChecked = false
                        topicFilter.setTextColor(R.color.colorPrimary)
                    }
                    statusFilter.id -> {
                        statusFilter.isChecked = false
                        statusFilter.setTextColor(R.color.colorPrimary)
                    }
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

        private fun filterByTopic() {
            val filterdList = ArrayList<Goal>(mGoals)
            filterdList.sortBy { goal -> goal.topic }
            this.mGoals = filterdList
            notifyDataSetChanged()
        }

        private fun filterByProximity() {
            val filterdList = ArrayList<Goal>(mGoals)
            filterdList.sortBy { goal -> goal.location.distanceTo(currentLocation) }
            this.mGoals = filterdList
            notifyDataSetChanged()
        }

        private fun filterByStatus() {
            val filterdList = ArrayList<Goal>(mGoals)
            filterdList.sortBy { goal -> -goal.status }
            this.mGoals = filterdList
            notifyDataSetChanged()
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
                    Log.d("CARD","Click occurred")
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