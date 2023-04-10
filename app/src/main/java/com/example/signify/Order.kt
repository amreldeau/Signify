package com.example.signify

class Order {
    private var total: Double = 0.0
    private var discount: Double = 0.0
    private var id: Int = 0
    private lateinit var paymentstrategy: PaymentStrategy

    fun setPaymentStrategy(paymentstrategy: PaymentStrategy) {
        this.paymentstrategy = paymentstrategy
    }
    fun executePaymentStrategy() {
        paymentstrategy.pay(total)
    }

    fun getTotal(): Double = total

    fun setPrice(amount: Double){
        this.total = amount
    }
    fun generateID(){
        this.id = 123
    }
    fun getID(): Int {
        return id
    }

    fun applyDiscountValue(amount: Double) {
        total -= amount
        discount += amount
    }

    fun applyDiscountPercentage(percent: Double) {
        val amount = total * percent / 100.0
        total -= amount
        discount += amount
    }

    fun cancelDiscountValue(amount: Double) {
        total += amount
        discount -= amount
    }

    fun cancelDiscountPercentage(percent: Double) {
        val amount = total * percent / 100.0
        total += amount
        discount -= amount
    }
}