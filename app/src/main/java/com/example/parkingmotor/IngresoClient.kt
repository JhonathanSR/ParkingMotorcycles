package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parkingmotor.databinding.ActivityIngresoClientBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IngresoClient : AppCompatActivity() {
    lateinit var binding: ActivityIngresoClientBinding
    lateinit var clienteDBHelper: clienteSQLiteHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIngresoClientBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clienteDBHelper = clienteSQLiteHelper(this)
        val btnRegistrarCliente = findViewById<Button>(R.id.btnRegistrarCliente)
        btnRegistrarCliente.setOnClickListener {
            registrarCliente()
        }

        var imgRetro3 = findViewById<ImageView>(R.id.imgRetro3)
        imgRetro3.setOnClickListener {
            goAtras()
        }
        var imgExit = findViewById<ImageView>(R.id.imgExit)
        imgExit.setOnClickListener {
            goSalir()
        }
        var imgNext3 = findViewById<ImageView>(R.id.imgNext3)
        imgNext3.setOnClickListener {
            goPagar()
        }
    }

    private fun registrarCliente() {
        val nombre = findViewById<EditText>(R.id.edtNomb).text.toString().trim()
        val cedula = findViewById<EditText>(R.id.edtDoc).text.toString().trim()
        val telefono = findViewById<EditText>(R.id.edtTel).text.toString().trim()
        val placa = findViewById<EditText>(R.id.edtPlaca).text.toString().trim().toUpperCase()
        val marca = binding.edtMar.text.toString().trim()

        if (nombre.isEmpty() || cedula.isEmpty() || telefono.isEmpty() || placa.isEmpty() || marca.isEmpty()) {
            Toast.makeText(this, "Complete todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if (cedula.length !in 8..10) {
            Toast.makeText(this, "La cédula debe tener entre 8 y 10 dígitos", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (placa.length !in 5..6) {
            Toast.makeText(this, "La placa debe tener entre 5 y 6 caracteres", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (marca.length !in 5..6) {
            Toast.makeText(this, "La marca debe tener entre 5 y 6 caracteres", Toast.LENGTH_SHORT)
                .show()
            return
        }

        val resultado = clienteDBHelper.registrarCliente(nombre, cedula, telefono, placa, marca)

        if (resultado) {
            Toast.makeText(this, "Cliente registrado correctamente", Toast.LENGTH_SHORT).show()
            limpiarCampos()

            // Mostrar fecha y hora de registro
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            val fechaHora = sdf.format(Date())
            Toast.makeText(this, "Registrado el: $fechaHora", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                this,
                "Error al registrar cliente (¿cédula ya existe?)",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun limpiarCampos() {
        findViewById<EditText>(R.id.edtNomb).setText("")
        findViewById<EditText>(R.id.edtDoc).setText("")
        findViewById<EditText>(R.id.edtTel).setText("")
        findViewById<EditText>(R.id.edtPlaca).setText("")
        findViewById<EditText>(R.id.edtMar).setText("")
    }

    private fun goAtras() {
        val i = Intent(this, Menu::class.java)
        startActivity(i)
    }

    private fun goSalir() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

    private fun goPagar() {
        /*val placa = binding.edtPlaca.text.toString().trim().uppercase()
        if (placa.isEmpty()) {
            Toast.makeText(this, "Ingrese una placa primero", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, Pagos::class.java).apply {
            putExtra("PLACA", placa)
        }
        startActivity(intent)*/
        val i = Intent(this, Pagos::class.java)
        startActivity(i)
    }
}
