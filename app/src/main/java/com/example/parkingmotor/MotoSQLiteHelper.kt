package com.example.parkingmotor
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import java.text.SimpleDateFormat
import java.util.*

class MotoSQLiteHelper (context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "motos.db"
        private const val DATABASE_VERSION = 1
        const val TABLE_MOTOS = "motos"
        const val COLUMN_ID = "_id"
        const val COLUMN_PLACA = "placa"
        const val COLUMN_MARCA = "marca"
        const val COLUMN_FECHA_INGRESO = "fecha_ingreso"
    }
    override fun onCreate(db: SQLiteDatabase) {
        val CREATE_MOTOS_TABLE = ("CREATE TABLE $TABLE_MOTOS ("
                + "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "$COLUMN_PLACA TEXT NOT NULL, "
                + "$COLUMN_MARCA TEXT NOT NULL, "
                + "$COLUMN_FECHA_INGRESO TEXT NOT NULL);")

        db.execSQL(CREATE_MOTOS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MOTOS")
        onCreate(db)
    }

    fun registrarMoto(placa: String, marca: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues()

        // Obtener fecha y hora actual automáticamente
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val fechaHora = sdf.format(Date())

        values.put(COLUMN_PLACA, placa)
        values.put(COLUMN_MARCA, marca)
        values.put(COLUMN_FECHA_INGRESO, fechaHora)

        val resultado = db.insert(TABLE_MOTOS, null, values)
        db.close()

        return resultado != -1L
    }

    fun obtenerTodasMotos(): List<Moto> {
        val motos = mutableListOf<Moto>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_MOTOS ORDER BY $COLUMN_FECHA_INGRESO DESC", null)

        if (cursor.moveToFirst()) {
            do {
                val moto = Moto(
                    cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PLACA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MARCA)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_FECHA_INGRESO))
                )
                motos.add(moto)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return motos
    }
}

data class Moto(
    val id: Long,
    val placa: String,
    val marca: String,
    val fechaIngreso: String
)