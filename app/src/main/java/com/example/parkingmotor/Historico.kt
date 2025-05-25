package com.example.parkingmotor

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.parkingmotor.databinding.ActivityHistoricoBinding


class Historico : AppCompatActivity() {
    private lateinit var binding: ActivityHistoricoBinding
    private lateinit var dbHelper: clienteSQLiteHelper


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




        val imgSal = findViewById<ImageView>(R.id.imgSal)
        imgSal.setOnClickListener {
            goSalir()
        }
    }



    private fun goSalir() {
        val i = Intent(this, MainActivity::class.java)
        startActivity(i)
    }
}