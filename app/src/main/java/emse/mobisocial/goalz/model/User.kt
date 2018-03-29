package emse.mobisocial.goalz.model

import android.arch.persistence.room.ColumnInfo
import emse.mobisocial.goalz.util.Gender
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 3/28/2018.
 */
data class User(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        @ColumnInfo(name = "user_id")
        var id : Int, //DO NOT UPDATE
        @ColumnInfo(name = "nickname")
        var nickname : String, //DO NOT UPDATE
        @ColumnInfo(name = "rating")
        var rating : Double, //DO NOT UPDATE
        @ColumnInfo(name = "website")
        var website : String?,
        @ColumnInfo(name = "registrationDate")
        var registrationDate : Date?,
        @ColumnInfo(name = "first_name")
        var firstName : String,
        @ColumnInfo(name = "last_name")
        var lastName : String,
        @ColumnInfo(name = "email")
        var email : String,
        @ColumnInfo(name = "age")
        var age : Int,
        @ColumnInfo(name = "gender")
        var gender : Gender)