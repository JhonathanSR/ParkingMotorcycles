package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        btnRegistrar.setOnClickListener {
            goMenu()
        }
        var txtRegis = findViewById<TextView>(R.id.txtRegis)
        txtRegis.setOnClickListener {
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
    private fun goLogin(){
    val i = Intent(this, Login::class.java)
    startActivity(i)
    }
    private fun goRegistrar(){
    val i = Intent(this, Menu::class.java)
    startActivity(i)
    }
}