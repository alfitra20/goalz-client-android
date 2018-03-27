package emse.mobisocial.goalz.test.fragments

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
import emse.mobisocial.goalz.model.UserDetails
import emse.mobisocial.goalz.test.viewmodels.UserTestViewModel

class TestUserFragment : Fragment() {

    private lateinit var model : UserTestViewModel

    private lateinit var listView : ListView
    private lateinit var detailsView : TextView
    private lateinit var deleteBtn : Button
    private lateinit var updateBtn : Button
    private lateinit var insertBtn : Button

    private lateinit var userList : ArrayList<User>
    private lateinit var listAdapter : ArrayAdapter<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_test_user, container, false)

        userList = ArrayList<User>()
        listAdapter = ArrayAdapter<User>(activity, R.layout.row_layout, userList)

        model = ViewModelProviders.of(this).get(UserTestViewModel::class.java)

        listView = view.findViewById(R.id.user_test_fragment_user_list_view)
        listView.adapter = listAdapter
        detailsView = view.findViewById(R.id.user_test_fragment_selected_user_details_view)
        deleteBtn = view.findViewById(R.id.user_test_fragment_delete_btn)
        updateBtn = view.findViewById(R.id.user_test_fragment_update_btn)
        insertBtn = view.findViewById(R.id.user_test_fragment_insert_btn)

        initializeObservers()
        initializeEventListeners()

        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }


    private fun initializeObservers() {
        model.userList.observe(this, Observer<List<User>> { users ->
            if (users != null) {
                userList.clear()
                userList.addAll(users)
                listAdapter.notifyDataSetChanged()
            }
        })
        model.selectedUserDetails.observe(this, Observer<UserDetails> { userDetails ->
            if (userDetails != null) {
                detailsView.text = userDetails.toString()
            }
        })
    }

    private fun initializeEventListeners() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
            var user = adapter.getItemAtPosition(position) as User
            model.getDetails(user)
        }

        deleteBtn.setOnClickListener {model.deleteSelectedUser()}
        updateBtn.setOnClickListener {model.updateSelectedUser()}
        insertBtn.setOnClickListener {model.registerUser()}
    }
}
