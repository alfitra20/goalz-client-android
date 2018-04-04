package emse.mobisocial.goalz.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey

/**
 * Created by MobiSocial EMSE Team on 4/4/2018.
 */
@Entity(tableName = "user_library",
        foreignKeys = [
            (ForeignKey(
                    entity = UserMinimal::class,
                    parentColumns = [("user_id")],
                    childColumns = [("user_id")],
                    onDelete = ForeignKey.CASCADE)),
            (ForeignKey(
                    entity = Resource::class,
                    parentColumns = [("resource_id")],
                    childColumns = [("resource_id")],
                    onDelete = ForeignKey.CASCADE))],
        primaryKeys= ["user_id", "resource_id"])
data class LibraryEntry(
        @ColumnInfo(name = "user_id")
        var user_id : Int,
        @ColumnInfo(name = "resource_id")
        var resource_id : Int
)