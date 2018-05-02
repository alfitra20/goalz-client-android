package emse.mobisocial.goalz.ui

import android.content.Intent
import android.os.Bundle
import android.support.constraint.Guideline
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal


class GoalActivitySubgoalsFragment : Fragment() {

    private var isAuth : Boolean = false
    private lateinit var recyclerView : RecyclerView
    private var recyclerViewAdapter : RecyclerViewAdapter = RecyclerViewAdapter(ArrayList<Goal>())

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater!!.inflate(R.layout.fragment_goal_activity_subgoals, container, false)

        val layoutManager = LinearLayoutManager(activity)

        recyclerView = view.findViewById(R.id.goal_activity_list)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = layoutManager

        recyclerView.adapter = recyclerViewAdapter

        return view
    }

    fun updateContent(subgoalsList : List<Goal>, isAuth : Boolean) {
        this.isAuth = isAuth
        recyclerViewAdapter.addItems(subgoalsList)
    }

    inner class RecyclerViewAdapter(values : List<Goal>) : RecyclerView.Adapter<RecyclerViewAdapter.GoalViewHolder>() {
        private var values: List<Goal> = values

        override fun getItemCount(): Int {
            return values.size
        }

        fun addItems(newGoalsList: List<Goal>) {
            this.values = newGoalsList
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): GoalViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.goal_card, viewGroup, false)
            return GoalViewHolder(v)
        }

        override fun onBindViewHolder(goalViewHolder: GoalViewHolder, i: Int) {
            // The data from the goal model is retrieved and bound to the card View here.
            val goal = values[i]

            goalViewHolder.goalTitle.text = goal.title
            goalViewHolder.goalTopic.text = goal.topic
            goalViewHolder.goalDescription.text = goal.description
            goalViewHolder.goalProgressBar.progress = goal.status
            goalViewHolder.goalStatusTw.text = "${goal.status.toString()}${getString(R.string.goal_activity_status_template)}"

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

        inner class GoalViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private var isExpanded : Boolean = false

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

                        if(isAuth) {
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
