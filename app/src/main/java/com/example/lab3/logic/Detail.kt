package com.example.lab3.logic

// підклас "Деталь"
class Detail(name: String,
             manufacturer: String,
             year: Int,
             price: Double,
             val material: String): Product(name, manufacturer, year, price) {
    override fun info(): String {
        return "Назва деталі: $name, Виробник: $manufacturer, Рік виготовлення: $year, Ціна: $price, Матеріал: $material"
    }
}