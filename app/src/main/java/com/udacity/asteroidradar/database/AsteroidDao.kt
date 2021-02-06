package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid

@Dao
interface AsteroidDao {
    @Insert
    suspend fun insert(asteroid: Asteroid)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(asteroids: List<Asteroid>)

    @Update
    suspend fun update(asteroid: Asteroid)

    @Query("SELECT * from asteroids_table WHERE id = :key")
    suspend fun get(key: Long): Asteroid?

    @Query("SELECT * FROM asteroids_table ORDER BY closeApproachDate ASC")
    fun getAll(): LiveData<List<Asteroid>>

    @Query(
        "SELECT * FROM asteroids_table WHERE closeApproachDate = :date ORDER BY closeApproachDate ASC"
    )
    fun getByDay(date: String): LiveData<List<Asteroid>>

    @Query(
        "SELECT * FROM asteroids_table WHERE closeApproachDate BETWEEN :startDate AND :endDate ORDER BY closeApproachDate ASC"
    )
    fun getByPeriod(startDate: String, endDate: String): LiveData<List<Asteroid>>

    @Query("DELETE FROM asteroids_table")
    suspend fun deleteAll()

    @Query("DELETE FROM asteroids_table WHERE closeApproachDate = :date")
    suspend fun deleteByDay(date: String)
}