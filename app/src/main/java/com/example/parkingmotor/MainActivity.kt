package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        dbHelper = MySQLiteHelper(applicationContext)

        binding.btnRegM.setOnClickListener {
            nextRegisMoto()
        }
        binding.txtRegisM.setOnClickListener {
            registroM()
        }
        binding.btnAdmin.setOnClickListener {
            ingresoAdmin()
        }

    }
    /*private fun registrarMotoYAvanzar() {
        // Datos de ejemplo - reemplaza con tus EditText reales
        val placa = "ABC123" // binding.edtPlaca.text.toString()
        val marca = "Honda"  // binding.edtMarca.text.toString()

        if (placa.isEmpty() || marca.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            if (dbHelper.registrarMoto(placa, marca)) {
                // Registro exitoso - navegar a Menu
                startActivity(
                    Intent(this, Menu::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                ) // ✅ Paréntesis correctamente cerrado
            } else {
                Toast.makeText(this, "Error al registrar moto", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error crítico: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e("MainActivity", "Error al registrar", e)
        }
    }

    override fun onDestroy() {
        dbHelper.close()
        super.onDestroy()
    }*/
private fun registroM(){
    val i = Intent(this, Menu::class.java)
    startActivity(i)
}
    private fun ingresoAdmin(){
        val i = Intent(this, Register::class.java)
        startActivity(i)
    }
    private fun nextRegisMoto(){
        val i = Intent(this, Menu::class.java)
        startActivity(i)
    }

}