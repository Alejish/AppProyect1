package com.mov.startupapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.mov.startupapp.databinding.ActivityMainBinding
import com.mov.startupapp.models.ResponseHttp
import com.mov.startupapp.models.User
import com.mov.startupapp.providers.UsersProvider
import com.mov.startupapp.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var usersProvider = UsersProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getUserFromSession()

        binding.imageviewGoRegister.setOnClickListener(){
            var intent = Intent(this, RegistrerActivity::class.java)
            startActivity(intent)
        }
        binding.bttnLogin.setOnClickListener(){
            login()
        }
    }
    private fun login(){
        //para capturar el valor que ingresa el usuario en el campo email
        val email = binding.emailTxt.text.toString()
        val password = binding.pswdText.text.toString()

        if(isValidateForm(email, password)){
            usersProvider.login(email,password)?.enqueue(object: Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    Log.d("MainActivity", "Response: ${response.body()}")

                    if(response.body()?.isSuccess == true){
                        Toast.makeText(this@MainActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                        saveUserInSession(response.body()?.data.toString())
                        goToHome()

                    }else{
                        Toast.makeText(this@MainActivity, "Los datos no son correctos", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d("MainActivity", "Hubo un error ${t.message}")
                    Toast.makeText(this@MainActivity, "Hubo un error ${t.message}", Toast.LENGTH_LONG).show()
                }

            })

        }else{
            Toast.makeText(this, "No es válido", Toast.LENGTH_LONG).show()
        }

    }

    private fun goToHome(){
        val i = Intent(this, SaveImageActivity::class.java)
        startActivity(i)
    }

    private fun getUserFromSession(){

        val sharedPref = SharePref(this)
        val gson = Gson()

        if(!sharedPref.getData("user").isNullOrBlank()){
            // SI EL USUARIO EXISTE EN SESSION
            val user = gson.fromJson(sharedPref.getData("user"), User::class.java)
            goToHome()
        }

    }

    private fun saveUserInSession(data: String){
        val sharePref = SharePref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharePref.save("user", user)

    }

    fun String.isEmailValid(): Boolean{
        return  !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun isValidateForm(email : String, password : String): Boolean{
        if (email.isBlank()){
            return false
        }
        if (password.isBlank()){
            return false
        }
        if (!email.isEmailValid()){
            return false
        }
        return true
    }
}