package com.example.parkingmotor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
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
            db.rawQuery(
                """
            SELECT * FROM clientes 
            WHERE placa = ?
        """.trimIndent(), arrayOf(placa)
            ).use { cursor ->
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

    }

    fun eliminarCliente(placa: String): Boolean {
        val db = writableDatabase
        return try {
            db.delete(TABLE_CLIENTES, "$COLUMN_PLACA = ?", arrayOf(placa)) > 0
        } finally {
            db.close()
        }
    }
    private var ultimoError: String? = null
    fun guardarReporte(reporte: Reporte): Boolean {
        val db = writableDatabase
        return try {
            db.beginTransaction()

            // Verificar si la tabla existe
            if (!tablaExiste(db, "reportes")) {
                throw SQLiteException("La tabla 'reportes' no existe")
            }

            val values = ContentValues().apply {
                put("placa", reporte.placa)
                put("marca", reporte.marca)
                put("hora_salida", reporte.horaSalida)
                put("duracion", reporte.duracion)  // ¡Atención a posibles typos!
                put("empleado", reporte.empleado)
                put("valor_pagar", reporte.valorPagar)
                put("fecha_reporte", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
            }

            // Debug: Mostrar valores que se insertarán
            Log.d("DB_DEBUG", "ContentValues: $values")

            val result = db.insert("reportes", null, values)
            db.setTransactionSuccessful()

            if (result == -1L) {
                ultimoError = "Error al insertar, posible problema con los datos"
                Log.e("DB_ERROR", ultimoError!!)
            }

            result != -1L
        } catch (e: SQLiteException) {
            ultimoError = "Error SQLite: ${e.message}"
            Log.e("DB_ERROR", ultimoError!!, e)
            false
        } catch (e: Exception) {
            ultimoError = "Error general: ${e.localizedMessage}"
            Log.e("DB_ERROR", ultimoError!!, e)
            false
        } finally {
            try {
                db.endTransaction()
            } catch (e: Exception) {
                Log.e("DB_ERROR", "Error al finalizar transacción", e)
            }
            db.close()
        }
    }

    // Método para verificar existencia de tablas
    private fun tablaExiste(db: SQLiteDatabase, tableName: String): Boolean {
        db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='$tableName'", null).use {
            return it.moveToFirst()
        }
    }

    // Método para obtener el último error
    fun obtenerUltimoError(): String? = ultimoError

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
data class Reporte(
    val placa: String,
    val marca: String,
    val horaSalida: String,
    val duracion: String,
    val empleado: String,
    val valorPagar: String
)
