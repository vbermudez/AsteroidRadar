package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.api.AsteroidApiFilter
import com.udacity.asteroidradar.api.PictureOfDayApi
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApiStatus
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch
import retrofit2.await
import java.lang.Exception

class MainViewModel(database: AsteroidDao, application: Application ) : AndroidViewModel(application) {
    private val _navigateToDetail = MutableLiveData<Asteroid>()
    val navigateToDetail: LiveData<Asteroid>
        get() = _navigateToDetail

    private val _pictureOfDay = MutableLiveData<PictureOfDay>()
    val pictureOfDay: LiveData<PictureOfDay>
        get() = _pictureOfDay

    private val _filter = MutableLiveData<AsteroidApiFilter>(AsteroidApiFilter.WEEK)
    private val _repository = AsteroidRepository(database)
    private val _pictureOfDayApi = PictureOfDayApi.retrofitService

    val status = _repository.status
    val asteroids = Transformations.switchMap(_filter) {
        when (it) {
            AsteroidApiFilter.TODAY -> _repository.todayAsteroids
            AsteroidApiFilter.WEEK -> _repository.nextWeekAsteroids
            AsteroidApiFilter.ALL -> _repository.savedAsteroids
        }
    }

    init {
        viewModelScope.launch {
            try {
                _repository.refresh()

                val result = _pictureOfDayApi.getPictureOfDay().await()

                if (result.mediaType == "image") {
                    _pictureOfDay.value = result
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", e.message!!)
                status.value = AsteroidApiStatus.DONE
            }
        }
    }

    fun onDetailClicked(asteroid: Asteroid) {
        _navigateToDetail.value = asteroid
    }

    fun onDetailNavigated() {
        _navigateToDetail.value = null
    }

    fun updateFilter(filter: AsteroidApiFilter) {
        _filter.value = filter
    }

    class Factory(private val application: Application): ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                val database = AsteroidDatabase.getInstance(application)

                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database.asteroidDao, application) as T
            }
            throw IllegalArgumentException("Unable to construct ViewModel")
        }
    }
}