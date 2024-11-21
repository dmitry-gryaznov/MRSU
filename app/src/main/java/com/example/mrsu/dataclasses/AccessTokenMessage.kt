package com.example.mrsu.dataclasses

import com.google.gson.annotations.SerializedName

data class AccessTokenMessage(
    @SerializedName("access_token")
    val accessToken: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("expires_in")
    val expiresIn: Int,

    @SerializedName("refresh_token")
    val refreshToken: String
)
