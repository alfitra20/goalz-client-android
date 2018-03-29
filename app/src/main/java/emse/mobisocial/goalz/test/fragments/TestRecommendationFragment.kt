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
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.test.viewmodels.RecommendationTestViewModel

class TestRecommendationFragment : Fragment() {

    private lateinit var model: RecommendationTestViewModel

    private lateinit var listView: ListView
    private lateinit var searchValueEt: EditText
    private lateinit var searchForAuthorBtn: Button
    private lateinit var searchForUserBtn: Button
    private lateinit var searchForGoalBtn: Button
    private lateinit var deleteBtn: Button
    private lateinit var updateBtn: Button
    private lateinit var insertBtn: Button

    private lateinit var goalList: ArrayList<Recommendation>
    private lateinit var listAdapter: ArrayAdapter<Recommendation>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_test_recommendation, container, false)

        goalList = ArrayList<Recommendation>()
        listAdapter = ArrayAdapter<Recommendation>(activity, R.layout.row_layout, goalList)

        model = ViewModelProviders.of(this).get(RecommendationTestViewModel::class.java)

        listView = view.findViewById(R.id.recommendation_test_fragment_recommendation_list_view)
        listView.adapter = listAdapter
        searchValueEt = view.findViewById(R.id.recommendation_test_fragment_search_value_et)
        searchForAuthorBtn = view.findViewById(R.id.recommendation_test_fragment_for_author_btn)
        searchForUserBtn = view.findViewById(R.id.recommendation_test_fragment_for_user_btn)
        searchForGoalBtn = view.findViewById(R.id.recommendation_test_fragment_for_goal_btn)
        deleteBtn = view.findViewById(R.id.recommendation_test_fragment_delete_btn)
        updateBtn = view.findViewById(R.id.recommendation_test_fragment_update_btn)
        insertBtn = view.findViewById(R.id.recommendation_test_fragment_insert_btn)

        initializeObservers()
        initializeEventListeners()

        return view
    }

    private fun initializeObservers() {
        model.recommendationList.observe(this, Observer<List<Recommendation>> { recommendations ->
            if (recommendations != null) {
                goalList.clear()
                goalList.addAll(recommendations)
                listAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initializeEventListeners() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
            var recommendation = adapter.getItemAtPosition(position) as Recommendation
            model.selectedRecommendation = recommendation
        }

        searchForAuthorBtn.setOnClickListener { model.searchForAuthor(searchValueEt.text.toString().toInt()) }
        searchForUserBtn.setOnClickListener { model.searchForUser(searchValueEt.text.toString().toInt()) }
        searchForGoalBtn.setOnClickListener { model.searchForGoal(searchValueEt.text.toString().toInt()) }
        deleteBtn.setOnClickListener {model.deleteSelectedRecommendation()}
        updateBtn.setOnClickListener {model.updateSelectedRecommendation()}
        insertBtn.setOnClickListener {model.insertRecommendation()}
    }
}
