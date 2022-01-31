package com.mov.startupapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.mov.startupapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(){
                if(it.isSuccessful) {
                    showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                }else{
                    showAlert()
                }
            }
        }else{
            Toast.makeText(this, "No valio, repita", Toast.LENGTH_SHORT).show()
        }

        //Toast.makeText(this, "El email es: ${email} y password: ${password}", Toast.LENGTH_SHORT).show()
        // pasarlo a consola los resultados
        Log.d("MainActivity", "el email es: $email")
        Log.d("MainActivity", "la contraseña es: $password")
    }

    //enviar un mensaje de error cuando no se autentifique bien el usuario
    private fun showAlert(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    // si es correcto pasa a la nueva entity
    private fun showHome(email: String, providerType: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", providerType.name)
        }
        startActivity(homeIntent)
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