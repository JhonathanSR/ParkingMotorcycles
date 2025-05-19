package com.example.parkingmotor

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parkingmotor.databinding.ActivityMenuBinding


class Menu : AppCompatActivity() {
    lateinit var binding: ActivityMenuBinding
    lateinit var dbHelper: MySQLiteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMenuBinding.inflate(layoutInflater)

        dbHelper = MySQLiteHelper(this)
        // Inicialización con try-catch
        dbHelper = try {
            MySQLiteHelper(applicationContext)
        } catch (e: Exception) {
            Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.btnRegistrarMoto.setOnClickListener {
            registrarMotoSeguro()
        }


        var btnNext = findViewById<ImageView>(R.id.btnNext)
        btnNext.setOnClickListener {
            goNext()
        }
        var imgExit = findViewById<ImageView>(R.id.imgExit)
        imgExit.setOnClickListener {
            goSalir()

        }
        var btnInicio = findViewById<Button>(R.id.btnInicio)
        btnInicio.setOnClickListener {
            goPrinc()
        }
    }

    private fun registrarMotoSeguro() {
        val placa = binding.edtPlac.text?.toString()?.trim() ?: ""
        val marca = binding.edtMarc.text?.toString()?.trim() ?: ""

        if (placa.isEmpty() || marca.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (dbHelper.registrarMoto(placa, marca)) {
                Toast.makeText(this, "Moto registrada!", Toast.LENGTH_SHORT).show()
                binding.edtPlac.text?.clear()
                binding.edtMarc.text?.clear()
            } else {
                Toast.makeText(this, "Error al registrar", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error crítico: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e("Menu", "Error en registrarMoto", e)
        }
    }

    private fun goNext() {
        val i = Intent(this, Cliente::class.java)
        startActivity(i)
    }
    private fun goSalir() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
    private fun goPrinc() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}
