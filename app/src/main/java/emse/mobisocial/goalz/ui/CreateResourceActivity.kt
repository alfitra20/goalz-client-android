package emse.mobisocial.goalz.ui

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.webkit.URLUtil
import android.widget.Toast
import emse.mobisocial.goalz.R
import emse.mobisocial.goalz.model.ResourceTemplate
import emse.mobisocial.goalz.ui.viewModels.FABGoalResourceVM
import kotlinx.android.synthetic.main.activity_create_resource.*

class CreateResourceActivity : AppCompatActivity() {

    private lateinit var model : FABGoalResourceVM

    // Temporary
    private val USER_ID = 1
    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
        override fun afterTextChanged(p0: Editable?) {
            checkEmptyField()
        }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_resource)
        supportActionBar?.title = "Create a new Resource"
        supportActionBar?.elevation = 0F

        model = ViewModelProviders.of(this).get(FABGoalResourceVM::class.java)

        createResourceButton.isEnabled = false
        resourceTitleText.addTextChangedListener(textWatcher)
        resourceTopicText.addTextChangedListener(textWatcher)
        resourceUrlText.addTextChangedListener(textWatcher)

        createResourceButton.setOnClickListener {createEventListener() }
    }
    private fun  createEventListener(){
        val newResource = ResourceTemplate(
                USER_ID,
                resourceUrlText.text.toString(),
                resourceTopicText.text.toString(),
                resourceTitleText.text.toString()
        )
        model.addResource(newResource)
        Toast.makeText(this, "Resource Successfully Created", Toast.LENGTH_LONG).show()
        finish()
    }

    private fun checkEmptyField(){
        if(resourceTitleText.text.toString()!=""&&resourceTopicText.text.toString()!=""&&resourceUrlText.text.toString()!="") {
            createResourceButton.isEnabled = true
        }else {
            createResourceButton.isEnabled = false
        }
        if(!URLUtil.isValidUrl(resourceUrlText.text.toString())) {
            createResourceButton.isEnabled = false
        }
    }


}
