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
    private lateinit var binding: ActivityMainBinding
    private lateinit var dbHelper: MySQLiteHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configuración del binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        // Configuración de insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // dbHelper = MySQLiteHelper(applicationContext)

        // Configuración de listeners
        val btnIngresarMoto = findViewById<Button>(R.id.btnIngresarMoto)
        btnIngresarMoto.setOnClickListener {
            nextRegisMoto()
        }

        val txtRegisM = findViewById<TextView>(R.id.txtRegisM)
        txtRegisM.setOnClickListener {
            registroM()
        }

        val btnAdmin = findViewById<Button>(R.id.btnAdmin)
        btnAdmin.setOnClickListener {
            ingresoAdmin()
        }
    }

    private fun nextRegisMoto() {
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
    }

    private fun registroM() {
        val intent = Intent(this, Menu::class.java)
        startActivity(intent)
    }

    private fun ingresoAdmin() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
    }
}