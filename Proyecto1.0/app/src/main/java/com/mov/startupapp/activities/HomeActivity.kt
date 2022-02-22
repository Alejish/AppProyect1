package com.mov.startupapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.mov.startupapp.R
import com.mov.startupapp.databinding.ActivityHomeBinding
import com.mov.startupapp.databinding.ActivityMainBinding
import com.mov.startupapp.fragments.client.ClientCategoriesFragment
import com.mov.startupapp.fragments.client.ClientOrdersFragment
import com.mov.startupapp.fragments.client.ClientProfileFragment
import com.mov.startupapp.models.User
import com.mov.startupapp.utils.SharePref

class HomeActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityHomeBinding
    var sharedPref : SharePref? = null
    var bottomNavigation: BottomNavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       /*binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)*/
        setContentView(R.layout.activity_home)
        sharedPref = SharePref(this)

        openFragment(ClientCategoriesFragment())
        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation?.setOnItemSelectedListener {

            when (it.itemId) {
                R.id.item_home -> {
                    openFragment(ClientCategoriesFragment())
                    true
                }
                R.id.item_orders -> {
                    openFragment(ClientOrdersFragment())
                    true
                }

                R.id.item_profile -> {
                    openFragment(ClientProfileFragment())
                    true
                }

                else -> false

            }
        }

        getUserFromSession()

        /*binding.btnLogout.setOnClickListener(){
            logout()
        }*/
    }

    private fun openFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
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