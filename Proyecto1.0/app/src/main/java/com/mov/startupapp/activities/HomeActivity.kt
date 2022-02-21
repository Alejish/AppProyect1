package com.mov.startupapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.mov.startupapp.databinding.ActivityHomeBinding
import com.mov.startupapp.databinding.ActivityMainBinding
import com.mov.startupapp.models.User
import com.mov.startupapp.utils.SharePref

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    var sharedPref : SharePref? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPref = SharePref(this)

        getUserFromSession()

        binding.btnLogout.setOnClickListener(){
            logout()
        }
    }

    private fun logout(){
        sharedPref?.remove("user")
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession(){

        val gson = Gson()

        if(!sharedPref?.getData("user").isNullOrBlank()){
            // SI EL USUARIO EXISTE EN SESSION
            val user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
            Log.d("HomeActivity", "Usuario: $user")
        }

    }
}