package emse.mobisocial.goalz.dal.converter

import android.arch.persistence.room.TypeConverter
import emse.mobisocial.goalz.model.Gender
import java.util.*

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
class GenderConverter {
    @TypeConverter
    fun toString(gender: Gender): String {
        return gender.name
    }

    @TypeConverter
    fun toGender(string : String): Gender {
        return Gender.valueOf(string)
    }
}