package emse.mobisocial.goalz

import android.app.Application
import emse.mobisocial.goalz.dal.repositories.UserRepository
import emse.mobisocial.goalz.dal.db.AppDatabase

/**
 * Android Application class. Used for accessing singletons.
 */
class GoalzApp : Application() {

    private lateinit var appExecutors: AppExecutors

    override fun onCreate() {
        super.onCreate()

        appExecutors = AppExecutors()
    }

    private val database: AppDatabase
        get() = AppDatabase.getInstance(this, appExecutors)

    val userRepository: UserRepository
        get() = UserRepository.getInstance(database.userDao(), appExecutors.diskIO())
}