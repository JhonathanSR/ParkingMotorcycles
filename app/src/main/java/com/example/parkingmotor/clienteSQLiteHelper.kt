package com.example.parkingmotor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*
class clienteSQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "clientes.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_CLIENTES = "clientes"
        const val COLUMN_ID = "_id"
        const val COLUMN_NOMBRE = "nombre"
        const val COLUMN_CEDULA = "cedula"
        const val COLUMN_TELEFONO = "telefono"
        const val COLUMN_PLACA = "placa"
        const val COLUMN_FECHA_REGISTRO = "fecha_registro"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_CLIENTES_TABLE = ("CREATE TABLE $TABLE_CLIENTES ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_NOMBRE TEXT NOT NULL, "
                + "$COLUMN_CEDULA TEXT NOT NULL UNIQUE, "
                + "$COLUMN_TELEFONO TEXT NOT NULL, "
                + "$COLUMN_PLACA TEXT NOT NULL, "
                + "$COLUMN_FECHA_REGISTRO TEXT NOT NULL);")

        db.execSQL(CREATE_CLIENTES_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")
        onCreate(db)
    }

    fun registrarCliente(nombre: String, cedula: String, telefono: String, placa: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        // Obtener fecha y hora actual automáticamente
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaHora = sdf.format(Date())

        values.put(COLUMN_NOMBRE, nombre)
        values.put(COLUMN_CEDULA, cedula)
        values.put(COLUMN_TELEFONO, telefono)
        values.put(COLUMN_PLACA, placa)
        values.put(COLUMN_FECHA_REGISTRO, fechaHora)

        try {
            val resultado = db.insert(TABLE_CLIENTES, null, values)
            db.close()
            return resultado != -1L
        } catch (e: Exception) {
            db.close()
            return false
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
    val fechaRegistro: String
)