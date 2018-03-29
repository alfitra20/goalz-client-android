package emse.mobisocial.goalz.test.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.test.viewmodels.GoalTestViewModel

class TestGoalFragment : Fragment() {

    private lateinit var model : GoalTestViewModel

    private lateinit var listView : ListView
    private lateinit var searchValueEt : EditText
    private lateinit var searchSubgoalsBtn : Button
    private lateinit var searchForUserBtn : Button
    private lateinit var searchByTopicBtn : Button
    private lateinit var deleteBtn : Button
    private lateinit var updateBtn : Button
    private lateinit var insertBtn : Button

    private lateinit var goalList : ArrayList<Goal>
    private lateinit var listAdapter : ArrayAdapter<Goal>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_test_goal, container, false)

        goalList = ArrayList<Goal>()
        listAdapter = ArrayAdapter<Goal>(activity, R.layout.row_layout, goalList)

        model = ViewModelProviders.of(this).get(GoalTestViewModel::class.java)

        listView = view.findViewById(R.id.goal_test_fragment_goal_list_view)
        listView.adapter = listAdapter
        searchValueEt = view.findViewById(R.id.goal_test_fragment_search_value_et)
        searchByTopicBtn = view.findViewById(R.id.goal_test_fragment_by_topic_btn)
        searchForUserBtn = view.findViewById(R.id.goal_test_fragment_for_user_btn)
        searchSubgoalsBtn = view.findViewById(R.id.goal_test_fragment_subgoal_btn)
        deleteBtn = view.findViewById(R.id.goal_test_fragment_delete_btn)
        updateBtn = view.findViewById(R.id.goal_test_fragment_update_btn)
        insertBtn = view.findViewById(R.id.goal_test_fragment_insert_btn)

        initializeObservers()
        initializeEventListeners()

        return view
    }

    private fun initializeObservers() {
        model.resourceList.observe(this, Observer<List<Goal>> { goals ->
            if (goals != null) {
                goalList.clear()
                goalList.addAll(goals)
                listAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initializeEventListeners() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
            var goal = adapter.getItemAtPosition(position) as Goal
            model.selectedGoal = goal
        }

        searchSubgoalsBtn.setOnClickListener {model.searchSubgoals(searchValueEt.text.toString().toInt())}
        searchForUserBtn.setOnClickListener {model.searchByUser(searchValueEt.text.toString().toInt())}
        searchByTopicBtn.setOnClickListener {model.searchByTopic(searchValueEt.text.toString())}
        deleteBtn.setOnClickListener {model.deleteSelectedGoal()}
        updateBtn.setOnClickListener {model.updateSelectedGoal()}
        insertBtn.setOnClickListener {model.addGoal()}
    }
}
