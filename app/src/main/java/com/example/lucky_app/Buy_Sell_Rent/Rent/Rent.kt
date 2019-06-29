package com.example.lucky_app.Buy_Sell_Rent.Rent

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.lucky_app.Buy_Sell_Rent.*
import com.example.lucky_app.R

class Rent : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buy_sell_rent)

        val title = findViewById<TextView>(R.id.title)
        title.text = intent.getStringExtra("Title")

        val back = findViewById<TextView>(R.id.tv_back)
        back.setOnClickListener { finish() }

        val vehicle = findViewById<TextView>(R.id.vehicle)
        vehicle.setOnClickListener {
            val intent = Intent(this@Rent, Rent_Main1::class.java)
            intent.putExtra("Back","Rent")
            startActivity(intent)
        }
        val eletronic = findViewById<TextView>(R.id.eletronic)
        eletronic.setOnClickListener {
            val intent = Intent(this@Rent, Rent_Main2::class.java)
            intent.putExtra("Back","Rent")
            startActivity(intent)
        }

    }

}