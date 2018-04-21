package emse.mobisocial.goalz.model

import android.arch.persistence.room.*
import emse.mobisocial.goalz.model.UserMinimal
import emse.mobisocial.goalz.util.Gender

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Entity(tableName = "user_details", foreignKeys = [(
        ForeignKey(entity = UserMinimal::class,
            parentColumns = [("user_id")],
            childColumns = [("user_id")],
            onDelete = ForeignKey.CASCADE)
        )])
data class UserDetails(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        @PrimaryKey
        @ColumnInfo(name = "user_id")
        var userId : String, //DO NOT UPDATE
        @ColumnInfo(name = "first_name")
        var firstName : String,
        @ColumnInfo(name = "last_name")
        var lastName : String,
        @ColumnInfo(name = "email")
        var email : String,
        @ColumnInfo(name = "age")
        var age : Int?,
        @ColumnInfo(name = "gender")
        var gender : Gender)