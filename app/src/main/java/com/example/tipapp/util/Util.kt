package com.example.tipapp.util

fun calculateTip(totalBill: Double, tipPercentage: Int) : Double {
    return (totalBill * tipPercentage) / 100
}

fun calculateTotalPerPerson(totalBill: Double, tipAmount:Double, numberOfPeople: Int) : Double {
    return (totalBill + tipAmount) / numberOfPeople
}