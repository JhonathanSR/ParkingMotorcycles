package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parkingmotor.databinding.ActivityLoginBinding
import com.example.parkingmotor.databinding.ActivityMainBinding

class Login : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var usuariosDBHelper: MySQLiteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        usuariosDBHelper = MySQLiteHelper(this)

        var btnIngresar = findViewById<Button>(R.id.btnIngresar)
        btnIngresar.setOnClickListener {
            loginUser()
        }
        var ImgRetro1 = findViewById<ImageView>(R.id.ImgRetro1)
        ImgRetro1.setOnClickListener {
            goPrincipal()
        }
        var imgExit1 = findViewById<ImageView>(R.id.imgExit1)
        imgExit1.setOnClickListener {
            goSalir()
        }

    }
    private  fun loginUser(){
        val nombre = findViewById<EditText>(R.id.edtUser).text.toString()
        val contrasena = findViewById<EditText>(R.id.edtPass).text.toString()



        Log.d("LoginUser", "Nombre: $nombre, Contraseña: $contrasena")

        if (nombre.isNotEmpty() && contrasena.isNotEmpty()) {
            val resultado = usuariosDBHelper.verificarUsuario(nombre, contrasena)

            Log.d("LoginUser", "User login resultado: $resultado")

            if (resultado) {
                Toast.makeText(this, "Inicio de Sesión Exitoso",
                    Toast.LENGTH_SHORT).show()
                findViewById<EditText>(R.id.edtUser).setText("")
                findViewById<EditText>(R.id.edtPass).setText("")

                val i = Intent(this, Pagos::class.java)
                startActivity(i)
            } else {
                Toast.makeText(this, "Usuario o contraseña incorrectos",
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Complete todos los campos",
                Toast.LENGTH_SHORT).show()
        }

    }

    private fun goPrincipal(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
    private fun goMenuUno(){
        val i = Intent(this, Pagos::class.java)
        startActivity(i)
    }
    private fun goSalir(){
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}