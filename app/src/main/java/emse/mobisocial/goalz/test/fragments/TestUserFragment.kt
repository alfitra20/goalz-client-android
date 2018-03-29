package emse.mobisocial.goalz.test.fragments

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.User
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.model.UserDetails
import emse.mobisocial.goalz.test.viewmodels.UserTestViewModel

class TestUserFragment : Fragment() {

    private lateinit var model : UserTestViewModel

    private lateinit var listView : ListView
    private lateinit var detailsView : TextView
    private lateinit var searchEt : EditText
    private lateinit var deleteBtn : Button
    private lateinit var updateBtn : Button
    private lateinit var insertBtn : Button
    private lateinit var searchBtn : Button

    private lateinit var userMinimalList: ArrayList<UserMinimal>
    private lateinit var listAdapter : ArrayAdapter<UserMinimal>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_test_user, container, false)

        userMinimalList = ArrayList<UserMinimal>()
        listAdapter = ArrayAdapter<UserMinimal>(activity, R.layout.row_layout, userMinimalList)

        model = ViewModelProviders.of(this).get(UserTestViewModel::class.java)

        listView = view.findViewById(R.id.user_test_fragment_user_list_view)
        listView.adapter = listAdapter
        detailsView = view.findViewById(R.id.user_test_fragment_selected_user_details_view)
        searchEt = view.findViewById(R.id.user_test_fragment_search_et)
        deleteBtn = view.findViewById(R.id.user_test_fragment_delete_btn)
        updateBtn = view.findViewById(R.id.user_test_fragment_update_btn)
        insertBtn = view.findViewById(R.id.user_test_fragment_insert_btn)
        searchBtn = view.findViewById(R.id.user_test_fragment_search_btn)

        initializeObservers()
        initializeEventListeners()

        return view
    }

    private fun initializeObservers() {
        model.userList.observe(this, Observer<List<UserMinimal>> { users ->
            if (users != null) {
                userMinimalList.clear()
                userMinimalList.addAll(users)
                listAdapter.notifyDataSetChanged()
            }
        })
        model.selectedUser.observe(this, Observer<User> { userDetails ->
            if (userDetails != null) {
                detailsView.text = userDetails.toString()
            }
        })
    }

    private fun initializeEventListeners() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
            var user = adapter.getItemAtPosition(position) as UserMinimal
            model.getDetails(user.id)
        }

        deleteBtn.setOnClickListener { model.deleteSelectedUser() }
        updateBtn.setOnClickListener { model.updateSelectedUser() }
        insertBtn.setOnClickListener { model.registerUser() }
        searchBtn.setOnClickListener { model.searchByTopic(searchEt.text.toString()) }
    }
}
