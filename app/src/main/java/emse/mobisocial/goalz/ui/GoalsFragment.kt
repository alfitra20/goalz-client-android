package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.GoalsViewModel


class GoalsFragment : Fragment() {

    private lateinit var mFirebaseAuth : FirebaseAuth
    private lateinit var filterView: MultiSelectToggleGroup
    private lateinit var model : GoalsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var recyclerViewAdapter: GoalsFragment.RecyclerViewAdapter

    private var filterOpen: Boolean = false
    private var user_id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_goals, container, false)
        setHasOptionsMenu(true)


        user_id = FirebaseAuth.getInstance().currentUser?.uid
        if (user_id != null) {
            Log.e("GOALS FRAGMENT", "USER AUTHENTICATED:" + user_id)

            // Initialize data
            filterView = view.findViewById(R.id.goals_filters) as MultiSelectToggleGroup
            recyclerView = view.findViewById(R.id.goals_recycler_view) as RecyclerView
            model = ViewModelProviders.of(this).get(GoalsViewModel::class.java)
            model.initialize(user_id!!)

            setupRecyclerView()
            initializeObservers()

        } else {
            Log.e("GOALS FRAGMENT: ", "COULD NOT GET AUTHENTICATED USER")
        }

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    override fun onDetach() {
        super.onDetach()

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
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.own_goal_card, viewGroup, false)
            return GoalViewHolder(v)
        }


        override fun onBindViewHolder(goalViewHolder: GoalViewHolder, i: Int) {
            // The data from the goal model is retrieved and bound to the card View here.
            val deadline = mGoals[i].deadline
            goalViewHolder.goalName.text = mGoals[i].title
            goalViewHolder.goalDescription.text = mGoals[i].description
            goalViewHolder.goalDeadline.text = if (deadline != null) deadline.toString() else "Not specified"
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            super.onAttachedToRecyclerView(recyclerView)
        }

        inner class GoalViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var goalCard: CardView
            internal var goalName: TextView
            internal var goalDescription: TextView
            internal var goalDeadline: TextView

            init {
                goalCard = itemView.findViewById<View>(R.id.own_goal_card_view) as CardView
                goalName = itemView.findViewById<View>(R.id.own_goal_name) as TextView
                goalDescription = itemView.findViewById<View>(R.id.own_goal_description) as TextView
                goalDeadline = itemView.findViewById<View>(R.id.own_goal_deadline) as TextView
            }
        }


    }

}
