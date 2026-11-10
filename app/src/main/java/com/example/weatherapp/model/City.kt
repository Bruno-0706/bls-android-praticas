package com.example.weatherapp.model

import com.google.android.gms.maps.model.LatLng

data class City (
    val name : String,
    // REMOVIDO. val weather: String? = null,
    val location: LatLng? = null
)
