package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.model.RecommendationTemplate
import emse.mobisocial.goalz.model.Resource
import emse.mobisocial.goalz.ui.viewModels.CreateRecommendationViewModel
import java.util.*

class CreateRecommendationActivity : AppCompatActivity() {

    private lateinit var model : CreateRecommendationViewModel

    private var spinnerMap : HashMap<Int, String> = HashMap()

    private lateinit var titleEt : EditText
    private lateinit var regTimeEt : EditText
    private lateinit var descriptionEt : EditText
    private lateinit var resourceSp : Spinner
    private lateinit var postBtn : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: finish()
        val goalId = intent.getStringExtra("goal_id") ?: finish()

        //Initialize model
        model = ViewModelProviders.of(this).get(CreateRecommendationViewModel::class.java)
        model.initialize(userId as String, goalId as String)

        //Initialize view
        setContentView(R.layout.activity_create_recommendation)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true);
        supportActionBar!!.setDisplayShowHomeEnabled(true);
        supportActionBar!!.setHomeButtonEnabled(true)
        supportActionBar!!.title = getString(R.string.create_goal_activity_appbar_title)
        supportActionBar!!.elevation = 0F

        titleEt = findViewById(R.id.create_recommendation_activity_title_et)
        regTimeEt = findViewById(R.id.create_recommendation_activity_reg_time_et)
        descriptionEt = findViewById(R.id.create_recommendation_activity_description_et)
        resourceSp = findViewById(R.id.create_recommendation_activity_resource_sp)
        postBtn = findViewById(R.id.create_recommendation_activity_submit_btn)

        //Initialize model observers
        model.resourceLibrary.observe(this, UserLibraryObserver())
        postBtn.setOnClickListener(CreateBtnOnClickListener())
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

    private fun addRecommendation(template : RecommendationTemplate){
        model.addRecommendation(template).observe(this, CreateRecommendationResponseObserver())
    }

    // Action listeners
    inner class CreateBtnOnClickListener : View.OnClickListener {
        override fun onClick(p0: View?) {
            val resourceId = spinnerMap[resourceSp.selectedItemPosition]
            val title = titleEt.text.toString()
            val description = descriptionEt.text.toString()
            val regTime = regTimeEt.text.toString()

            if(!areValidFields(resourceId, title, regTime, description)) {
                showInvalidFieldsToast()
                return
            }

            val template = RecommendationTemplate(
                    resourceId!!, model.goalId!!, model.userId!!, title, description, regTime.toInt())
            //Call add method from the parent
            addRecommendation(template)
        }

        private fun areValidFields(resourceId : String?, title : String, regTime : String, description: String) : Boolean{
            if (resourceId == null) return false

            try {
                regTime.toInt()
            } catch (e : Exception){
                return false
            }

            return  resourceId != "none" && title != "" && description != "" && regTime != ""
        }

        private fun showInvalidFieldsToast() {
            Toast.makeText(this@CreateRecommendationActivity,
                    getString(R.string.create_recommendation_activity_invalid_fields_toast),
                    Toast.LENGTH_SHORT).show()
        }
    }

    // Observers
    inner class UserLibraryObserver : Observer<List<Resource>> {
        override fun onChanged(resources: List<Resource>?) {
            if (resources == null) return

            val spinnerArray = arrayOfNulls<String>(resources.size + 1)

            spinnerMap[0] = "none"
            spinnerArray[0] = "None"

            for ((iterator, goal) in resources.withIndex()) {
                spinnerMap[iterator + 1] = goal.id
                spinnerArray[iterator + 1] = goal.title
            }

            val adapter = ArrayAdapter<String>(this@CreateRecommendationActivity, R.layout.spinner_item, spinnerArray)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            resourceSp.adapter = adapter
        }
    }

    inner class CreateRecommendationResponseObserver : Observer<DalResponse> {
        override fun onChanged(response: DalResponse?) {
            if (response?.status == DalResponseStatus.SUCCESS){
                Toast.makeText(this@CreateRecommendationActivity,
                        getString(R.string.create_recommendation_activity_success_toast),
                        Toast.LENGTH_LONG).show()
                finish()
            }
            else if (response?.status == DalResponseStatus.FAIL){
                Toast.makeText(this@CreateRecommendationActivity,
                        getString(R.string.create_recommendation_activity_fail_toast),
                        Toast.LENGTH_LONG).show()
            }
        }

    }
}
