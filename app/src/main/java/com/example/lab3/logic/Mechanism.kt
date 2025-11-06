package com.example.lab3.logic

class Mechanism(name: String,
                manufacturer: String,
                year: Int,
                price: Double,
                val assemblies: List<Assembly>): Product(name, manufacturer, year, price){

    override fun info(): String {
        val assembliesNames = assemblies.joinToString{ it.name }
        return "Назва механізму: $name, Виробник: $manufacturer, Рік виготовлення: $year, Ціна: $price, Вузли: $assembliesNames"
    }

}