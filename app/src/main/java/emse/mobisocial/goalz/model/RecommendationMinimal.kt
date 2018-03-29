package emse.mobisocial.goalz.model

import android.arch.persistence.room.*

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Entity(tableName = "recommendations",
        foreignKeys = [
                (ForeignKey(entity = Goal::class,
                parentColumns = [("goal_id")],
                childColumns = [("goal_id")],
                onDelete = ForeignKey.CASCADE)),

                (ForeignKey(entity = Resource::class,
                parentColumns = [("resource_id")],
                childColumns = [("resource_id")],
                onDelete = ForeignKey.CASCADE)),

                (ForeignKey(entity = UserMinimal::class,
                parentColumns = [("user_id")],
                childColumns = [("user_id")],
                onDelete = ForeignKey.CASCADE))
        ],
        indices = [
                (Index(value = ["goal_id"])),
                (Index(value = ["user_id"])),
                (Index(value = ["resource_id"]))
        ]
)
data class RecommendationMinimal(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "recommendation_id")
        var id : Int,
        @ColumnInfo(name = "resource_id")
        var resourceId : Int,
        @ColumnInfo(name = "goal_id")
        var goalId : Int,
        @ColumnInfo(name = "user_id")
        var userId : Int,
        @ColumnInfo(name = "title")
        var title : String,
        @ColumnInfo(name = "description")
        var description : String,
        @ColumnInfo(name = "req_time")
        var reqTime : Int,
        @ColumnInfo(name = "rating")
        var rating : Double){
}