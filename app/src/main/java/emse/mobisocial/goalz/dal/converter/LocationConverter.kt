package emse.mobisocial.goalz.dal.converter

import android.arch.persistence.room.TypeConverter
import android.location.Location
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 3/29/2018.
 *
 * Code taken from https://commonsware.com/AndroidArch/previews/room-and-custom-types
 */
class LocationConverter {
    companion object {
        @TypeConverter
        @JvmStatic
        fun toString(location: Location): String {
            return String.format(Locale.US, "%f,%f", location.latitude, location.longitude)
        }

        @TypeConverter
        @JvmStatic
        fun toLocation(coordinates: String): Location {
            val pieces = coordinates.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var result = Location("")

            result.latitude = pieces[0].toDouble()
            result.longitude = pieces[1].toDouble()

            return result
        }
    }
}

