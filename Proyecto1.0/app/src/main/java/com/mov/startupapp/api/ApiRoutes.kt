package com.mov.startupapp.api

import com.mov.startupapp.models.User
import com.mov.startupapp.routes.UsersRoutes

class ApiRoutes {

    val API_URL = "http://192.168.1.7:3000/api/"
    val retrofit = RetrofitClient()

    fun getUsersRoutes(): UsersRoutes{
        return retrofit.getClient(API_URL).create(UsersRoutes::class.java)
    }
}