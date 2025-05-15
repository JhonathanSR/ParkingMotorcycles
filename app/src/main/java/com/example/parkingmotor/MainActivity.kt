package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parkingmotor.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var  binding: ActivityMainBinding
    lateinit var usuariosDBHelper: mySQLiteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        usuariosDBHelper = mySQLiteHelper(this)
        var txtRegistro = findViewById<TextView>(R.id.txtRegistro)
        txtRegistro.setOnClickListener {
            goAdmin()
        }
        var btnRegistrarM = findViewById<Button>(R.id.btnRegistrarM)
        btnRegistrarM.setOnClickListener {
            goMenu()
        }
        var txtRegisM = findViewById<TextView>(R.id.txtRegisM)
        txtRegisM.setOnClickListener {
            goMenu()
        }
        var btnAdmin = findViewById<Button>(R.id.btnAdmin)
        btnAdmin.setOnClickListener {
            goRegistrar()
        }
    }

    private  fun goMenu(){
    val i = Intent(this, Menu::class.java)
    startActivity(i)
}
    private fun goRegistrar(){
    val i = Intent(this, Register::class.java)
    startActivity(i)
    }
    private fun goAdmin(){
    val i = Intent(this, Register::class.java)
    startActivity(i)
    }
}