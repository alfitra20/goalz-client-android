package emse.mobisocial.goalz.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Relation

/**
 * Created by MobiSocial EMSE Team on 3/29/2018.
 */

// When a recommendation has this rating value it is not taken into account for:
//      - The rating of the user who posted it
//      - The rating of the resource to which it refers
const val DEFAULT_RECOMMENDATION_RATING = -100

data class Recommendation (
        @ColumnInfo(name = "recommendation_id")
        var id : String, //DO NOT UPDATE
        @ColumnInfo(name = "resource_id")
        var resourceId : String, //DO NOT UPDATE
        @ColumnInfo(name = "goal_id")
        var goalId : String, //DO NOT UPDATE
        @ColumnInfo(name = "user_id")
        var userId : String, //DO NOT UPDATE
        @ColumnInfo(name = "title")
        var title : String, //DO NOT UPDATE
        @ColumnInfo(name = "description")
        var description : String, //DO NOT UPDATE
        @ColumnInfo(name = "req_time")
        var reqTime : Int, //DO NOT UPDATE
        @ColumnInfo(name = "rating")
        var rating : Double){

    @Relation(parentColumn = "resource_id", entityColumn = "resource_id", entity = Resource::class)
    var resource : List<Resource> = ArrayList<Resource>()
}