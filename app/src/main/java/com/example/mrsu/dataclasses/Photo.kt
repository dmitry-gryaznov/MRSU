package com.example.mrsu.dataclasses

import com.google.gson.annotations.SerializedName

data class Photo(
    @SerializedName("UrlSmall")
    val urlSmall: String,

    @SerializedName("UrlMedium")
    val urlMedium: String,

    @SerializedName("UrlSource")
    val urlSource: String
)
