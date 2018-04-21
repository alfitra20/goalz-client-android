package emse.mobisocial.goalz.dal.remote.data

import emse.mobisocial.goalz.dal.remote.FirebaseData
import emse.mobisocial.goalz.model.RecommendationMinimal
import emse.mobisocial.goalz.model.RecommendationTemplate

/**
 * Created by MobiSocial EMSE Team on 4/19/2018.
 */
class RecommendationFb constructor(): FirebaseData<RecommendationMinimal> {

    var id : String? = null
    var resource_id : String? = null
    var goal_id : String? = null
    var user_id : String? = null
    var title : String? = null
    var description : String? = null
    var reqTime : Int? = null
    var rating : Double = 0.0

    constructor(template : RecommendationTemplate) : this() {
        user_id = template.userId
        resource_id = template.resourceId
        goal_id = template.goalId
        user_id = template.userId
        title = template.title
        description = template.description
        reqTime = template.reqTime
    }

    override fun toEntity(id: String): RecommendationMinimal {
        return RecommendationMinimal(
                id, resource_id!!, goal_id!!, user_id!!, title!!, description!!, reqTime!!, rating)
    }

}