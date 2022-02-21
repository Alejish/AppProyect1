package com.mov.startupapp.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import com.mov.startupapp.R
import com.mov.startupapp.databinding.ActivityHomeBinding
import com.mov.startupapp.databinding.ActivitySaveImageBinding
import com.mov.startupapp.models.ResponseHttp
import com.mov.startupapp.models.User
import com.mov.startupapp.providers.UsersProvider
import com.mov.startupapp.utils.SharePref
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SaveImageActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySaveImageBinding
    private var imageFile: File?= null
    var usersProvider = UsersProvider()
    var sharedPref : SharePref? = null
    var user:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPref = SharePref(this)

        getUserFromSession()

        binding.circleimageUser.setOnClickListener(){
            selectImage()
        }
        binding.btnNext.setOnClickListener(){goToHome()}
        binding.btnConfirm.setOnClickListener(){saveImage()}
    }

    private fun saveImage(){

        if(imageFile != null && user != null){
            usersProvider.update(imageFile!!, user!! )?.enqueue(object: Callback<ResponseHttp> {
                override fun onResponse(call: Call<ResponseHttp>, response: Response<ResponseHttp>) {
                    Log.d("SaveImageActivity", "Response: ${response}")
                    Log.d("SaveImageActivity", "Response: ${response.body()}")
                }

                override fun onFailure(call: Call<ResponseHttp>, t: Throwable) {
                    Log.d("SaveImageActivity", "Hubo un error ${t.message}")
                    Toast.makeText(this@SaveImageActivity, "Hubo un error ${t.message}", Toast.LENGTH_LONG).show()
                }

            })
        }else{
            Toast.makeText(this@SaveImageActivity, "La imagen no puede ser nula ni los datos de session dle usuario", Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserFromSession(){

        val gson = Gson()

        if(!sharedPref?.getData("user").isNullOrBlank()){
            // SI EL USUARIO EXISTE EN SESSION
            user = gson.fromJson(sharedPref?.getData("user"), User::class.java)
        }

    }

    private fun goToHome(){
        val i = Intent(this, HomeActivity::class.java)
        startActivity(i)
    }
    private val startImageForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

            val resultCode = result.resultCode
            val data = result.data

            if (resultCode == Activity.RESULT_OK) {
                val fileUri = data?.data
                imageFile = File(fileUri?.path) // EL ARCHIVO QUE VAMOS A GUARDAR COMO IMAGEN EN EL SERVIDOR
                binding.circleimageUser.setImageURI(fileUri)
            }
            else if (resultCode == ImagePicker.RESULT_ERROR) {
                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Tarea se cancelo", Toast.LENGTH_LONG).show()
            }

        }


    private fun selectImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent ->
                startImageForResult.launch(intent)
            }
    }

}