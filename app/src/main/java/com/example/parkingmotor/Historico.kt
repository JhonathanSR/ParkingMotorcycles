package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parkingmotor.databinding.ActivityHistoricoBinding
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

class Historico : AppCompatActivity() {
    private lateinit var binding: ActivityHistoricoBinding
    private lateinit var dbHelper: clienteSQLiteHelper
    private var placaActual: String =""
    private var ultimoReporteEliminado: Reporte? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHistoricoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = clienteSQLiteHelper(this)

        placaActual = intent.getStringExtra("PLACA") ?: ""

        binding.btnImprimir.setOnClickListener {
            generarPDF()
        }

        binding.imgSal.setOnClickListener {
            finish()
        }
        binding.btnSearch.setOnClickListener {
            consultarDatos()
        }
        setupButtonListeners()
    }
    private fun setupButtonListeners(){
        binding.btnDelete.setOnClickListener { eliminarRegistro() }
    }
    private fun eliminarRegistro() {
        if (placaActual.isEmpty()) {
           Toast.makeText(this, "Primero consulte una placa", Toast.LENGTH_SHORT).show()
            return
        }
        val reporte = dbHelper.obtenerReportePorPlaca(placaActual)

        if (dbHelper.eliminarReporte(placaActual)) {
            ultimoReporteEliminado = reporte // Guardar el reporte eliminado
            Toast.makeText(this, "Registro eliminado correctamente", Toast.LENGTH_SHORT).show()
            placaActual = ""
        } else {
            Toast.makeText(this, "Error al eliminar el registro", Toast.LENGTH_SHORT).show()
        }
    }
    private fun consultarDatos() {
        placaActual = binding.edtPla.text.toString().trim().uppercase()
        Log.d("Pagos", "Consultando datos para placa: $placaActual")

        if (placaActual.isEmpty()) {
            mostrarMensaje("Ingrese una placa para consultar") // Ahora funcionará
            return
        }
        try {
            val reporte = dbHelper.obtenerReportePorPlaca(placaActual) ?: run {
                mostrarMensaje("No se encontró registro para esta placa")
                limpiarCampos()
                return
            }
            Log.d("Pagos", "Reporte encontrado: ${reporte.marca}, Fecha registro: ${reporte.horaSalida}")

            binding.edtMarcas.setText(reporte.marca)
            binding.edtDat.setText(reporte.horaSalida)
            binding.edtTime.setText(reporte.duracion)
            binding.edtValor.setText(reporte.valorPagar)
            binding.edtEmplea.setText(reporte.empleado)

        } catch (e: Exception) {
            mostrarMensaje("Error al consultar datos: ${e.message}")
            Log.e("Pagos", "Error en consulta: ${e.message}", e)
        }
    }

    // Función auxiliar para mostrar Toast
    private fun mostrarMensaje(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }
    private fun generarPDF() {
        try {
            val reportes = dbHelper.obtenerTodosReportes().toMutableList()
            //val reportes = dbHelper.obtenerTodosReportes()
            if (reportes.isEmpty()) {
                Toast.makeText(this, "No hay datos para generar PDF", Toast.LENGTH_SHORT).show()
                return
            }
            ultimoReporteEliminado?.let {
                reportes.add(it)
                Toast.makeText(this, "Incluyendo registro eliminado en el PDF", Toast.LENGTH_SHORT).show()
            }
            if (reportes.isEmpty()) {
                Toast.makeText(this, "No hay datos para generar PDF", Toast.LENGTH_SHORT).show()
                return
            }

            // Crear directorio si no existe
            val folder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ParkingMotor")
            if (!folder.exists()) {
                folder.mkdirs()
            }

            // Crear archivo PDF
            val file = File(folder, "Historial_Motos_${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.pdf")
            val document = Document(PageSize.A4.rotate())
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()

            // Configurar fuentes - Usando Font de iTextPDF
            val fontTitulo = Font(Font.FontFamily.HELVETICA, 18f, Font.BOLD)
            val fontHeader = Font(Font.FontFamily.HELVETICA, 12f, Font.BOLD)
            val fontNormal = Font(Font.FontFamily.HELVETICA, 10f)

            // Título del documento
            val titulo = Paragraph("HISTORIAL DE MOTOS REGISTRADAS", fontTitulo)
            titulo.alignment = Element.ALIGN_CENTER
            document.add(titulo)

            // Fecha de generación
            val fecha = Paragraph("Generado: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())}", fontNormal)
            fecha.alignment = Element.ALIGN_RIGHT
            document.add(fecha)
            document.add(Paragraph(" ")) // Espacio en blanco

            // Crear tabla con los datos
            val table = PdfPTable(6)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(2f, 2f, 3f, 2f, 2f, 2f))

            // Encabezados de la tabla
            val headers = arrayOf("Placa", "Marca", "Hora Salida", "Duración", "Empleado", "Valor")
            headers.forEach { header ->
                val cell = PdfPCell(Phrase(header, fontHeader))
                cell.horizontalAlignment = Element.ALIGN_CENTER
                cell.backgroundColor = BaseColor.LIGHT_GRAY
                table.addCell(cell)
            }

            // Agregar datos a la tabla
            reportes.forEach { reporte ->
                table.addCell(createCell(reporte.placa, fontNormal))
                table.addCell(createCell(reporte.marca, fontNormal))
                table.addCell(createCell(reporte.horaSalida, fontNormal))
                table.addCell(createCell(reporte.duracion, fontNormal))
                table.addCell(createCell(reporte.empleado, fontNormal))
                table.addCell(createCell(reporte.valorPagar, fontNormal))
            }

            document.add(table)
            document.close()

            Toast.makeText(this, "PDF guardado en: ${file.path}", Toast.LENGTH_LONG).show()
            abrirPDF(file)
            ultimoReporteEliminado = null

        } catch (e: Exception) {
            Toast.makeText(this, "Error al generar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
    private fun limpiarCampos() {
        binding.edtPla.text.clear()
        binding.edtMarcas.text.clear()
        binding.edtDat.text.clear()
        binding.edtTime.text.clear()
        binding.edtEmplea.text.clear()
        binding.edtValor.text.clear()

    }

    private fun createCell(text: String, font: Font): PdfPCell {
        val cell = PdfPCell(Phrase(text, font))
        cell.horizontalAlignment = Element.ALIGN_CENTER
        cell.setPadding(5f)
        return cell
    }

    private fun abrirPDF(file: File) {
        val uri = FileProvider.getUriForFile(
            this,
            "${packageName}.provider",
            file
        )

        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)
    }

    private fun goSalir() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }

}