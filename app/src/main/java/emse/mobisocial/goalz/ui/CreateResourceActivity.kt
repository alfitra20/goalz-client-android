package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.webkit.URLUtil
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.model.ResourceTemplate
import emse.mobisocial.goalz.ui.viewModels.CreateGoalViewModel
import kotlinx.android.synthetic.main.activity_create_goal.*
import kotlinx.android.synthetic.main.activity_create_resource.*

class CreateResourceActivity : AppCompatActivity() {

    private lateinit var model : CreateGoalViewModel
    private var userId:String? = null
    private lateinit var mSnackbar: Snackbar
    private var redColor = Color.parseColor("#FF6347")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_resource)
        supportActionBar?.title = "Create a new Resource"
        supportActionBar?.elevation = 0F

        userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            model = ViewModelProviders.of(this).get(CreateGoalViewModel::class.java)
            createResourceButton.setOnClickListener { createEventListener() }
        }else{
            Log.e("CREATE A GOAL: ", "COULD NOT GET AUTHENTICATED USER")
        }
    }
    private fun  createEventListener(){
        val newResource = ResourceTemplate(
                userId!!,
                resourceUrlText.text.toString(),
                resourceTopicText.text.toString(),
                resourceTitleText.text.toString()
        )
        addResource(newResource)
    }

    private fun addResource(newResource:ResourceTemplate){
        if(resourceTitleText.text.toString()!=""&&resourceTopicText.text.toString()!=""&&resourceUrlText.text.toString()!="") {
            if(!URLUtil.isValidUrl(resourceUrlText.text.toString())) {
                launchSnackbar("Invalid URL")
            }else {
                model.addResource(newResource).observe(this, Observer<DalResponse> { response ->
                    if(response?.status == DalResponseStatus.SUCCESS){
                        Toast.makeText(this, "Resource Successfully Created", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    if(response?.status == DalResponseStatus.FAIL){
                        Toast.makeText(this, "Resource Creation Failed", Toast.LENGTH_LONG).show()
                        finish()
                    }
                })

            }
        }else{
            launchSnackbar("Invalid Fields")
        }
    }

    private fun launchSnackbar(title: String) {
        mSnackbar = Snackbar.make(create_goal_layout, title, Snackbar.LENGTH_SHORT)
        mSnackbar.view.background = ColorDrawable(redColor)
        mSnackbar.show()
    }


}
