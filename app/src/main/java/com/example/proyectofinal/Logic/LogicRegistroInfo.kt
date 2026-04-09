package com.example.proyectofinal.Logic

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

// Listas de datos
val dias = (1..31).map { it.toString() }
val meses = listOf(
    "Enero",
    "Febrero",
    "Marzo",
    "Abril",
    "Mayo",
    "Junio",
    "Julio",
    "Agosto",
    "Septiembre",
    "Octubre",
    "Noviembre",
    "Diciembre"
)
// Obtenemos el año actual
@RequiresApi(Build.VERSION_CODES.O)
val anioActual = LocalDate.now().year
// Lista de años para mayores o igual de 14 años
@RequiresApi(Build.VERSION_CODES.O)
val anios = (anioActual - 14 downTo 1920).map { it.toString() }
// Lista de años para menores de 14 años
@RequiresApi(Build.VERSION_CODES.O)
val aniosMenor = (anioActual downTo anioActual - 14).map { it.toString() }

// Función que calcula el día máximo de un mes según el mes indicado en indexMonth.
// Devuelve el día máximo de ese mes y la lista de todos los días del mes
fun dayPerMonthFunction(indexMonth: Int): Pair<Int, List<String>> {
    var maxDay = 31
    if (indexMonth == 2) {
        maxDay = 28
    } else if (indexMonth == 4 || indexMonth == 6 || indexMonth == 9 || indexMonth == 11) {
        maxDay = 30
    }
    val dayList = (1..maxDay).map { it.toString() }
    val result = Pair(maxDay, dayList)
    return result
}

// Datos ficticios de Centros y Profesores
val listaGimnasios = listOf("Las Rozas", "Quintanar", "Pedro Muñoz")
val mapaProfesores = mapOf(
    "Las Rozas" to listOf("Ángel Ruiz", "Manuel Ruiz"),
    "Pedro Muñoz" to listOf("Carlos López"),
    "Quintanar" to listOf("Juan José Cantero")
)