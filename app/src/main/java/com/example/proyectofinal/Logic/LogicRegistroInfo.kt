package com.example.proyectofinal.Logic

// Listas de datos
val dias = (1..31).map { it.toString() }
val meses = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
val anios = (2026 downTo 1920).map { it.toString() }

fun dayPerMonthFunction (indexMonth: Int): Pair<Int, List<String>> {
    var maxDay = 31
    if(indexMonth == 2) {
        maxDay = 28
    } else if (indexMonth == 4 || indexMonth == 6 || indexMonth == 9 || indexMonth == 11) {
        maxDay = 30
    }
    val dayList = (1..maxDay).map { it.toString() }
    val result = Pair(maxDay, dayList)
    return result
}