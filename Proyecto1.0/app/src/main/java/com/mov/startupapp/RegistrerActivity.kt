package com.mov.startupapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mov.startupapp.databinding.ActivityRegistrerBinding

class RegistrerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrerBinding
    // conexion a la bd en firebase
    private val db = FirebaseFirestore.getInstance()

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
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(){
                if(it.isSuccessful) {
                    showHome(it.result?.user?.email ?: "", ProviderType.BASIC)
                }else{
                    showAlert()
                }
            }
        }else{
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_LONG).show()
        }

        if(isValidateForm(email, password, name, lastname, phone, conf_pass)){
            db.collection("users").document(email).set(
                hashMapOf("Nombre" to name,
                    "Apellido" to lastname,
                    "Telefono" to phone,
                    "Contraseña" to password)
            )
        }else{
            Toast.makeText(this, "Registro no válido", Toast.LENGTH_LONG).show()
        }

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
        val homeIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", providerType.name)
        }
        startActivity(homeIntent)
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