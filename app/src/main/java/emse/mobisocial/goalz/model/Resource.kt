package emse.mobisocial.goalz.model

/**
 * Created by dtoni on 3/25/2018.
 */

private const val NEW_RESOURCE_ID = 0
private const val NEW_RESOURCE_RATING : Double = 0.0
private const val NEW_AVG_REQ_TIME = -1

data class Resource private constructor(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        var id : Int, var link : String, var title : String,
        var topic : String, var rating : Double, var avgReqTime : Int) {

    constructor(
        // This constructor should be used by the application logic to create new goals
        link : String, title : String, topic : String
    ) : this(NEW_RESOURCE_ID, link, title, topic, NEW_RESOURCE_RATING, NEW_AVG_REQ_TIME)

}