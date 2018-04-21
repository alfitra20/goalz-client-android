package emse.mobisocial.goalz.dal.remote.synchronizers

import emse.mobisocial.goalz.dal.db.dao.LibraryDao
import emse.mobisocial.goalz.dal.remote.ISynchronizer
import emse.mobisocial.goalz.model.LibraryEntry
import java.util.concurrent.Executor

/**
 * Created by MobiSocial EMSE Team on 4/20/2018.
 */
class LibrarySynchronizer(
        private var libraryDao: LibraryDao,
        private var diskExecutor: Executor) : ISynchronizer<LibraryEntry> {

    override fun addData(t: LibraryEntry) {
        diskExecutor.execute { libraryDao.insertLibraryEntry(t) }
    }

    override fun updateData(t: LibraryEntry) {
        return;
    }

    override fun deleteData(t: LibraryEntry) {
        diskExecutor.execute { libraryDao.deleteLibraryEntry(t) }
    }

    //Companion Object
    companion object {
        @Volatile private var INSTANCE: LibrarySynchronizer? = null

        fun getInstance(libraryDao: LibraryDao, diskExecutor: Executor) : LibrarySynchronizer {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: LibrarySynchronizer(libraryDao, diskExecutor)
            }
        }
    }
}