package com.ozantok.depremtakipapp.data.model

import com.google.gson.annotations.SerializedName

data class EarthquakeResponse(
    @SerializedName("eventID")
    val eventId: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("depth")
    val depth: Double,
    @SerializedName("magnitude")
    val magnitude: Double,
    @SerializedName("date")
    val date: String,
    @SerializedName("time")
    val time: String
)