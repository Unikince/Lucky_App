package com.example.lucky_app.Buy_Sell_Rent.Sell

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.lucky_app.Buy_Sell_Rent.New_Main1
import com.example.lucky_app.Buy_Sell_Rent.Rent_Main1
import com.example.lucky_app.Buy_Sell_Rent.Sell_Main1
import com.example.lucky_app.Product_dicount.User_post

abstract class PassRent1: Fragment(){
    lateinit var BACK_SELL: Rent_Main1

    override fun onAttach(context: Context) {
        super.onAttach(context)
        BACK_SELL = context as Rent_Main1
    }
}