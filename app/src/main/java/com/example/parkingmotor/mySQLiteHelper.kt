package com.example.parkingmotor

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MySQLiteHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "parking.db"
        private const val DATABASE_VERSION = 3
        private const val TABLE_USUARIOS = "usuarios"
        private const val TABLE_MOTOS = "motos"
        private const val KEY_ID = "id"
        private const val KEY_NOMBRE = "nombre"
        private const val KEY_CONTRASENA = "contrasena"
        private const val KEY_PLACA = "placa"
        private const val KEY_MARCA = "marca"
        private const val KEY_FECHA = "fecha_ingreso"
    }

    override fun onCreate(db: SQLiteDatabase) {
        try {

            val createUsuariosTable = ("CREATE TABLE $TABLE_USUARIOS ("
                    + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "$KEY_NOMBRE TEXT NOT NULL,"
                    + "$KEY_CONTRASENA TEXT NOT NULL)")


            val createTable = """
                CREATE TABLE $TABLE_MOTOS (
                _id INTEGER PRIMARY KEY AUTOINCREMENT,
                placa TEXT NOT NULL UNIQUE,
                marca TEXT NOT NULL,
                fecha_ingreso TEXT NOT NULL
            )
        """.trimIndent()
            db.execSQL(createUsuariosTable)
            db.execSQL(createTable)

            Log.d("MySQLiteHelper", "Tablas creadas correctamente")
        } catch (e: Exception) {
            Log.e("MySQLiteHelper", "Error al crear tablas: ${e.message}")
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        try {
            db.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
            db.execSQL("DROP TABLE IF EXISTS $TABLE_MOTOS")
            onCreate(db)
        } catch (e: Exception) {
            Log.e("MySQLiteHelper", "Error al actualizar BD: ${e.message}")
        }
    }

    // Funcion para usuarios
    fun anadirUsuario(nombre: String, contrasena: String): Boolean {
        val db = writableDatabase
        return try {
            val contentValues = ContentValues().apply {
                put(KEY_NOMBRE, nombre)
                put(KEY_CONTRASENA, contrasena)
            }
            val result = db.insert(TABLE_USUARIOS, null, contentValues)
            Log.d("MySQLiteHelper", "Usuario insertado con ID: $result")
            result != -1L
        } catch (e: Exception) {
            Log.e("MySQLiteHelper", "Error al añadir usuario: ${e.message}")
            false
        } finally {
            db.close()
        }
    }

    // funciones para motos
    fun registrarMoto(placa: String, marca: String): Boolean {
        return try {
            val db = writableDatabase
            val values = ContentValues().apply {
                put("placa", placa.uppercase())
                put("marca", marca)
                put("fecha_ingreso", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                    Date()
                ))
            }
            val result = db.insert(TABLE_MOTOS, null, values)
            db.close()
            result != -1L
        } catch (e: Exception) {
            Log.e("DB_ERROR", "Error al registrar moto", e)
            false
        }

    }

    fun verificarUsuario(nombre: String, contrasena: String): Boolean {
        val db = readableDatabase
        return try {
            val query = "SELECT * FROM $TABLE_USUARIOS WHERE $KEY_NOMBRE = ? AND $KEY_CONTRASENA = ?"
            db.rawQuery(query, arrayOf(nombre, contrasena)).use { cursor ->
                cursor.count > 0
            }
        } catch (e: Exception) {
            Log.e("MySQLiteHelper", "Error al verificar usuario: ${e.message}")
            false
        } finally {
            db.close()
        }
    }
}