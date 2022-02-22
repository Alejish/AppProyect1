package com.mov.startupapp.models

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

class User(

    //mapear los mismos datos que contine la base de datos
    @SerializedName ("id") val id: String? = null,
    @SerializedName ("name") var name: String,
    @SerializedName ("lastname") var lastname: String,
    @SerializedName ("email") val email: String,
    @SerializedName ("phone") var phone: String,
    @SerializedName ("password") val password: String,
    @SerializedName ("image") val image: String? = null,
    @SerializedName ("session_token") val sessionToken: String? = null,
    @SerializedName ("is_available") val isAvailable: Boolean? = null,
) {

    override fun toString(): String {
        return "User(id=$id, name='$name', lastname='$lastname', email='$email', phone='$phone', password='$password', image=$image, sessionToken=$sessionToken, isAvailable=$isAvailable)"
    }

    fun toJson(): String{
        return  Gson().toJson(this)
    }
 }