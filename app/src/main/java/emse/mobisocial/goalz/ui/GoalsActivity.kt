package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.constraint.Guideline
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.ui.viewModels.GoalsViewModel
import java.util.ArrayList

class GoalsActivity : AppCompatActivity() {

    private lateinit var model : GoalsViewModel

    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerViewAdapter : RecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var userId = intent.getStringExtra("user_id") ?: finish()
        var userNickname = intent.getStringExtra("user_nickname") ?: finish()

        //Initialize model and user
        val authUserId = FirebaseAuth.getInstance().currentUser?.uid

        if(authUserId != null && authUserId == (userId as String)){
            // Normally should not reach this point. In case you do redirect to my user fragment
            // TODO: Add code here
        }
        model = ViewModelProviders.of(this).get(GoalsViewModel::class.java)
        model.initialize(userId as String, userNickname as String)

        //Initialize view
        setContentView(R.layout.activity_goals)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = "${model.userNickname}${getString(R.string.goals_activity_appbar_title)}"

        recyclerView = findViewById(R.id.goals_activity_reciclerview)

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerViewAdapter = RecyclerViewAdapter(ArrayList<Goal>())
        recyclerView.adapter = recyclerViewAdapter

        // Initialize model observers
        model.goals.observe(this, Observer<List<Goal>> { goals ->
            if (goals != null) {
                recyclerViewAdapter.addItems(goals)
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            android.R.id.home -> {
                onBackPressed();
                return true;
            }
        }

        return super.onOptionsItemSelected(item)
    }

    // Recycler view adapter
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
            val goal = mGoals[i]

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
                val intent = Intent(this@GoalsActivity, GoalActivity::class.java)
                intent.putExtra("goal_id", goalId)
                startActivity(intent)
            }
            goalViewHolder.cloneBtn.setOnClickListener {
                val intent = Intent(this@GoalsActivity, CreateGoalActivity::class.java)
                intent.putExtra("title", goal.title)
                intent.putExtra("topic", goal.topic)
                intent.putExtra("description", goal.description)
                startActivity(intent)
            }
            goalViewHolder.contributeBtn.setOnClickListener {
                val goalId = goal.id
                val intent = Intent(this@GoalsActivity, CreateRecommendationActivity::class.java)
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
