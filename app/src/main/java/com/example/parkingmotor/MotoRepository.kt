package com.example.parkingmotor

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MotoRepository(context: Context) {
    private val dbHelper = MotoSQLiteHelper(context)

    suspend fun insertarMoto(placa: String, marca: String): Long {
        return withContext(Dispatchers.IO) {
            dbHelper.agregarMoto(placa, marca)
        }
    }

    suspend fun obtenerMotos(): List<Moto> {
        return withContext(Dispatchers.IO) {
            dbHelper.obtenerTodasMotos()
        }
    }
}