package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
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