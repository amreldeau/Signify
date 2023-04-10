package com.example.signify

import java.util.*

class CardPayment(name: String, cardnumber: String, expires: Date) : PaymentStrategy  {
    private val name: String
    private val cardnumber: String
    private val expires: Date
    fun getname(): String {
        return name
    }

    fun getcardnumber(): String {
        return cardnumber
    }

    fun getexpires(): Date {
        return expires
    }

    init {
        this.name = name
        this.cardnumber = cardnumber
        this.expires = expires
    }


    override fun pay(amount: Double): Boolean {
        return true // if payment goes through
    }
}
