package emse.mobisocial.goalz.dal

import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.model.RecommendationTemplate

/**
 * Created by MobiSocial EMSE Team on 3/29/2018.
 */
interface IRecommendationRepository {

    //Query
    fun getRecommendation(id : Int) : LiveData<Recommendation>

    fun getRecommendationsForGoal(goalId : Int) : LiveData<List<Recommendation>>

    /**
     * Returns all the recommendations suggested to a User identified by <user_id>
     * i.e. recommendations from all the goals of the user
     */
    fun getRecommendationsForUser(userId : Int) : LiveData<List<Recommendation>>

    /**
     * Returns all the recommendations suggested by a User
     * i.e. the user is the author of the recommendation
     */
    fun getRecommendationsForAuthor(userId : Int) : LiveData<List<Recommendation>>

    //Insert
    fun addRecommendation(template: RecommendationTemplate) : LiveData<Int>

    //Update
    //This method only updates the rating other fields are not updatable in a recommendation
    fun rateRecommendation(id : Int, rating : Double) : LiveData<Boolean>

    //Delete
    fun deleteRecommendation(id: Int) : LiveData<Boolean>
}