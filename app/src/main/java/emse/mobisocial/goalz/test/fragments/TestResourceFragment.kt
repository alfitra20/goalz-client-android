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
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.test.viewmodels.ResourceTestViewModel

class TestResourceFragment : Fragment() {

    private lateinit var model : ResourceTestViewModel

    private lateinit var listView : ListView
    private lateinit var topicEt : EditText
    private lateinit var deleteIdEt : EditText
    private lateinit var deleteBtn : Button
    private lateinit var insertBtn : Button
    private lateinit var clearBtn : Button
    private lateinit var filterBtn : Button

    private lateinit var resourceList: ArrayList<Resource>
    private lateinit var listAdapter : ArrayAdapter<Resource>

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        var view = inflater!!.inflate(R.layout.fragment_test_resource, container, false)

        resourceList = ArrayList<Resource>()
        listAdapter = ArrayAdapter<Resource>(activity, R.layout.row_layout, resourceList)

        model = ViewModelProviders.of(this).get(ResourceTestViewModel::class.java)

        listView = view.findViewById(R.id.resource_test_fragment_resource_list_view)
        listView.adapter = listAdapter
        topicEt = view.findViewById(R.id.resource_test_fragment_topic_et)
        deleteIdEt = view.findViewById(R.id.resource_test_fragment_delete_id_et)
        deleteBtn = view.findViewById(R.id.resource_test_fragment_delete_btn)
        insertBtn = view.findViewById(R.id.resource_test_fragment_insert_btn)
        clearBtn = view.findViewById(R.id.resource_test_fragment_for_user_btn)
        filterBtn = view.findViewById(R.id.resource_test_fragment_filter_btn)

        initializeObservers()
        initializeEventListeners()

        return view
    }

    private fun initializeObservers() {
        model.resourceList.observe(this, Observer<List<Resource>> { users ->
            if (users != null) {
                resourceList.clear()
                resourceList.addAll(users)
                listAdapter.notifyDataSetChanged()
            }
        })
    }

    private fun initializeEventListeners() {
        filterBtn.setOnClickListener {model.applyByTopicFilter(topicEt.text.toString())}
        clearBtn.setOnClickListener {model.applyByUserFilter(topicEt.text.toString())}
        deleteBtn.setOnClickListener {model.deleteResource(deleteIdEt.text.toString().toInt())}
        insertBtn.setOnClickListener {model.createResource()}
    }
}
