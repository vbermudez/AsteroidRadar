package com.udacity.asteroidradar.adapter

import com.udacity.asteroidradar.Asteroid

class AsteroidListener(val clickListener: (asteroid: Asteroid) -> Unit) {
    fun onClick(asteroid: Asteroid) = clickListener(asteroid)
}