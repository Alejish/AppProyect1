package com.mov.startupapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.mov.startupapp.databinding.ActivityRegistrerBinding
import com.mov.startupapp.models.ResponseHttp
import com.mov.startupapp.models.User
import com.mov.startupapp.providers.UsersProvider
import com.mov.startupapp.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrerActivity : AppCompatActivity() {

    var usersProvider = UsersProvider()
    val TAG = "RegistrerActivity"

    private lateinit var binding: ActivityRegistrerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.returnLogin.setOnClickListener(){
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        binding.btnRegistrarse.setOnClickListener(){
            registrar()
        }
    }
    private fun registrar(){
        val name = binding.nombreTxt.text.toString()
        val lastname = binding.apellidoTxt.text.toString()
        val email = binding.emailTxt.text.toString()
        val phone = binding.telTxt.text.toString()
        val password = binding.psswdTxt.text.toString()
        val conf_pass = binding.confPassTxt.text.toString()

        if(isValidateForm(email, password, name, lastname, phone, conf_pass)){
            val user = User(
                name = name,
                lastname = lastname,
                email = email,
                phone = phone,
                password = password
            )
            usersProvider.register(user)?.enqueue(object :Callback<ResponseHttp>{
                override fun onResponse(
                    call: Call<ResponseHttp>,
                    response: Response<ResponseHttp>
                ) {
                    if(response.body()?.isSuccess == true){
                        saveUserInSession(response.body()?.data.toString())
                        goToHome()

                    }
                    Toast.makeText(this@RegistrerActivity, response.body()?.message, Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Response: ${response}")
                    Log.d(TAG, "Body: ${response.body()}")

                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d(TAG, "se produjo un error ${t.message}")
                    Toast.makeText(this@RegistrerActivity, "Se produjo un error ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }else{
            Toast.makeText(this, "No es válido", Toast.LENGTH_LONG).show()
        }

    }

    private fun goToHome(){
        val i = Intent(this, SaveImageActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // eliminar le historial de pantallas
        startActivity(i)
    }

    private fun saveUserInSession(data: String) {

        val sharedPref = SharePref(this)
        val gson = Gson()
        val user = gson.fromJson(data, User::class.java)
        sharedPref.save("user", user)
    }


    fun String.isEmailValid(): Boolean{
        return  !TextUtils.isEmpty(this) && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }

    private fun isValidateForm(email : String, password : String, name: String, lastname: String, phone: String, conf_pass:String): Boolean{
        if (email.isBlank()){
            Toast.makeText(this, "Ingresar el correo", Toast.LENGTH_SHORT).show()
            return false
        }
        if (password.isBlank()){
            Toast.makeText(this, "Ingresar la contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        if (name.isBlank()){
            Toast.makeText(this, "El nombre no puede quedar vacio", Toast.LENGTH_SHORT).show()
            return false
        }
        if (lastname.isBlank()){
            Toast.makeText(this, "Ingresar el Apellido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (phone.isBlank()){
            Toast.makeText(this, "Ingresar el número de teléfono", Toast.LENGTH_SHORT).show()
            return false
        }
        if (conf_pass.isBlank()){
            Toast.makeText(this, "Ingresar la contraseña nuevamente", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!email.isEmailValid()){
            return false
        }
        if(password != conf_pass){
            return false
            Toast.makeText(this, "Ingrese de nuevo la contraseña", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, "La contraseña coincide", Toast.LENGTH_SHORT).show()
        }
        return true
    }

}