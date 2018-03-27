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
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "resource_id")
        var id : Int,
        @ColumnInfo(name = "link")
        var link : String,
        @ColumnInfo(name = "title")
        var title : String,
        @ColumnInfo(name = "topic")
        var topic : String,
        @ColumnInfo(name = "rating")
        var rating : Double,
        @ColumnInfo(name = "avg_req_time")
        var avgReqTime : Int) {
}