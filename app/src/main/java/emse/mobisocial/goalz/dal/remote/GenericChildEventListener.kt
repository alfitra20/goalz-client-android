package emse.mobisocial.goalz.dal.remote

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

/**
 * Created by MobiSocial EMSE Team on 4/19/2018.
 */
class GenericChildEventListener<T, K : FirebaseData<T>>(
        private val repository: ISynchronizer<T>,
        private val klass : Class<K>)
    : ChildEventListener {

    override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
        try{
            repository.addData(parseData(dataSnapshot))
        }catch (e : Exception) {
            e.printStackTrace()
        }
    }

    override fun onChildChanged(dataSnapshot: DataSnapshot, s: String?) {
        repository.updateData(parseData(dataSnapshot))
    }

    override fun onChildRemoved(dataSnapshot: DataSnapshot) {
        repository.deleteData(parseData(dataSnapshot))
    }

    override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
        return
    }

    override fun onCancelled(databaseError: DatabaseError?) {
        Log.i("FIREBASE", "Received error from server " + databaseError.toString())
    }

    private fun parseData(dataSnapshot: DataSnapshot) : T {
        val data = dataSnapshot.getValue<K>(klass)
        return data!!.toEntity(dataSnapshot.key)
    }
}