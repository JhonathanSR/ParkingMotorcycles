package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parkingmotor.databinding.ActivityRegisterBinding

class Register : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding
    lateinit var usuariosDBHelper: mySQLiteHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usuariosDBHelper = mySQLiteHelper(this)

        var btnRegistro = findViewById<Button>(R.id.btnRegistro)
        btnRegistro.setOnClickListener {
            goLogin()
        }
        var imgExit1 = findViewById<ImageView>(R.id.imgExit1)
        imgExit1.setOnClickListener {
            goInicio()
        }
        var imgAtras = findViewById<ImageView>(R.id.imgAtras)
        imgAtras.setOnClickListener {
            goAtras()
        }
        var btnIngres = findViewById<Button>(R.id.btnIngres)
        btnIngres.setOnClickListener {
            goLogin()
        }

    }
    private fun registerUser() {
        val nombre = findViewById<EditText>(R.id.edtCorreo).text.toString()
        val contrasena = findViewById<EditText>(R.id.edtPassw).text.toString()

        Log.d("RegisterUser", "Nombre: $nombre, Contrasena: $contrasena")

        if (nombre.isNotEmpty() && contrasena.isNotEmpty()) {
            val usuariosDBHelper = mySQLiteHelper(this)
            val resultado = usuariosDBHelper.anadirdato(nombre, contrasena)

            Log.d("RegisterUser", "User registration resultado: $resultado")

            if (resultado) {
                Toast.makeText(
                    this, "Usuario Registrado Correctamente",
                    Toast.LENGTH_SHORT
                ).show()
                findViewById<EditText>(R.id.edtCorreo).setText("")
                findViewById<EditText>(R.id.edtPassw).setText("")
            } else {
                Toast.makeText(
                    this, "Usuario y contraseña incorrectos",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                this, "Complete todos los campos",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    private fun goLogin(){
        val i = Intent(this, Login::class.java)
        startActivity(i)
    }
    private fun goInicio(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
    private fun goAtras(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}