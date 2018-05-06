package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.constraint.Guideline
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup
import com.nex3z.togglebuttongroup.button.LabelToggle

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.ui.dialogs.GoalDeleteDialog
import emse.mobisocial.goalz.ui.viewModels.MyGoalsViewModel
import java.util.*
import kotlin.collections.ArrayList


class MyGoalsFragment : Fragment() {

    enum class Filter {NONE, TOPIC, DEADLINE, RESOURCES}

    private lateinit var filterViewLayout : LinearLayout
    private lateinit var filterView: MultiSelectToggleGroup
    private lateinit var modelMy: MyGoalsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewAdapter: MyGoalsFragment.RecyclerViewAdapter

    private lateinit var topicFilter: LabelToggle
    private lateinit var deadlineFilter: LabelToggle
    private lateinit var resourcesCountFilter: LabelToggle

    private var filterOpen: Boolean = false
    private var userId: String? = null
    private var selectedFilter = Filter.NONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_goals, container, false)
        setHasOptionsMenu(true)

        recyclerView = view.findViewById(R.id.goals_recycler_view) as RecyclerView

        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Log.d("TEST", "HERE")
            activity.finish()
            return null
        }

        Log.d("TEST", "AND HERE")

        // Initialize data
        filterViewLayout = view.findViewById(R.id.goals_filters_layout) as LinearLayout
        filterView = view.findViewById(R.id.goals_filters) as MultiSelectToggleGroup

        modelMy = ViewModelProviders.of(this).get(MyGoalsViewModel::class.java)
        modelMy.initialize(userId!!)

        deadlineFilter = view.findViewById(R.id.order_goals_deadline)
        topicFilter = view.findViewById(R.id.order_goals_topic)
        resourcesCountFilter = view.findViewById(R.id.order_goals_resources_count)
        deadlineFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        topicFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        resourcesCountFilter.setTextColor(activity.resources.getColor(R.color.colorPrimary))
        deadlineFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)
        topicFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)
        resourcesCountFilter.markerColor = activity.resources.getColor(R.color.colorSecondary)

        setupRecyclerView()
        initializeObservers()
        initializeListeners()

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
        modelMy.goalsList.observe(this, Observer<List<Goal>> { goals ->
            if (goals != null) {
                recyclerViewAdapter.addGoals(goals, selectedFilter)
            }
        })
        modelMy.recommendationsList.observe(this, Observer<List<Recommendation>> { recommendations ->
            if (recommendations != null) {
                recyclerViewAdapter.addRecommendations(recommendations)
            }
        })
    }

    private fun initializeListeners() {
        filterView.setOnCheckedChangeListener { _, checkedId, isChecked ->
            if (isChecked){
                when(checkedId) {
                    deadlineFilter.id -> {
                        if(topicFilter.isChecked) {topicFilter.isChecked = false}
                        if(resourcesCountFilter.isChecked) {resourcesCountFilter.isChecked = false}
                        selectedFilter = Filter.DEADLINE
                    }
                    topicFilter.id -> {
                        if(deadlineFilter.isChecked) {deadlineFilter.isChecked = false}
                        if(resourcesCountFilter.isChecked) {resourcesCountFilter.isChecked = false}
                        selectedFilter = Filter.TOPIC
                    }
                    resourcesCountFilter.id -> {
                        if(topicFilter.isChecked) {topicFilter.isChecked = false}
                        if(deadlineFilter.isChecked) {deadlineFilter.isChecked = false}
                        selectedFilter = Filter.RESOURCES
                    }
                }
                recyclerViewAdapter.sortRecyclerView(selectedFilter)
            } else if (!isChecked && !topicFilter.isChecked && !deadlineFilter.isChecked && !resourcesCountFilter.isChecked) {
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
                if (userId != null) {
                    modelMy.searchGoalsForUser(searchQuery!!, userId!!)
                }
                return true
            }

            override fun onQueryTextChange(searchQuery: String?): Boolean {
                if (userId != null ) {
                    modelMy.searchGoalsForUser(searchQuery!!, userId!!)
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

    inner class RecyclerViewAdapter(goalsParam: ArrayList<Goal>, recommendationsParam: ArrayList<Recommendation>) : RecyclerView.Adapter<RecyclerViewAdapter.GoalViewHolder>() {
        private var mGoals: List<Goal> = goalsParam
        private var unsortedList: List<Goal> = goalsParam
        private var mRecommendations: List<Recommendation> = recommendationsParam

        override fun getItemCount(): Int {
            return mGoals.size
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GoalViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.own_goal_card, viewGroup, false)
            return GoalViewHolder(v)
        }

        override fun onBindViewHolder(goalViewHolder: GoalViewHolder, i: Int) {
            // The data from the goal modelMy is retrieved and bound to the card View here.
            val goal = mGoals[i]

            var deadline = "Not specified"
            if (goal.deadline != null) {
                deadline = DateFormat.format("dd MMM yyyy", goal.deadline) as String
            }

            goalViewHolder.titleTw.text = goal.title
            goalViewHolder.topicTw.text = goal.topic
            goalViewHolder.deadlineTw.text = "${getString(R.string.own_goals_card_deadline_text)}$deadline"
            goalViewHolder.resourcesTw.text = "${getString(R.string.own_goals_card_resources_text)}${countRecommendationsForGoal(goal.id)}"
            goalViewHolder.descriptionTw.text = goal.description
            goalViewHolder.statusPb.progress = goal.status
            goalViewHolder.statusTw.text = goal.status.toString() + getString(R.string.goal_activity_status_template)

            goalViewHolder.addResourceBtn.visibility = View.GONE
            goalViewHolder.updateBtn.visibility = View.GONE
            goalViewHolder.deleteBtn.visibility = View.GONE
            goalViewHolder.guideline.visibility = View.INVISIBLE
            goalViewHolder.descriptionTw.maxLines = 2
            goalViewHolder.expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)

            goalViewHolder.itemView.setOnClickListener {
                val goalId = goal.id
                val intent = Intent(activity, GoalActivity::class.java)
                intent.putExtra("goal_id", goalId)
                startActivity(intent)
            }
            goalViewHolder.addResourceBtn.setOnClickListener {
                val intent = Intent(activity, CreateRecommendationActivity::class.java)
                intent.putExtra("goal_id", goal.id)
                startActivity(intent)
            }
            goalViewHolder.updateBtn.setOnClickListener {
                val intent = Intent(activity, EditGoalActivity::class.java)
                intent.putExtra("goal_id", goal.id)
                startActivity(intent)
            }
            goalViewHolder.deleteBtn.setOnClickListener {
                val dialogFragment = GoalDeleteDialog()
                val args = Bundle()

                args.putString("goal_id", goal.id)
                dialogFragment.arguments = args

                dialogFragment.show(activity.fragmentManager, getString(R.string.goal_activity_delete_dialog_tag))
            }
        }

        fun addGoals(newGoalsList: List<Goal>, filter: Filter) {
            this.unsortedList = newGoalsList
            this.mGoals = sortItems(filter)
            notifyDataSetChanged()
        }

        fun addRecommendations(newRecommendationsList: List<Recommendation>) {
            this.mRecommendations = newRecommendationsList
            notifyDataSetChanged()
        }

        fun sortRecyclerView(filter: Filter){
            mGoals = sortItems(filter)
            notifyDataSetChanged()
        }

        private fun countRecommendationsForGoal(goal_id: String): Int {
            return mRecommendations.count { it.goalId == goal_id }
        }

        private fun sortItems(filter: Filter) : List<Goal> {
            var newList : List<Goal> = ArrayList<Goal>(unsortedList)
            when (filter) {
                Filter.DEADLINE -> newList = ArrayList<Goal>(mGoals).sortedWith(compareBy<Goal,Long?>(nullsLast(), { it.deadline?.time }))
                Filter.TOPIC -> (newList as ArrayList<Goal>).sortBy { goal -> goal.topic }
                Filter.RESOURCES -> (newList as ArrayList<Goal>).sortBy { goal -> -countRecommendationsForGoal(goal.id) }
            }
            return newList
        }

        inner class GoalViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var isExpanded : Boolean = false

            internal var titleTw: TextView = itemView.findViewById(R.id.own_goal_card_title)
            internal var topicTw: TextView = itemView.findViewById(R.id.own_goal_card_topic)
            internal var deadlineTw: TextView = itemView.findViewById(R.id.own_goal_card_deadline)
            internal var resourcesTw: TextView = itemView.findViewById(R.id.own_goal_card_resources)
            internal var descriptionTw: TextView = itemView.findViewById(R.id.own_goal_card_description)
            internal var statusPb: ProgressBar = itemView.findViewById(R.id.own_goal_card_status_pb)
            internal var statusTw: TextView = itemView.findViewById(R.id.own_goal_card_status_tw)
            internal var addResourceBtn: Button = itemView.findViewById(R.id.own_goal_card_add_resource_btn)
            internal var updateBtn: Button = itemView.findViewById(R.id.own_goal_card_update_btn)
            internal var deleteBtn: Button = itemView.findViewById(R.id.own_goal_card_delete_btn)
            internal var expandBtn: ImageButton = itemView.findViewById(R.id.own_goal_card_expand_btn)
            internal var guideline : Guideline = itemView.findViewById(R.id.own_goal_card_guideline) as Guideline

            init {
                descriptionTw.maxLines = 2
                addResourceBtn.visibility = View.GONE
                updateBtn.visibility = View.GONE
                deleteBtn.visibility = View.GONE
                guideline.visibility = View.INVISIBLE
                expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)

                expandBtn.setOnClickListener {
                    Log.d("CARD","Click occurred")
                    isExpanded = !isExpanded

                    if (isExpanded) {
                        descriptionTw.maxLines = 50
                        guideline.visibility = View.GONE
                        expandBtn.setImageResource(R.drawable.ic_expand_less_black_36dp)

                        addResourceBtn.visibility = View.VISIBLE
                        updateBtn.visibility = View.VISIBLE
                        deleteBtn.visibility = View.VISIBLE
                    }
                    else{
                        descriptionTw.maxLines = 2
                        addResourceBtn.visibility = View.GONE
                        updateBtn.visibility = View.GONE
                        deleteBtn.visibility = View.GONE
                        guideline.visibility = View.INVISIBLE
                        expandBtn.setImageResource(R.drawable.ic_expand_more_black_36dp)
                    }
                }
            }
        }


    }

}
