package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup
import com.nex3z.togglebuttongroup.button.LabelToggle

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.ui.viewModels.GoalsViewModel
import java.util.*


class GoalsFragment : Fragment() {

    private lateinit var filterView: MultiSelectToggleGroup
    private lateinit var model : GoalsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewAdapter: GoalsFragment.RecyclerViewAdapter

    private lateinit var topicFilter: LabelToggle
    private lateinit var deadlineFilter: LabelToggle
    private lateinit var resourcesCountFilter: LabelToggle

    private var filterOpen: Boolean = false
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_goals, container, false)
        setHasOptionsMenu(true)

        recyclerView = view.findViewById(R.id.goals_recycler_view) as RecyclerView

        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            Log.e("GOALS FRAGMENT", "USER AUTHENTICATED:" + userId)

            // Initialize data
            filterView = view.findViewById(R.id.goals_filters) as MultiSelectToggleGroup

            model = ViewModelProviders.of(this).get(GoalsViewModel::class.java)
            model.initialize(userId!!)

            deadlineFilter = view.findViewById(R.id.order_goals_deadline)
            topicFilter = view.findViewById(R.id.order_goals_topic)
            resourcesCountFilter = view.findViewById(R.id.order_goals_resources_count)

            setupRecyclerView()
            initializeObservers()
            initializeListeners()

        } else {
            Log.e("GOALS FRAGMENT: ", "COULD NOT GET AUTHENTICATED USER")
        }

        return view
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = RecyclerViewAdapter(ArrayList<Goal>(), ArrayList<Recommendation>())
        recyclerView.adapter = recyclerViewAdapter
    }

    private fun initializeObservers() {
        model.goalsList.observe(this, Observer<List<Goal>> { goals ->
            if (goals != null) {
                recyclerViewAdapter.addGoals(goals)
            }
        })
        model.recommendationsList.observe(this, Observer<List<Recommendation>> { recommendations ->
            if (recommendations != null) {
                recyclerViewAdapter.addRecommendations(recommendations)
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
                    model.reset(userId!!)
                }
            }
        })
    }

    private fun uncheckOthers(checkId: Int) {
        for (id: Int in filterView.getCheckedIds()) {
            if (id != checkId) {
                when(id) {
                    resourcesCountFilter.id -> resourcesCountFilter.setChecked(false)
                    topicFilter.id -> topicFilter.setChecked(false)
                    deadlineFilter.id -> deadlineFilter.setChecked(false)
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
                if (userId != null) {
                    model.searchGoalsForUser(searchQuery!!, userId!!)
                }
                return true
            }

            override fun onQueryTextChange(searchQuery: String?): Boolean {
                if (userId != null ) {
                    model.searchGoalsForUser(searchQuery!!, userId!!)
                }
                return true
            }
        })


        // Set up the behaviour of the filter menu item
        val filterItem = menu!!.findItem(R.id.exploreFilter)
        val r = context.resources
        val topMarginPx = r.getDimensionPixelSize(R.dimen.goals_fragment_top_margin)
        val params = recyclerView.layoutParams as ViewGroup.MarginLayoutParams
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

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

    }

    inner class RecyclerViewAdapter(goalsParam: ArrayList<Goal>, recommendationsParam: ArrayList<Recommendation>) : RecyclerView.Adapter<RecyclerViewAdapter.GoalViewHolder>() {
        private var mGoals: List<Goal> = goalsParam
        private var mRecommendations: List<Recommendation> = recommendationsParam

        override fun getItemCount(): Int {
            return mGoals.size
        }

        fun addGoals(newGoalsList: List<Goal>) {
            this.mGoals = newGoalsList
            notifyDataSetChanged()
        }

        fun addRecommendations(newRecommendationsList: List<Recommendation>) {
            this.mRecommendations = newRecommendationsList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GoalViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.own_goal_card, viewGroup, false)
            return GoalViewHolder(v)
        }


        override fun onBindViewHolder(goalViewHolder: GoalViewHolder, i: Int) {
            // The data from the goal model is retrieved and bound to the card View here.
            val deadline = mGoals[i].deadline
            val recommendations_count = countRecommendationsForGoal(mGoals[i].id)
            goalViewHolder.goalTitle.text = mGoals[i].title
            goalViewHolder.goalTopic.text = mGoals[i].topic
            goalViewHolder.goalRecommendationsCount.text = recommendations_count.toString()
            if (deadline != null) {
                val formattedDate = DateFormat.format("dd MMM yyyy", deadline) as String
                goalViewHolder.goalDeadline.text = formattedDate
            } else {
                goalViewHolder.goalDeadline.text = "Not specified"
            }
        }

        fun countRecommendationsForGoal(goal_id: String): Int {
            var count = 0
            for (r in mRecommendations){
                if (r.goalId == goal_id) {
                    count++
                }
            }
            return count
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            super.onAttachedToRecyclerView(recyclerView)
        }

        fun filterRecyclerView() {
            val checkedIds = filterView.getCheckedIds()
            for (id: Int in checkedIds) {
                when (id) {
                    deadlineFilter.id -> filterByDeadline()
                    topicFilter.id -> filterByTopic()
                    resourcesCountFilter.id -> filterByResourcesCount()
                }
            }
        }

        fun filterByTopic() {
            val filterdList = ArrayList<Goal>(mGoals)
            filterdList.sortBy { goal -> goal.topic }
            this.mGoals = filterdList
            notifyDataSetChanged()
        }

        fun filterByDeadline() {
            val filterdList = ArrayList<Goal>(mGoals).sortedWith(compareBy<Goal,Long?>(nullsLast(), { it.deadline?.time }))
            this.mGoals = filterdList
            notifyDataSetChanged()
        }

        fun filterByResourcesCount() {
            val filterdList = ArrayList<Goal>(mGoals)
            filterdList.sortBy { goal -> -countRecommendationsForGoal(goal.id) }
            this.mGoals = filterdList
            notifyDataSetChanged()
        }

        inner class GoalViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var goalCard: CardView
            internal var goalTitle: TextView
            internal var goalTopic: TextView
            internal var goalDeadline: TextView
            internal var goalRecommendationsCount: TextView

            init {
                goalCard = itemView.findViewById<View>(R.id.own_goal_card_view) as CardView
                goalTitle = itemView.findViewById<View>(R.id.own_goal_title) as TextView
                goalTopic = itemView.findViewById<View>(R.id.own_goal_topic) as TextView
                goalDeadline = itemView.findViewById<View>(R.id.own_goal_deadline) as TextView
                goalRecommendationsCount = itemView.findViewById(R.id.own_goal_recommendations_count) as TextView

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
