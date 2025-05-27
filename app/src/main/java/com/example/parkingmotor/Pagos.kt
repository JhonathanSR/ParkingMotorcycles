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
    lateinit var binding: ActivityPagosBinding
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
        //binding.btnElim.setOnClickListener { eliminarRegistro() }
        binding.btnSave.setOnClickListener { guardarReporte() }
        binding.imgExt.setOnClickListener { salir() }

        binding.edtHora.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.edtHora.text.isNotBlank()) {
                calcularValorPagar()
            }
        }
    }

    private fun consultarDatos() {
        placaActual = binding.edtPlac1.text.toString().trim().uppercase()
        Log.d("Pagos", "Consultando datos para placa: $placaActual")

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
            Log.d(
                "Pagos",
                "Cliente encontrado: ${cliente.marca}, Fecha registro: ${cliente.fechaRegistro}"
            )


            binding.edtMarca.setText(cliente.marca)

            val fechaIngreso = try {
                SimpleDateFormat(
                    "yyyy-MM-dd HH:mm:ss",
                    Locale.getDefault()
                ).parse(cliente.fechaRegistro)
            } catch (e: Exception) {
                try {
                    SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss",
                        Locale.getDefault()
                    ).parse(cliente.fechaRegistro)
                } catch (e: Exception) {
                    Date()
                }
            } ?: Date()

            Log.d("FechaParseada", "Fecha de ingreso parseada: $fechaIngreso")

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val fechaSalida = Date()
            Log.d("Pagos", "Fecha ingreso parseada: $fechaIngreso")

            binding.edtHora.setText(sdf.format(fechaSalida))
            val duracionMs = fechaSalida.time - fechaIngreso.time
            val minutosTotales = duracionMs / (1000 * 60)
            binding.edtTiempo.setText("$minutosTotales minutos")

            val horasRedondeadas = ceil(minutosTotales / 60.0).toInt()
            val valorPagar = horasRedondeadas * tarifaPorHora
            binding.edtTotal.setText("$$valorPagar")

            binding.edtEmpl1.setText("Empleado 1") // O obtén el empleado de donde corresponda

        } catch (e: Exception) {
            mostrarMensaje("Error al consultar datos: ${e.message}")
            Log.e("Pagos", "Error en consulta: ${e.message}", e)
        }
    }

    private fun calcularValorPagar() {
        try {
            if (placaActual.isEmpty()) {
                mostrarMensaje("Primero consulte una placa")
                return
            }

            val cliente = clienteDBHelper.consultarClientePorPlaca(placaActual) ?: run {
                mostrarMensaje("No se encontró registro para esta placa")
                return
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            // Obtener fecha de ingreso desde la base de datos
            val fechaIngreso = sdf.parse(cliente.fechaRegistro) ?: Date()


            // Obtener fecha de salida ingresada manualmente
            val horaSalidaStr = binding.edtHora.text.toString()
            val fechaSalida = try {
                sdf.parse(horaSalidaStr) ?: run {
                    mostrarMensaje("Formato de hora inválido")
                    return
                }
            } catch (e: Exception) {
                mostrarMensaje("Formato de hora debe ser dd/MM/yyyy HH:mm")
                return
            }

            // Validar que la fecha de salida sea posterior a la de ingreso
            if (fechaSalida.before(fechaIngreso)) {
                mostrarMensaje("La hora de salida debe ser posterior a la de ingreso")
                return
            }
            // Cálculo en minutos
            val minutosTotales = (fechaSalida.time - fechaIngreso.time) / (1000 * 60)
            binding.edtTiempo.setText("$minutosTotales minutos")

            // Cálculo del valor
            val horasRedondeadas = ceil(minutosTotales / 60.0).toInt()
            val valorPagar = horasRedondeadas * tarifaPorHora
            binding.edtTotal.setText("$$valorPagar")


        } catch (e: Exception) {
            mostrarMensaje("Error al calcular: ${e.message}")
            Log.e("Pagos", "Error en cálculo", e)
        }
        binding.edtHora.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && binding.edtHora.text.isNotBlank()) {
                calcularValorPagar()
            }
        }
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

    /*private fun eliminarRegistro() {
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
    }*/

    private fun guardarReporte() {
        try {
            // Verificación inicial
            if (placaActual.isEmpty()) {
                mostrarMensaje("Primero consulte una placa")
                return
            }

            // Obtener y validar campos
            val marca = binding.edtMarca.text.toString().trim().takeIf { it.isNotEmpty() }
                ?: run {
                    mostrarMensaje("La marca no puede estar vacía")
                    return
                }

            val horaSalida =
                binding.edtHora.text.toString().trim().takeIf { it.isNotEmpty() && isValidDate(it) }
                    ?: run {
                        mostrarMensaje("Hora de salida inválida o vacía")
                        return
                    }

            val empleado = binding.edtEmpl1.text.toString().trim().takeIf { it.isNotEmpty() }
                ?: run {
                    mostrarMensaje("Nombre de empleado vacío")
                    return
                }

            val valorPagar = binding.edtTotal.text.toString().replace("$", "").trim()
                .takeIf { it.isNotEmpty() && it.matches(Regex("\\d+")) }
                ?: run {
                    mostrarMensaje("Valor a pagar inválido")
                    return
                }

            // Crear objeto Reporte
            val reporte = Reporte(
                placa = placaActual,
                marca = marca,
                horaSalida = horaSalida,
                duracion = binding.edtTiempo.text.toString(),
                empleado = empleado,
                valorPagar = valorPagar
            )

            // Debug: Mostrar datos en logs
            Log.d(
                "REPORTE_DEBUG", """
            === DATOS DEL REPORTE ===
            Placa: ${reporte.placa}
            Marca: ${reporte.marca}
            Hora Salida: ${reporte.horaSalida}
            Duración: ${reporte.duracion}
            Empleado: ${reporte.empleado}
            Valor: ${reporte.valorPagar}
            =========================
        """.trimIndent()
            )

            // Intentar guardar
            var resultado = clienteDBHelper.guardarReporte(reporte)

            if (!resultado) {
                clienteDBHelper.recrearBaseDeDatos()
                resultado = clienteDBHelper.guardarReporte(reporte)
            }

            if (resultado) {
                mostrarMensaje("Reporte guardado exitosamente")
                limpiarCampos()
                placaActual = ""
            } else {
                // Obtener error específico de la base de datos
                val errorInfo = clienteDBHelper.obtenerUltimoError()
                mostrarMensaje("Error al guardar: ${errorInfo ?: "Verifique los datos"}")
                Log.e("GUARDAR_REPORTE", "Error detallado: $errorInfo")
            }

        } catch (e: Exception) {
            mostrarMensaje("Error inesperado: ${e.localizedMessage}")
            Log.e("GUARDAR_REPORTE", "Excepción: ${e.stackTraceToString()}")
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

    private fun isValidDate(dateStr: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            sdf.isLenient = false // Para validación estricta
            sdf.parse(dateStr)
            true
        } catch (e: Exception) {
            Log.e("DateValidation", "Fecha inválida: $dateStr", e)
            false
        }
    }
}





