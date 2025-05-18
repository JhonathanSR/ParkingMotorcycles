package com.example.parkingmotor

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
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
    lateinit var motosDBHelper: MotoSQLiteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)
        motosDBHelper = MotoSQLiteHelper(this)

        var btnRegP = findViewById<Button>(R.id.btnRegP)
        btnRegP.setOnClickListener {
            goRegistrarMoto()
        }

        var btnNext = findViewById<ImageView>(R.id.btnNext)
        btnNext.setOnClickListener {
            goNext()
        }
        var imgExit = findViewById<ImageView>(R.id.imgExit)
        imgExit.setOnClickListener {
            goSalir()

        }
    }
    private fun registrarMoto() {
        val placa = findViewById<TextView>(R.id.edtPlac).text.toString().trim().toUpperCase()
        val marca = findViewById<TextView>(R.id.edtMarc).text.toString().trim()

        if (placa.isEmpty() || marca.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (placa.length < 5 || placa.length > 6) {
            Toast.makeText(this, "La placa debe tener entre 5 y 6 caracteres", Toast.LENGTH_SHORT).show()
            return
        }

        val resultado = motosDBHelper.registrarMoto(placa, marca)

        if (resultado) {
            Toast.makeText(this, "Moto registrada correctamente", Toast.LENGTH_SHORT).show()
            findViewById<TextView>(R.id.edtPlac).setText("")
            findViewById<TextView>(R.id.edtMarc).setText("")

            // Opcional: Mostrar la fecha y hora de registro
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            val fechaHora = sdf.format(Date())
            Toast.makeText(this, "Registrado el: $fechaHora", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(this, "Error al registrar la moto", Toast.LENGTH_SHORT).show()
        }
    }

    private fun goRegistrarMoto() {
        val i = Intent(this, Cliente::class.java)
        startActivity(i)
    }
    private fun goNext() {
        val i = Intent(this, Cliente::class.java)
        startActivity(i)
    }
    private fun goSalir() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}
