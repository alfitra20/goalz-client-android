package emse.mobisocial.goalz.dal.db

import emse.mobisocial.goalz.dal.converter.LocationConverter
import emse.mobisocial.goalz.model.*
import emse.mobisocial.goalz.util.Gender
import java.text.SimpleDateFormat
import java.util.*


private fun parseDate(date : String) : Date {
    return SimpleDateFormat("dd/MM/yyyy").parse(date);
}

private val users = arrayOf(
        UserMinimal(1, "Toniuc", 2.4, null, Date()),
        UserMinimal(2, "Hamek", 1.7, "www.react.com", Date()),
        UserMinimal(3, "Alfitra", 3.8, "www.music.com", Date()),
        UserMinimal(4, "Sabina", 2.1, null, Date())
)

private val userDetails = arrayOf(
        UserDetails(1, "Daniel", "Toniuc", "dtoniuc@gmail.com", 23, Gender.MALE),
        UserDetails(2, "Chouaib", "Hamek", "randomEmail@oulu.fi", 38, Gender.UNDEFINED),
        UserDetails(3, "Alfitra", "Rahman", "rahman@indonesia.com", 18, Gender.MALE),
        UserDetails(4, "Sabina", "Fataliyeva", "sabina@oulu.fi", 24, Gender.FEMALE)
)

private val resources = arrayOf(
        Resource(1, "www.resource1.com", "First Resource", "random", 3.4, 50),
        Resource(2, "www.google.com", "Dummy resource", "search", 0.2, 10),
        Resource(3, "www.stackoverflow.com", "Profi", "search", 1.5, 30),
        Resource(4, "www.scopus.com", "Uni Database", "random", 2.4, 110)
)

private val goals = arrayOf(
        Goal(1, 1, null, "MyTopGoal", "sport", "I want to learn a new sport", LocationConverter.toLocation("65.010836,25.490361"), 0, null),
        Goal(2, 1, 1, "MySubGoal-Basketball", "sport", "This is cool", LocationConverter.toLocation("65.054859,25.472305"), 70, parseDate("15/05/2018")),
        Goal(3, 1, 1, "MySubGoal-Swimming", "sport", "Hard, but why not", LocationConverter.toLocation("65.009067,25.498739"), 10, parseDate("31/09/2018")),
        Goal(4, 1, 3, "MySubGoal-Swimming-Freestyle", "sport", "Easiest way", LocationConverter.toLocation("65.009067,25.498739"),40, parseDate("20/07/2018")),
        Goal(5, 3, null, "Music goal", "music", "Because I can", LocationConverter.toLocation("1.347763,103.862217"),64, null),
        Goal(6, 3, 5, "Play piano", "music", "Like Beethoven", LocationConverter.toLocation("65.049201, 25.472006"), 25, parseDate("14/12/2020")),
        Goal(7, 2, null, "Cook rice", "cooking", "Good food is healthy", LocationConverter.toLocation("36.741450,3.087247"), 83, parseDate("31/08/2018")),
        Goal(8, 4, null, "Learn Django", "programming", "Because unicorns", LocationConverter.toLocation("65.030254,25.411079"), 100, parseDate("05/06/2018"))
)

private val recommendations = arrayOf(
        RecommendationMinimal(1, 1,1,3,"Sport is random", "Try random things", 20, 3.2),
        RecommendationMinimal(2,2,1,4, "Google it","It always helps",120,1.5),
        RecommendationMinimal(3,2,1,2, "Google is helpful","It's simple and fast",10,2.1),
        RecommendationMinimal(4,3,8,1, "Holy grail of programming","Everything is here",200,4.7),
        RecommendationMinimal(5,4,8,2, "Science is important","Literature corpus",400,2.8)
)



/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
abstract class DataGenerator {

    companion object {
        fun generateUsers() : List<UserMinimal> {
            return users.toList()
        }

        fun generateUserDetails() : List<UserDetails> {
            return userDetails.toList()
        }

        fun generateResources() : List<Resource> {
            return resources.toList()
        }

        fun generateGoals() : List<Goal> {
            return goals.toList()
        }

        fun generateRecommendations() : List<RecommendationMinimal> {
            return recommendations.toList()
        }
    }

}