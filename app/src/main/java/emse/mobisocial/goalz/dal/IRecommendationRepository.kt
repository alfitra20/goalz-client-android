package emse.mobisocial.goalz.dal

import android.arch.lifecycle.LiveData
import emse.mobisocial.goalz.model.Recommendation
import emse.mobisocial.goalz.model.RecommendationTemplate

/**
 * Created by MobiSocial EMSE Team on 3/29/2018.
 */
interface IRecommendationRepository {

    //Query
    fun getRecommendation(id : String) : LiveData<Recommendation>

    fun getRecommendationsForGoal(goalId : String) : LiveData<List<Recommendation>>

    /**
     * Returns all the recommendations suggested to a User identified by <user_id>
     * i.e. recommendations from all the goals of the user
     */
    fun getRecommendationsForUser(userId : String) : LiveData<List<Recommendation>>

    /**
     * Returns all the recommendations suggested by a User
     * i.e. the user is the author of the recommendation
     */
    fun getRecommendationsForAuthor(userId : String) : LiveData<List<Recommendation>>

    //Insert
    fun addRecommendation(template: RecommendationTemplate) : LiveData<DalResponse>

    //Update
    //This method only updates the rating other fields are not updatable in a recommendation
    fun rateRecommendation(id : String, rating : Double) : LiveData<DalResponse>

    //Delete
    fun deleteRecommendation(id: String) : LiveData<DalResponse>
}