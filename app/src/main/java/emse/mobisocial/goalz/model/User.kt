package emse.mobisocial.goalz.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Entity(tableName = "users")
data class User(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "user_id")
        var id : Int, //DO NOT UPDATE
        @ColumnInfo(name = "nickname")
        var nickname : String, //DO NOT UPDATE
        @ColumnInfo(name = "rating")
        var rating : Double, //DO NOT UPDATE
        @ColumnInfo(name = "website")
        var website : String?,
        @ColumnInfo(name = "registrationDate")
        var registrationDate : Date? ) //DO NOT UPDATE