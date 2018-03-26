package emse.mobisocial.goalz.model

/**
 * Created by dtoni on 3/25/2018.
 */

private const val NEW_REC_ID = 0
private const val NEW_REC_RATING : Double = 0.0

data class Recommendation(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        var id : Int, var resourceId : Int, var goalId : Int, var userId : Int,
        var title : String, var description : String, var reqTime : Int, var rating : Double) {

    constructor(
            // This constructor should be used by the application logic to create new goals
            resourceId : Int, goalId : Int, userId : Int,
            title : String, description : String, reqTime : Int
    ) : this(NEW_REC_ID, resourceId, goalId, userId, title, description, reqTime, NEW_REC_RATING)

}