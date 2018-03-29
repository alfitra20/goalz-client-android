package emse.mobisocial.goalz.model

import android.arch.persistence.room.*
import android.location.Location
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Entity(tableName = "goals",
        foreignKeys = [
                (ForeignKey(entity = Goal::class,
                        parentColumns = [("goal_id")],
                        childColumns = [("parent_id")],
                        onDelete = ForeignKey.CASCADE)),
                (ForeignKey(entity = UserMinimal::class,
                        parentColumns = [("user_id")],
                        childColumns = [("user_id")],
                        onDelete = ForeignKey.CASCADE))
        ],
        indices = [(Index(value = ["parent_id"])), (Index(value = ["user_id"]))]
)
data class Goal constructor(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "goal_id")
        var id : Int, // DO NOT UPDATE
        @ColumnInfo(name = "user_id")
        var userId : Int, // DO NOT UPDATE
        @ColumnInfo(name = "parent_id")
        var parentId : Int?, // DO NOT UPDATE
        @ColumnInfo(name = "title")
        var title : String,
        @ColumnInfo(name = "topic")
        var topic : String,
        @ColumnInfo(name = "description")
        var description : String,
        @ColumnInfo(name = "location")
        var location : Location,
        @ColumnInfo(name = "status")
        var status : Int,
        @ColumnInfo(name = "deadline")
        var deadline : Date?)