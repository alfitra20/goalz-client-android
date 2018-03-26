package emse.mobisocial.goalz.test

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import emse.mobisocial.goalz.model.UserBasic
import android.arch.lifecycle.ViewModelProviders
import android.widget.*
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.UserDetails


class UserTestActivity : AppCompatActivity() {

    private lateinit var model : UserTestViewModel

    private lateinit var listView : ListView
    private lateinit var detailsView : TextView
    private lateinit var deleteBtn : Button
    private lateinit var updateBtn : Button
    private lateinit var insertBtn : Button

    private lateinit var userList : ArrayList<UserBasic>
    private lateinit var listAdapter : ArrayAdapter<UserBasic>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_test)

        userList = ArrayList<UserBasic>()
        listAdapter = ArrayAdapter<UserBasic>(this, R.layout.row_layout, userList)

        model = ViewModelProviders.of(this).get(UserTestViewModel::class.java)

        listView = findViewById(R.id.user_test_activity_user_list_view)
        listView.adapter = listAdapter
        detailsView = findViewById(R.id.user_test_activity_selected_user_details_view)
        deleteBtn = findViewById(R.id.user_test_activity_delete_btn)
        updateBtn = findViewById(R.id.user_test_activity_update_btn)
        insertBtn = findViewById(R.id.user_test_activity_insert_btn)

        initializeObservers()
        initializeEventListenrs()
    }

    private fun initializeObservers() {
        model.userList.observe(this, Observer<List<UserBasic>> { users ->
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

    private fun initializeEventListenrs() {
        listView.onItemClickListener = AdapterView.OnItemClickListener { adapter, _, position, _ ->
            var user = adapter.getItemAtPosition(position) as UserBasic
            model.getDetails(user)
        }

        deleteBtn.setOnClickListener {model.deleteSelectedUser()}
        updateBtn.setOnClickListener {model.updateSelectedUser()}
        insertBtn.setOnClickListener {model.registerUser()}
    }
}
