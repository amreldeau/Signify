package com.example.signify

class ApplyDiscountValueCommand(private val order: Order, private val amount: Double) : Command {
    override fun execute() {
        order.applyDiscountValue(amount)
    }

    override fun undo() {
        order.cancelDiscountValue(amount)
    }
}