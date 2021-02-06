package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.AsteroidApiStatus
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.AsteroidDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.await
import java.text.SimpleDateFormat
import java.util.*

class AsteroidRepository(private val database: AsteroidDao) {
    private var today = "";
    private var plus7Days = "";
    private val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())

    val status = MutableLiveData<AsteroidApiStatus>()
    val todayAsteroids: LiveData<List<Asteroid>> = database.getByDay(today)
    val nextWeekAsteroids: LiveData<List<Asteroid>> = database.getByPeriod(today, plus7Days)
    val savedAsteroids: LiveData<List<Asteroid>> = database.getAll()

    init {
        val calendar = Calendar.getInstance()

        today = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        plus7Days = dateFormat.format(calendar.time)
    }

    suspend fun refresh() {
        status.value = AsteroidApiStatus.LOADING

        withContext(Dispatchers.IO) {
            val result = AsteroidApi.retrofitService.getAsteroids().await()

            database.insertAll(
                parseAsteroidsJsonResult(
                    JSONObject(result)
                )
            )
        }

        status.value = AsteroidApiStatus.DONE
    }

    suspend fun deleteFromPreviousDay() {
        val calendar = Calendar.getInstance()

        calendar.add(Calendar.DAY_OF_YEAR, -1)

        val yesterday = dateFormat.format(calendar.time)

        withContext(Dispatchers.IO) {
            database.deleteByDay(yesterday)
        }
    }
}