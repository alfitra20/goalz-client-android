package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.model.ResourceTemplate
import emse.mobisocial.goalz.ui.viewModels.FABGoalResourceVM
import kotlinx.android.synthetic.main.activity_create_resource.*

class CreateResourceActivity : AppCompatActivity() {

    private lateinit var model : FABGoalResourceVM

    // Temporary
    private val USER_ID = "FOlyCo0IILeOnfUxhZpphdYnICS2"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_resource)
        supportActionBar?.title = "Create a new Resource"
        supportActionBar?.elevation = 0F

        model = ViewModelProviders.of(this).get(FABGoalResourceVM::class.java)

        createResourceButton.setOnClickListener {createEventListener() }
    }
    private fun  createEventListener(){
        val newResource = ResourceTemplate(
                USER_ID,
                resourceUrlText.text.toString(),
                resourceTopicText.text.toString(),
                resourceTitleText.text.toString()
        )
        addResource(newResource)
    }

    private fun addResource(newResource:ResourceTemplate){
        if(resourceTitleText.text.toString()!=""&&resourceTopicText.text.toString()!=""&&resourceUrlText.text.toString()!="") {
            if(!URLUtil.isValidUrl(resourceUrlText.text.toString())) {
                Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this, "Invalid Fields", Toast.LENGTH_SHORT).show()
        }
    }


}
