package emse.mobisocial.goalz

import android.app.Application
import android.content.Context
import android.content.Intent
import emse.mobisocial.goalz.dal.IGoalRepository
import emse.mobisocial.goalz.dal.IRecommendationRepository
import emse.mobisocial.goalz.dal.IResourceRepository
import emse.mobisocial.goalz.dal.IUserRepository
import emse.mobisocial.goalz.dal.repositories.UserRepository
import emse.mobisocial.goalz.dal.db.AppDatabase
import emse.mobisocial.goalz.dal.remote.FirebaseConnectionService
import emse.mobisocial.goalz.dal.repositories.GoalRepository
import emse.mobisocial.goalz.dal.repositories.RecommendationRepository
import emse.mobisocial.goalz.dal.repositories.ResourceRepository
import emse.mobisocial.goalz.dal.remote.ISynchronizer
import emse.mobisocial.goalz.dal.remote.synchronizers.*
import emse.mobisocial.goalz.model.*
import emse.mobisocial.goalz.notification.NotificationRegistrationService


/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 *
 * Android Application class. Used for accessing singletons.
 */
class GoalzApp : Application() {

    private lateinit var appExecutors: AppExecutors

    private val database: AppDatabase
        get() = AppDatabase.getInstance(this, appExecutors)

    // Application Repositories interfaces
    val userRepository: IUserRepository
        get() = UserRepository.getInstance(
                database.userDao(), appExecutors.diskIO(), appExecutors.networkIO())

    val resourceRepository: IResourceRepository
        get() = ResourceRepository.getInstance(database.resourceDao(),
                database.libraryDao(), appExecutors.diskIO(), appExecutors.networkIO())

    val goalRepository: IGoalRepository
        get() = GoalRepository.getInstance(
                database.goalDao(), appExecutors.diskIO(), appExecutors.networkIO())

    val recommendationRepository: IRecommendationRepository
        get() = RecommendationRepository.getInstance(
                database.recommendationDao(), appExecutors.diskIO(), appExecutors.networkIO())

    // Firebase Synchronizer interfaces
    val goalSynchronizer: ISynchronizer<Goal>
        get() = GoalSynchronizer.getInstance(database.goalDao(), appExecutors.diskIO())

    val recommendationSynchronizer: ISynchronizer<RecommendationMinimal>
        get() = RecommendationSynchronizer.getInstance(
                    database.recommendationDao(), appExecutors.diskIO())

    val resourcesSynchronizer: ISynchronizer<Resource>
        get() = ResourceSynchronizer.getInstance(database.resourceDao(), appExecutors.diskIO())

    val librarySynchronizer: ISynchronizer<LibraryEntry>
        get() = LibrarySynchronizer.getInstance(database.libraryDao(), appExecutors.diskIO())

    val userSynchronizer: ISynchronizer<User>
        get() = UserSynchronizer.getInstance(database.userDao(), appExecutors.diskIO())


    override fun onCreate() {
        super.onCreate()

        appExecutors = AppExecutors()
        startService(Intent(this, FirebaseConnectionService::class.java))
        startService(Intent(this, NotificationRegistrationService::class.java))
    }
}