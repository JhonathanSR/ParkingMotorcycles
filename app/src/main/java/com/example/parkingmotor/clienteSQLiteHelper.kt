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
        private const val DATABASE_VERSION = 3
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

    /*fun eliminarCliente(placa: String): Boolean {
        val db = writableDatabase
        return try {
            db.delete(TABLE_CLIENTES, "$COLUMN_PLACA = ?", arrayOf(placa)) > 0
        } finally {
            db.close()
        }
    }*/
    fun eliminarReporte(placa: String): Boolean {
        val db = writableDatabase
        return try {
            db.delete("reportes", "placa = ?", arrayOf(placa)) > 0
        } finally {
            db.close()
        }
    }
    private var ultimoError: String? = null
    fun guardarReporte(reporte: Reporte): Boolean {
        val db = writableDatabase
        return try {
            db.beginTransaction()

            // Verificar y crear tabla si no existe
            if (!tablaExiste(db, "reportes")) {
                db.execSQL("""
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
            """.trimIndent())
            }

            val values = ContentValues().apply {
                put("placa", reporte.placa)
                put("marca", reporte.marca)
                put("hora_salida", reporte.horaSalida)
                put("duracion", reporte.duracion)
                put("empleado", reporte.empleado)
                put("valor_pagar", reporte.valorPagar)
                put("fecha_reporte", SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date()))
            }

            val result = db.insert("reportes", null, values)
            db.setTransactionSuccessful()
            result != -1L
        } catch (e: Exception) {
            Log.e("DBHelper", "Error al guardar reporte", e)
            false
        } finally {
            try {
                db.endTransaction()
            } catch (e: Exception) {
                Log.e("DBHelper", "Error al finalizar transacción", e)
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
        try {

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
            db.execSQL("""
            CREATE TABLE IF NOT EXISTS reportes (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                placa TEXT NOT NULL,
                marca TEXT NOT NULL,
                hora_salida TEXT NOT NULL,
                duracion TEXT NOT NULL,
                empleado TEXT NOT NULL,
                valor_pagar TEXT NOT NULL,
                fecha_reporte TEXT NOT NULL
            )
        """.trimIndent())

            Log.d("Database", "Tablas creadas exitosamente")
        } catch (e: Exception) {
            Log.e("Database", "Error al crear tablas", e)
        }
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
    fun recrearBaseDeDatos() {
        val db = writableDatabase
        try {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")
            db.execSQL("DROP TABLE IF EXISTS reportes")
            onCreate(db)
            Log.d("Database", "Base de datos recreada exitosamente")
        } catch (e: Exception) {
            Log.e("Database", "Error al recrear base de datos", e)
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
    fun obtenerTodosReportes(): List<Reporte> {
        val reportes = mutableListOf<Reporte>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM reportes ORDER BY fecha_reporte DESC", null)

        with(cursor) {
            while (moveToNext()) {
                reportes.add(
                    Reporte(
                        placa = getString(getColumnIndexOrThrow("placa")),
                        marca = getString(getColumnIndexOrThrow("marca")),
                        horaSalida = getString(getColumnIndexOrThrow("hora_salida")),
                        duracion = getString(getColumnIndexOrThrow("duracion")),
                        empleado = getString(getColumnIndexOrThrow("empleado")),
                        valorPagar = getString(getColumnIndexOrThrow("valor_pagar"))
                    )
                )
            }
            close()
        }
        db.close()
        return reportes
    }
    fun obtenerReportePorPlaca(placa: String): Reporte? {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM reportes WHERE placa = ?", arrayOf(placa)).use { cursor ->
            if (cursor.moveToFirst()) {
                Reporte(
                    placa = cursor.getString(cursor.getColumnIndexOrThrow("placa")),
                    marca = cursor.getString(cursor.getColumnIndexOrThrow("marca")),
                    horaSalida = cursor.getString(cursor.getColumnIndexOrThrow("hora_salida")),
                    duracion = cursor.getString(cursor.getColumnIndexOrThrow("duracion")),
                    empleado = cursor.getString(cursor.getColumnIndexOrThrow("empleado")),
                    valorPagar = cursor.getString(cursor.getColumnIndexOrThrow("valor_pagar"))
                )
            } else {
                null
            }
        }
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
