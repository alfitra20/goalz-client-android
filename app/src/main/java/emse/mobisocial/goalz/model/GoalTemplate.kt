package emse.mobisocial.goalz.model

import android.location.Location
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 3/28/2018.
 */
data class GoalTemplate(
        var userId : Int,
        var parentId : Int?,
        var title : String,
        var topic : String,
        var description : String,
        var location : Location,
        var status : Int = 0,
        var deadline : Date? = null)