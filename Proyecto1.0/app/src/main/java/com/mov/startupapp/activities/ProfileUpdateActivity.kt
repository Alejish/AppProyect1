package com.mov.startupapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.mov.startupapp.databinding.ActivityMainBinding
import com.mov.startupapp.databinding.ActivityProfileUpdateBinding
import com.mov.startupapp.models.ResponseHttp
import com.mov.startupapp.models.User
import com.mov.startupapp.providers.UsersProvider
import com.mov.startupapp.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileUpdateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileUpdateBinding
    var sharedPref: SharePref? = null
    var user: User? = null
    var usersProvider = UsersProvider()
    val TAG = "ProfileUpdateActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharePref(this)

        getUserFromSession()

        binding.nombreUpdateTxt.setText(user?.name)
        binding.apellidoUpdateTxt.setText(user?.lastname)
        binding.telUpdateTxt.setText(user?.phone)

        binding.btnUpdate.setOnClickListener(){ updateData() }

    }

    private fun updateData(){
        val name = binding.nombreUpdateTxt.text.toString()
        val lastname = binding.apellidoUpdateTxt.text.toString()
        val phone = binding.telUpdateTxt.text.toString()

        user?.name = name
        user?.lastname = lastname
        user?.phone = phone

        usersProvider.updateWithoutImage(user!!)?.enqueue(object: Callback<ResponseHttp> {
            override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {

                Log.d(TAG, "RESPONSE: $response")
                Log.d(TAG, "BODY: ${response.body()}")

                Toast.makeText(this@ProfileUpdateActivity, response.body()?.message, Toast.LENGTH_SHORT).show()

                saveUserInSession(response.body()?.data.toString())

            }

            override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                Log.d(TAG, "Error: ${t.message}")
                Toast.makeText(this@ProfileUpdateActivity, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getUserFromSession() {

        val gson = Gson()

        if (!sharedPref?.getData("user").isNullOrBlank()) {
            // SI EL USARIO EXISTE EN SESION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }

    private fun saveUserInSession(data: String) {

        val sharedPref = SharePref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }
}

