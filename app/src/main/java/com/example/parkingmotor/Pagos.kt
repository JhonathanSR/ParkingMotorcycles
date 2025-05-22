package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parkingmotor.databinding.ActivityPagosBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.Locale
import kotlin.math.ceil

class Pagos : AppCompatActivity() {
    lateinit var  binding: ActivityPagosBinding
    lateinit var clienteDBHelper: clienteSQLiteHelper
    private val tarifaPorHora = 1000
    private var placaActual: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPagosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        clienteDBHelper = clienteSQLiteHelper(this)

        placaActual = intent.getStringExtra("PLACA") ?: ""
        if (placaActual.isNotEmpty()) {
            binding.edtPlac1.setText(placaActual)
            consultarDatos()
        }

        setupButtonListeners()
    }

    private fun setupButtonListeners() {
        binding.btnBuscar.setOnClickListener { consultarDatos() }
        binding.btnActual.setOnClickListener { actualizarDatos() }
        binding.btnElim.setOnClickListener { eliminarRegistro() }
        binding.btnSave.setOnClickListener { guardarReporte() }
        binding.imgExt.setOnClickListener { salir() }
    }

    private fun consultarDatos() {
        placaActual = binding.edtPlac1.text.toString().trim().uppercase()
        if (placaActual.isEmpty()) {
            mostrarMensaje("Ingrese una placa para consultar")
            return
        }
        try {
            val cliente = clienteDBHelper.consultarClientePorPlaca(placaActual) ?: run {
                mostrarMensaje("No se encontró registro para esta placa")
                limpiarCampos()
                return
            }
            /*val cliente = clienteDBHelper.consultarClientePorPlaca(placaActual)
            if (cliente != null) {*/
                binding.edtMarca.setText(cliente.marca)

                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val fechaIngreso = sdf.parse(cliente.fechaRegistro) ?: Date()
                val fechaSalida = Date()

                binding.edtHora.setText(sdf.format(fechaSalida))

                val duracionMs = fechaSalida.time - fechaIngreso.time
                val horas = duracionMs / (1000 * 60 * 60)
                val minutos = (duracionMs % (1000 * 60 * 60)) / (1000 * 60)

                binding.edtTiempo.setText("$horas horas $minutos minutos")

                val horasRedondeadas = ceil(horas + minutos / 60.0).toInt()
                val valorPagar = horasRedondeadas * tarifaPorHora

                binding.edtTotal.setText("$$valorPagar")
                binding.edtEmpl1.setText("Empleado 1")

            }catch (e: Exception) {
            mostrarMensaje("Error al consultar datos: ${e.message}")
            Log.e("Pagos", "Error en consulta", e)
        }/* else {
                mostrarMensaje("No se encontró registro para esta placa")
                limpiarCampos()
            }*/
    }

    private fun actualizarDatos() {
        if (placaActual.isEmpty()) {
            mostrarMensaje("Primero consulte una placa")
            return
        }

        val empleado = binding.edtEmpl1.text.toString().trim()
        if (empleado.isEmpty()) {
            mostrarMensaje("Ingrese el nombre del empleado")
            return
        }
        mostrarMensaje("Datos actualizados correctamente")
    }

    private fun eliminarRegistro() {
        if (placaActual.isEmpty()) {
            mostrarMensaje("Primero consulte una placa")
            return
        }

        if (clienteDBHelper.eliminarCliente(placaActual)) {
            mostrarMensaje("Registro eliminado correctamente")
            limpiarCampos()
            placaActual = ""
        } else {
            mostrarMensaje("Error al eliminar el registro")
        }
    }

    private fun guardarReporte() {
        if (placaActual.isEmpty()) {
            mostrarMensaje("Primero consulte una placa")
            return
        }

        val reporte = Reporte(
            placa = placaActual,
            marca = binding.edtMarca.text.toString(),
            horaSalida = binding.edtHora.text.toString(),
            duracion = binding.edtTiempo.text.toString(),
            empleado = binding.edtEmpl1.text.toString(),
            valorPagar = binding.edtTotal.text.toString().replace("$", "")
        )
        try {
            if (clienteDBHelper.guardarReporte(reporte)) {
                mostrarMensaje("Reporte guardado correctamente")
                limpiarCampos()
            } else {
                mostrarMensaje("Error al guardar el reporte")
            }
        } catch (e: Exception) {
            mostrarMensaje("Error crítico: ${e.message}")
            Log.e("Pagos", "Error guardando reporte", e)
        }
    }

    private fun limpiarCampos() {
        binding.edtMarca.text.clear()
        binding.edtHora.text.clear()
        binding.edtTiempo.text.clear()
        binding.edtEmpl1.text.clear()
        binding.edtTotal.text.clear()
    }

    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun salir() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

data class Reporte(
    val placa: String,
    val marca: String,
    val horaSalida: String,
    val duracion: String,
    val empleado: String,
    val valorPagar: String
)



