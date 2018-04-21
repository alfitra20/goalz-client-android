package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.CardView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.ExploreGoalsViewModel
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.LinearLayoutManager
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.goal_card.*

class ExploreGoalsFragment : Fragment() {

    private lateinit var model : ExploreGoalsViewModel
    private lateinit var recyclerView: RecyclerView
    // An array can be used to hold the data temporarily. Maybe needed later
    // private lateinit var goalsList : ArrayList<Goal>
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_explore_goals, container, false)

        // goalsList = ArrayList<Goal>()

        model = ViewModelProviders.of(this).get(ExploreGoalsViewModel::class.java)

        recyclerView = view.findViewById(R.id.explore_goals_recycler_view) as RecyclerView

        recyclerView.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerViewAdapter = RecyclerViewAdapter(ArrayList<Goal>())
        recyclerView.adapter = recyclerViewAdapter

        initializeObservers()

        return view
    }

    private fun initializeObservers() {
        model.goalsList.observe(this, Observer<List<Goal>> { goals ->
            if (goals != null) {
                recyclerViewAdapter.addItems(goals)
            }
        })
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
            goalViewHolder.goalName.text = mGoals[i].title
            goalViewHolder.goalDescription.text = mGoals[i].description
            if (mGoals[i].status == 0){
                goalViewHolder.goalStatusImage.setImageResource(R.drawable.incomplete)
            }else{
                goalViewHolder.goalStatusImage.setImageResource(R.drawable.completed2)
            }
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            super.onAttachedToRecyclerView(recyclerView)
        }

        inner class GoalViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var goalCard: CardView
            internal var goalName: TextView
            internal var goalDescription: TextView
            internal var goalStatusImage: ImageView

            init {
                goalCard = itemView.findViewById<View>(R.id.goal_card_view) as CardView
                goalName = itemView.findViewById<View>(R.id.goal_name) as TextView
                goalDescription = itemView.findViewById<View>(R.id.goal_description) as TextView
                goalStatusImage = itemView.findViewById(R.id.status_image) as ImageView
            }
        }

    }
}