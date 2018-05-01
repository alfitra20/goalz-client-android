package emse.mobisocial.goalz.ui

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal


class GoalActivitySubgoalsFragment : Fragment() {

    private lateinit var recyclerView : RecyclerView
    private var recyclerViewAdapter : RecyclerViewAdapter = RecyclerViewAdapter(ArrayList<Goal>())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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

    fun updateContent(subgoalsList : List<Goal> ) {
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
            /*// The data from the goal model is retrieved and bound to the card View here.
            goalViewHolder.goalTitle.text = values[i].title
            goalViewHolder.goalTopic.text = values[i].topic
            if (values[i].status == 0){
                goalViewHolder.goalStatusImage.setImageResource(R.drawable.incomplete)
            }else{
                goalViewHolder.goalStatusImage.setImageResource(R.drawable.completed2)
            }*/
        }

        override fun onAttachedToRecyclerView(recyclerView: RecyclerView?) {
            super.onAttachedToRecyclerView(recyclerView)
        }

        inner class GoalViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
            /*internal var goalTitle: TextView = itemView.findViewById<View>(R.id.goal_card_title) as TextView
            internal var goalTopic: TextView = itemView.findViewById<View>(R.id.goal_topic) as TextView
            internal var goalStatusImage: ImageView = itemView.findViewById(R.id.status_image) as ImageView

            init {
                itemView.setOnClickListener {
                    val goalId = values[adapterPosition].id
                    val intent = Intent(activity, GoalActivity::class.java)
                    intent.putExtra("goal_id", goalId)
                    startActivity(intent)
                }
            }*/
        }
    }
}
