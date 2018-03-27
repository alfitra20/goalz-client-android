package emse.mobisocial.goalz.model

import java.util.*

private const val NEW_GOAL_ID = 0
private const val NEW_GOAL_STATUS = 0

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
data class Goal private constructor(
        var id : Int, var userId : Int, var parentId : Int,
        var title : String, var topic : String, var description : String) {

    var goalDetails : GoalDetails? = null

    constructor(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        id : Int, userId : Int, parentId : Int,
        title : String, topic : String, description : String,
        goalDetails: GoalDetails
    ) : this(id, userId, parentId, title, topic, description){
        this.goalDetails = goalDetails
    }

    constructor(
        // This constructor should be used by the application logic to create new goals
        userId : Int, parentId : Int,
        title : String, topic : String, description : String,
        deadline : Date? = null
    ) : this(NEW_GOAL_ID, userId, parentId, title, topic, description){
        this.goalDetails = GoalDetails(NEW_GOAL_ID, NEW_GOAL_STATUS, deadline)
    }
}