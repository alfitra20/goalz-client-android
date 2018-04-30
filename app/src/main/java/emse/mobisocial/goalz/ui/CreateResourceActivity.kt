package emse.mobisocial.goalz.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.URLUtil
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.dal.DalResponse
import emse.mobisocial.goalz.dal.DalResponseStatus
import emse.mobisocial.goalz.model.ResourceTemplate
import emse.mobisocial.goalz.ui.viewModels.CreateResourceViewModel
import kotlinx.android.synthetic.main.activity_create_resource.*

class CreateResourceActivity : AppCompatActivity() {

    private lateinit var model : CreateResourceViewModel
    private var userId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tmpId = FirebaseAuth.getInstance().currentUser?.uid ?: finish()

        userId = tmpId as String
        model = ViewModelProviders.of(this).get(CreateResourceViewModel::class.java)
        createResourceButton.setOnClickListener { createEventListener() }

        setContentView(R.layout.activity_create_resource)
        supportActionBar?.title = getString(R.string.create_resource_activity_appbar_title)
        supportActionBar?.elevation = 0F

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
        if (resourceTitleText.text.toString() == "" || resourceTopicText.text.toString() == "" ||
            resourceUrlText.text.toString() == "") {

            Toast.makeText(this, getString(R.string.create_resource_activity_invalid_fields_toast),
                    Toast.LENGTH_SHORT).show()
            return
        }

        if(!URLUtil.isValidUrl(resourceUrlText.text.toString())) {
            Toast.makeText(this, getString(R.string.create_resource_activity_invalid_uri_toast),
                    Toast.LENGTH_SHORT).show()
            return
        }

        model.addResource(newResource).observe(this, Observer<DalResponse> { response ->
            if(response?.status == DalResponseStatus.SUCCESS){
                Toast.makeText(this, getString(R.string.create_resource_activity_success_toast),
                        Toast.LENGTH_LONG).show()
                finish()
            }
            if(response?.status == DalResponseStatus.FAIL){
                Toast.makeText(this, getString(R.string.create_resource_activity_fail_toast), Toast.LENGTH_LONG).show()
                finish()
            }
        })
    }


}
