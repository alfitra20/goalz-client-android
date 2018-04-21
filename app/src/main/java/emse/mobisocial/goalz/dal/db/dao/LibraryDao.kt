package emse.mobisocial.goalz.dal.db.dao

import android.arch.persistence.room.*
import emse.mobisocial.goalz.model.LibraryEntry
import emse.mobisocial.goalz.model.Resource

/**
 * Created by MobiSocial EMSE Team on 4/4/2018.
 */
@Dao
abstract class LibraryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertLibraryEntry(entryList: LibraryEntry)

    @Query("SELECT * FROM user_library WHERE user_id = :user_id AND resource_id = :resource_id")
    abstract fun loadLibraryEntryForDelete(user_id : String, resource_id : String): LibraryEntry?

    @Delete
    abstract fun deleteLibraryEntry(entryList: LibraryEntry)

    @Query("DELETE FROM user_library")
    abstract fun invalidateData()
}