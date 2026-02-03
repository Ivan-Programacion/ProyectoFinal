package com.example.proyectofinal.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.example.proyectofinal.R

// Definicion de las tipografías
// 1. Necesitamos un Provider, un proveedor. En este caso, Google Fons
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

// 2. Definimos las tipografías que queremos
val Roboto = GoogleFont("Roboto")

// Debido a que Special Gothic da problemas, hemos instalado de forma manual la tipografía y la rescataremos de res/font
// Por lo tanto no se neesita la variable sacad de GoogleFons (al ser manual)

// 3. Definimois las familias que necesitamos
val RobotoFontFamily = FontFamily(
    Font(googleFont = Roboto, fontProvider = provider, weight = FontWeight.Normal),
    Font(googleFont = Roboto, fontProvider = provider, weight = FontWeight.Medium),
    Font(googleFont = Roboto, fontProvider = provider, weight = FontWeight.Light),
)

val SpecialGothicFontFamily = FontFamily(
    Font(resId = R.font.special_gothic_bold, weight = FontWeight.Bold),
    Font(resId = R.font.special_gothic_medium, weight = FontWeight.Medium)
)


// Set of Material typography styles to start with
val Typography = Typography(
    // Títulos
    headlineLarge = TextStyle(
        fontFamily = SpecialGothicFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = SpecialGothicFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
    ),
    // Subtítulos
    titleMedium = TextStyle(
        fontFamily = SpecialGothicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = SpecialGothicFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 28.sp,
    ),
    // Texto primario
    bodyLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
    ),
    // Texto secundario
    bodyMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
    ),
    // Botones
    labelLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 18.sp,
    ),
    // NavigationBar
    labelMedium = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
    ),
    // Otros
    labelSmall = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 16.sp,
    ),
)