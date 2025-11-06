package com.example.lab3.logic

interface Sellable {
    fun buy(product: Product)
    fun sell(product: Product)
}