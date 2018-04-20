package emse.mobisocial.goalz.dal.remote

import android.content.Intent
import com.google.firebase.database.*
import emse.mobisocial.goalz.GoalzApp
import android.app.Service
import android.os.IBinder
import emse.mobisocial.goalz.dal.remote.data.*
import emse.mobisocial.goalz.model.*

/**
 * Created by MobiSocial EMSE Team on 4/19/2018.
 */
class FirebaseConnectionService : Service() {

    private val firebaseDatabase : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val goalTable : DatabaseReference = firebaseDatabase.reference.child("goals")
    private val recommendationTable : DatabaseReference = firebaseDatabase.reference.child("recommendations")
    private val resourceTable : DatabaseReference = firebaseDatabase.reference.child("resources")
    private val libraryTable : DatabaseReference = firebaseDatabase.reference.child("user_library")
    private val userTable : DatabaseReference = firebaseDatabase.reference.child("users")

    private lateinit var goalEventListener : GenericChildEventListener<Goal, GoalFb>
    private lateinit var recommendationEventListener : GenericChildEventListener<RecommendationMinimal, RecommendationFb>
    private lateinit var resourceEventListener: GenericChildEventListener<Resource, ResourceFb>
    private lateinit var libraryEventListener: GenericChildEventListener<LibraryEntry, LibraryEntryFb>
    private lateinit var userEventListener: GenericChildEventListener<User, UserFb>
    
    override fun onCreate() {
        val goalSync = (application as GoalzApp).goalSynchronizer
        goalEventListener = GenericChildEventListener<Goal, GoalFb>(goalSync, GoalFb::class.java)
        goalTable.addChildEventListener(goalEventListener)

        val recommendationSync = (application as GoalzApp).recommendationSynchronizer
        recommendationEventListener = GenericChildEventListener<RecommendationMinimal, RecommendationFb>(
                recommendationSync, RecommendationFb::class.java)
        recommendationTable.addChildEventListener(recommendationEventListener)

        val resourceSync = (application as GoalzApp).resourcesSynchronizer
        resourceEventListener = GenericChildEventListener<Resource, ResourceFb>(
                resourceSync, ResourceFb::class.java)
        resourceTable.addChildEventListener(resourceEventListener)

        val librarySync = (application as GoalzApp).librarySynchronizer
        libraryEventListener = GenericChildEventListener<LibraryEntry, LibraryEntryFb>(
                librarySync, LibraryEntryFb::class.java)
        libraryTable.addChildEventListener(libraryEventListener)

        val userSync = (application as GoalzApp).userSynchronizer
        userEventListener = GenericChildEventListener<User, UserFb> (userSync, UserFb::class.java)
        userTable.addChildEventListener(userEventListener)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()

        goalTable.removeEventListener(goalEventListener)
        recommendationTable.removeEventListener(recommendationEventListener)
        resourceTable.removeEventListener(resourceEventListener)
        libraryTable.removeEventListener(libraryEventListener)
        userTable.removeEventListener(userEventListener)
    }
}

