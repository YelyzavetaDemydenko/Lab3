package com.example.lab3.logic

class Warehouse(val name: String) : Sellable {
    val allDetails = mutableListOf<Detail>()
    val allAssemblies = mutableListOf<Assembly>()
    val allMechanisms = mutableListOf<Mechanism>()

    override fun buy(product: Product) {
        when (product) {
            is Detail -> {
                allDetails.add(product)
                println("Деталь '${product.name}' куплена та додана до складу.")
            }
            is Assembly -> {
                allAssemblies.add(product)
                println("Вузол '${product.name}' куплений та доданий до складу.")
            }
            is Mechanism -> {
                allMechanisms.add(product)
                println("Механізм '${product.name}' куплений та доданий до складу.")
            }
            else -> println("Неможливо додати на склад.")
        }
    }
    override fun sell(product: Product) {
        when (product) {
            is Detail -> {
                if (allDetails.remove(product)) {
                    println("Деталь '${product.name}' була продана та видалена зі складу.")
                } else {
                    println("Деталі '${product.name}' немає на складі.")
                }
            }
            is Assembly -> {
                if (allAssemblies.remove(product)) {
                    println("Вузол '${product.name}' був проданий та видалений зі складу.")
                } else {
                    println("Вузла '${product.name}' немає на складі.")
                }
            }
            is Mechanism -> {
                if (allMechanisms.remove(product)) {
                    println("Механізм '${product.name}' був проданий та видалений зі складу.")
                } else {
                    println("Механізма '${product.name}' немає на складі.")
                }
            }
            else -> println("Такого типу продукції не існує.")
        }
    }
    fun info(): String {
        val detailsInfo = if (allDetails.isEmpty()) "немає" else allDetails.joinToString(", ") { it.name }
        val assembliesInfo = if (allAssemblies.isEmpty()) "немає" else allAssemblies.joinToString(", ") { it.name }
        val mechanismsInfo = if (allMechanisms.isEmpty()) "немає" else allMechanisms.joinToString(", ") { it.name }

        return "Склад:\nДеталі: $detailsInfo\nВузли: $assembliesInfo\nМеханізми: $mechanismsInfo"
    }
}