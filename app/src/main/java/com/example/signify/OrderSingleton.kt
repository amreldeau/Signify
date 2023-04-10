package com.example.signify

class OrderSingleton {
    var order: Order? = null

    companion object {
        @Volatile
        private var INSTANCE: OrderSingleton? = null

        fun getInstance(): OrderSingleton {
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = OrderSingleton()
                INSTANCE = instance
                return instance
            }
        }
    }
}
