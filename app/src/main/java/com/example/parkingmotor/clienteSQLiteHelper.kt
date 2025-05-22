package com.example.parkingmotor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.*
class clienteSQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "clientes.db"
        private const val DATABASE_VERSION = 2
        const val TABLE_CLIENTES = "clientes"
        const val COLUMN_ID = "_id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CEDULA = "cedula"
        const val COLUMN_TELEFONO = "telefono"
        const val COLUMN_PLACA = "placa"
        const val COLUMN_FECHA_REGISTRO = "fecha_registro"
        //const val COLUMN_MARCA = "marca"
    }

    val CREATE_TABLE_REPORTES = """
        CREATE TABLE reportes (
            _id INTEGER PRIMARY KEY AUTOINCREMENT,
            placa TEXT NOT NULL,
            marca TEXT NOT NULL,
            hora_salida TEXT NOT NULL,
            duracion TEXT NOT NULL,
            empleado TEXT NOT NULL,
            valor_pagar TEXT NOT NULL,
            fecha_reporte TEXT NOT NULL
        )
    """.trimIndent()

    //db.execSQL(CREATE_TABLE_REPORTES)

    fun consultarClientePorPlaca(placa: String): Cliente? {
        val db = readableDatabase
        return try {
            db.rawQuery("""
            SELECT * FROM clientes 
            WHERE placa = ?
        """.trimIndent(), arrayOf(placa)).use { cursor ->
                if (cursor.moveToFirst()) {
                    Cliente(
                        id = cursor.getLong(cursor.getColumnIndexOrThrow("_id")),
                        nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre")),
                        cedula = cursor.getString(cursor.getColumnIndexOrThrow("cedula")),
                        telefono = cursor.getString(cursor.getColumnIndexOrThrow("telefono")),
                        placa = cursor.getString(cursor.getColumnIndexOrThrow("placa")),
                        fechaRegistro = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro")),
                        marca = cursor.getString(cursor.getColumnIndexOrThrow("marca"))
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("DB", "Error al consultar cliente", e)
            null
        }
        /*val db = readableDatabase
        val cursor = db.query(
            TABLE_CLIENTES,
            arrayOf(COLUMN_NOMBRE, COLUMN_CEDULA, COLUMN_TELEFONO, COLUMN_PLACA, COLUMN_FECHA_REGISTRO, "marca"),
            "$COLUMN_PLACA = ?",
            arrayOf(placa),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            Cliente(
                cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CEDULA)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACA)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_REGISTRO)),
                cursor.getString(cursor.getColumnIndexOrThrow("marca"))
            )
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }*/
    }
    fun eliminarCliente(placa: String): Boolean {
        val db = writableDatabase
        return try {
            db.delete(TABLE_CLIENTES, "$COLUMN_PLACA = ?", arrayOf(placa)) > 0
        } finally {
            db.close()
        }
    }
    fun guardarReporte(reporte: Reporte): Boolean {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("placa", reporte.placa)
            put("marca", reporte.marca)
            put("hora_salida", reporte.horaSalida)
            put("duracion", reporte.duracion)
            put("empleado", reporte.empleado)
            put("valor_pagar", reporte.valorPagar)
            put(
                "fecha_reporte",
                SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            )
        }
        return try {
            db.insert("reportes", null, values) != -1L
        } finally {
            db.close()
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CLIENTES_TABLE = """
        CREATE TABLE $TABLE_CLIENTES (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NOMBRE TEXT NOT NULL,
            $COLUMN_CEDULA TEXT NOT NULL UNIQUE,
            $COLUMN_TELEFONO TEXT NOT NULL,
            $COLUMN_PLACA TEXT NOT NULL UNIQUE,
            marca TEXT NOT NULL,
            $COLUMN_FECHA_REGISTRO TEXT NOT NULL
        )
    """.trimIndent()

        db.execSQL(CREATE_CLIENTES_TABLE)
        db.execSQL(CREATE_TABLE_REPORTES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // Migración para añadir el campo marca
            db.execSQL("ALTER TABLE $TABLE_CLIENTES ADD COLUMN marca TEXT NOT NULL DEFAULT ''")
        }
    }

    fun registrarCliente(nombre: String, cedula: String, telefono: String, placa: String, marca: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOMBRE, nombre)
            put(COLUMN_CEDULA, cedula)
            put(COLUMN_TELEFONO, telefono)
            put(COLUMN_PLACA, placa)
            put("marca", marca)
            put(COLUMN_FECHA_REGISTRO, SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()))
        }

        return try {
            db.insert(TABLE_CLIENTES, null, values) != -1L
        } catch (e: Exception) {
            false
        } finally {
            db.close()
        }
    }

    fun obtenerTodosClientes(): List<Cliente> {
        val clientes = mutableListOf<Cliente>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CLIENTES ORDER BY $COLUMN_FECHA_REGISTRO DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val cliente = Cliente(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CEDULA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TELEFONO)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_REGISTRO))
                )
                clientes.add(cliente)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return clientes
    }
}

data class Cliente(
    val id: Long,
    val nombre: String,
    val cedula: String,
    val telefono: String,
    val placa: String,
    val fechaRegistro: String,
    val marca: String = ""
)
