package com.example.lab3.logic

// клас "Вузол"
class Assembly(name: String,
               manufacturer: String,
               year: Int,
               price: Double,
               val details: List<Detail>) : Product(name, manufacturer, year, price){
    override fun info(): String {
        val detailNames = details.joinToString { it.name }
        return "Назва вузла: $name, Виробник: $manufacturer, Рік виготовлення: $year, Ціна: $price, Деталі: $detailNames"
    }

}