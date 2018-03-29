/*
 * This class is generated based on the AppDatabase class from the
 * BasicSample project. The link to the original file and the licence
 * of that class is provided below:
 *
 * https://github.com/googlesamples/android-architecture-components/blob/master/BasicSample/app/src/main/java/com/example/android/persistence/db/AppDatabase.java
 *
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package emse.mobisocial.goalz.dal.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import emse.mobisocial.goalz.AppExecutors
import emse.mobisocial.goalz.dal.converter.DateConverter
import emse.mobisocial.goalz.dal.converter.GenderConverter
import emse.mobisocial.goalz.dal.converter.LocationConverter
import emse.mobisocial.goalz.dal.db.dao.GoalDao
import emse.mobisocial.goalz.dal.db.dao.RecommendationDao
import emse.mobisocial.goalz.dal.db.dao.ResourceDao
import emse.mobisocial.goalz.dal.db.dao.UserDao
import emse.mobisocial.goalz.model.*

private const val APP_DATABASE_NAME : String = "GoalzDatabase"

/**
 * Created by MobiSocial EMSE Team on 3/27/2018.
 */
@Database(
    entities = [
        (UserMinimal::class),
        (UserDetails::class),
        (Resource::class),
        (Goal::class),
        (RecommendationMinimal::class)],
    version = 1)
@TypeConverters(DateConverter::class, GenderConverter::class, LocationConverter::class)
abstract class AppDatabase : RoomDatabase(){

    private val mIsDatabaseCreated = MutableLiveData<Boolean>()

    val databaseCreated: LiveData<Boolean>
        get() = mIsDatabaseCreated

    abstract fun userDao(): UserDao

    abstract fun goalDao(): GoalDao

    abstract fun resourceDao(): ResourceDao

    abstract fun recommendationDao(): RecommendationDao

    private fun updateDatabaseCreated(context: Context) {
        if (context.getDatabasePath(APP_DATABASE_NAME).exists()) {
            setDatabaseCreated()
        }
    }

    private fun setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true)
    }
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context, appExecutors: AppExecutors): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context, appExecutors).also {
                    INSTANCE = it
                    INSTANCE!!.updateDatabaseCreated(context.applicationContext)
                }
            }
        }

        private fun buildDatabase(appContext: Context, appExecutors: AppExecutors) : AppDatabase {
            return Room.databaseBuilder(
                    appContext.applicationContext, AppDatabase::class.java, APP_DATABASE_NAME)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            appExecutors.diskIO().execute({
                                // Add a delay to simulate a long-running operation
                                addDelay()

                                // Generate the data for pre-population
                                val users = DataGenerator.generateUsers()
                                val userDetails = DataGenerator.generateUserDetails()
                                val resources = DataGenerator.generateResources()
                                val goals = DataGenerator.generateGoals()
                                val recommendations = DataGenerator.generateRecommendations()

                                val database = getInstance(appContext, appExecutors)
                                insertData(database, users, userDetails, resources, goals, recommendations)

                                // notify that the database was created and it's ready to be used
                                database.setDatabaseCreated()
                            })
                        }
                    }).build()
        }

        private fun insertData(
                database: AppDatabase, userMinimals: List<UserMinimal>, userDetails: List<UserDetails>,
                resources: List<Resource>, goals: List<Goal>, recommendationMinimals: List<RecommendationMinimal>) {
            database.runInTransaction {
                database.userDao().insertUserMinimalList(userMinimals)
                database.userDao().insertUserDetailsList(userDetails)
                database.resourceDao().insertResourceList(resources)
                for (goal in goals){
                    database.goalDao().insertGoal(goal)
                }
                database.recommendationDao().insertRecommendationList(recommendationMinimals)
            }
        }

        private fun addDelay() {
            try {
                Thread.sleep(4000)
            } catch (ignored: InterruptedException) {
            }

        }
    }

}