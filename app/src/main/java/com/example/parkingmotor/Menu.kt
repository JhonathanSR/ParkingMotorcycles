package com.example.parkingmotor

import android.content.ContentValues
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class Menu : AppCompatActivity() {
    lateinit var binding: ActivityMenuBinding
    lateinit var dbHelper: MySQLiteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicialización con try-catch
        dbHelper = try {
            MySQLiteHelper(applicationContext)
        } catch (e: Exception) {
            Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        var btnRegistrarMoto = findViewById<Button>(R.id.btnRegistrarMoto)
        btnRegistrarMoto.setOnClickListener {
            registrarMoto()
        }

        binding.btnNext.setOnClickListener {
            goNext()
        }

        binding.imgExit.setOnClickListener {
            goSalir()
        }

        binding.btnIni.setOnClickListener {
            goPrinc()
        }
    }

    private fun registrarMoto() {
        // 5. Verifica que estos IDs coincidan con tu XML
        val placa = binding.edtPlac.text?.toString()?.trim() ?: "" // Cambia a tu ID real
        val marca = binding.edtMarc.text?.toString()?.trim() ?: "" // Cambia a tu ID real

        if (placa.isEmpty() || marca.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (dbHelper.registrarMoto(placa, marca)) {
                Toast.makeText(this, "Moto registrada exitosamente", Toast.LENGTH_SHORT).show()
                // Limpiar campos después de registro exitoso
                binding.edtPlac.text?.clear()
                binding.edtMarc.text?.clear()
            } else {
                Toast.makeText(this, "Error al registrar la moto", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error crítico: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e("MenuActivity", "Error al registrar moto", e)
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
    override fun onDestroy() {
        try {
            dbHelper.close()
        } catch (e: Exception) {
            Log.e("MenuActivity", "Error al cerrar BD", e)
        }
        super.onDestroy()
    }
}


