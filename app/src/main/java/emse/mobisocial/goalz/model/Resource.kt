package emse.mobisocial.goalz.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Entity(tableName = "resources")
data class Resource constructor(
        // This constructor is used by the data layer. DO NOT use it in any upper layers
        @PrimaryKey
        @ColumnInfo(name = "resource_id")
        var id : String, //DO NOT UPDATE
        @ColumnInfo(name = "user_id")
        var user_id : String, //AUTHOR - DO NOT UPDATE
        @ColumnInfo(name = "link")
        var link : String, //DO NOT UPDATE
        @ColumnInfo(name = "title")
        var title : String, //DO NOT UPDATE
        @ColumnInfo(name = "topic")
        var topic : String, //DO NOT UPDATE
        @ColumnInfo(name = "rating")
        var rating : Double, //DO NOT UPDATE
        @ColumnInfo(name = "avg_req_time")
        var avgReqTime : Int//DO NOT UPDATE
)