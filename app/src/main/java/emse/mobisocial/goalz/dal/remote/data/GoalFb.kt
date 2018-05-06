package emse.mobisocial.goalz.dal.remote.data

import emse.mobisocial.goalz.dal.db.converter.LocationConverter
import emse.mobisocial.goalz.dal.remote.FirebaseData
import emse.mobisocial.goalz.model.Goal
import emse.mobisocial.goalz.model.GoalTemplate
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 4/18/2018.
 */

class GoalFb constructor() : FirebaseData<Goal> {

    var user_id : String? = null
    var parent_id : String? = null
    var title : String? = null
    var topic : String? = null
    var description : String? = null
    var latitude : Double? = null
    var longitude : Double? = null
    var status : Int? = null
    var deadline : Long? = null

    constructor(template : GoalTemplate) : this() {
        user_id = template.userId
        parent_id = template.parentId
        title = template.title
        topic = template.topic
        description = template.description
        latitude = template.location.latitude
        longitude = template.location.longitude
        status =  template.status

        val x = template.deadline
        deadline = if (x == null) null else (x.time) / 1000
    }

    override fun toEntity(id : String) : Goal {
        var coordinates = latitude.toString() + "," + longitude.toString()
        var date : Date? = if (deadline != null) Date(deadline!!*1000) else null

        return Goal(id, user_id!!, parent_id, title!!, topic!!, description!!,
                LocationConverter.toLocation(coordinates), status!!, date)
    }
}