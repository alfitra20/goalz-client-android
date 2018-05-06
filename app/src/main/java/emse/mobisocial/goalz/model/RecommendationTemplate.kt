package emse.mobisocial.goalz.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.PrimaryKey

/**
 * Created by MobiSocial EMSE Team on 3/29/2018.
 */
data class RecommendationTemplate(
         var resourceId : String,
         var goalId : String,
         var userId : String,
         var title : String,
         var description : String,
         var reqTime : Int)