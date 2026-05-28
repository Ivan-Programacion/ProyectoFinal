package com.example.proyectofinal.ViewModel

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectofinal.Logic.obtenerIndice
import com.example.proyectofinal.Logic.pantallasIniciales
import com.example.proyectofinal.Logic.tituloTopBar
import com.example.proyectofinal.View.AdminGestionExamen
import com.example.proyectofinal.View.AdminListaClientes
import com.example.proyectofinal.View.AdminPerfilCliente
import com.example.proyectofinal.View.Contenido
import com.example.proyectofinal.View.Favoritos
import com.example.proyectofinal.View.ListaCinturones
import com.example.proyectofinal.View.ListaContenido
import com.example.proyectofinal.View.Login
import com.example.proyectofinal.View.Perfil
import com.example.proyectofinal.View.RegistroInfo
import com.example.proyectofinal.View.RegistroPass
import com.example.proyectofinal.ui.theme.ProyectoFinalTheme
import com.example.proyectofinal.Repository.AuthRepository
import com.example.proyectofinal.Repository.AuthRepositoryImpl
import com.example.proyectofinal.Repository.UserRepositoryImpl
import com.example.proyectofinal.Repository.ContentRepositoryImpl
import com.example.proyectofinal.Repository.ExamRepositoryImpl
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectofinal.Logic.removeFcmTokenOnLogout
import com.example.proyectofinal.Logic.updateFcmTokenInFirestore
import com.example.proyectofinal.R
import com.example.proyectofinal.Services.checkIsAppExpiredSecurely
import com.example.proyectofinal.View.OlvidoPass
import kotlinx.coroutines.launch
import android.app.Activity
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class MainActivity : ComponentActivity() {
    // 1. Variable de estado para controlar cuándo desaparece el logotipo de carga
    private var isCheckingSecurity = true

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Para la pantalla de carga
        // 2. Guardamos la instancia del SplashScreen al instalarlo
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 3. Le decimos al SplashScreen que se quede congelado en pantalla
        // mientras 'isCheckingSecurity' sea true
        splashScreen.setKeepOnScreenCondition { isCheckingSecurity }
        // 4. Lanzamos la comprobación de seguridad en segundo plano antes de cargar la app
        lifecycleScope.launch {
            val isExpired = checkIsAppExpiredSecurely()
            // 5. Añadimos las variables de la logica de los repositorios
            val authRepository: AuthRepository = AuthRepositoryImpl()
            val startDestination = if (authRepository.getCurrentUserUid() != null) {
                "listaCinturones"
            } else {
                "login"
            }
            if (isExpired) {
                // Cerramos el proceso y el usuario nunca llega a ver la interfaz.
                authRepository.logout()
                finishAffinity()
            } else {
                // ¡VÍA LIBRE! La app está en vigor.
                isCheckingSecurity = false // Quitamos el Splash

                // 6. Finalmente, pintamos tu UI de Compose
                setContent {
                    ProyectoFinalTheme(dynamicColor = false) {
                        App(startDestination = startDestination)
                    }
                }
            }
        }
    }
}

// RequieresApi -> Necesario en la app para que funcione las fechas
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(startDestination: String = "login") {
    // Creamos las factorias para poder utilizar los diferentes repositories en cada viewModel que correspondan
    val userRepository = UserRepositoryImpl()
    val authRepository = AuthRepositoryImpl()
    val contentRepository = ContentRepositoryImpl()
    val examRepository = ExamRepositoryImpl()

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            authRepository = authRepository,
            userRepository = userRepository,
            examRepository = examRepository
        )
    )

    val beltsViewModel: BeltsViewModel = viewModel(
        factory = BeltsViewModelFactory(
            authRepository = authRepository,
            userRepository = userRepository,
            contentRepository = contentRepository
        )
    )

    val contentViewModel: ContentViewModel = viewModel(
        factory = ContentViewModelFactory(
            contentRepository = contentRepository
        )
    )

    val adminListaClientesViewModel: AdminListaClientesViewModel = viewModel(
        factory = AdminListaClientesViewModelFactory(
            authRepository = authRepository,
            userRepository = userRepository
        )
    )

    val adminPerfilClienteViewModel: AdminPerfilClienteViewModel = viewModel(
        factory = AdminPerfilClienteViewModelFactory(
            userRepository = userRepository,
            contentRepository = contentRepository,
            authViewModel = authViewModel
        )
    )

    val adminGestionExamenViewModel: AdminGestionExamenViewModel = viewModel(
        factory = AdminGestionExamenViewModelFactory(
            examRepository = examRepository,
            userRepository = userRepository,
            authRepository = authRepository,
            contentRepository = contentRepository,
            authViewModel = authViewModel
        )
    )

    // controller --> para poder navegar entre pantallas (remember)
    val controller = rememberNavController()
    // para saber en que pantalla estamos exactamente
    val currentRoute = controller.currentBackStackEntryAsState().value?.destination?.route
    // AÑADIR ESTO PARA LAS TRANSICIONES
    var beforeRoute by remember { mutableStateOf("listaCinturones") }
    // Para snackbarHost --> utilizado para los mensajes de errores y éxitos
    val snackbarHostState = remember { SnackbarHostState() }
    // Guardamos si el snackbar mostrado es de error o no para darle estilo
    var isErrorSnackbar by remember { mutableStateOf(true) }

    // PARA COMPROBAR LA APP Y CERRARLA PASADA LA FECHA EN EL TESTING
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Lanzamos una corrutina porque la comprobación es una función suspend
                coroutineScope.launch {
                    val isExpired = checkIsAppExpiredSecurely()
                    if (isExpired) {
                        // Si se dejó la app abierta y caduca, cerramos sesión y matamos la app
                        authRepository.logout()
                        (context as? Activity)?.finishAffinity()
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    // -------------------------------------

    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUserState.collectAsStateWithLifecycle()

    val beltsState by beltsViewModel.beltsUiState.collectAsStateWithLifecycle()
    val selectedBeltId by contentViewModel.selectedBeltId.collectAsStateWithLifecycle()
    // Para guardar el nombre del cinturon seleccionado (según el idioma)
    val selectedBeltName =
        beltsState.find { it.belt.id == selectedBeltId }?.belt?.name?.get(com.example.proyectofinal.Logic.locale)
            ?: ""

    // El mensaje de error o de éxito del snackBar
    // El mensaje será el correspondiente indicado y se le enviará el mensaje traducido según el código de stringResource
    // Si no tuviera traducción y fuera texto estático, lo muestra sin más
    val errorMessageText = (authUiState as? AuthUiState.Error)?.let { errorState ->
        errorState.messageRes?.let { stringResource(it) } ?: errorState.message ?: ""
    }

    val successMessageText = (authUiState as? AuthUiState.Success)?.let { successState ->
        successState.messageRes?.let { stringResource(it) } ?: successState.message
    }

    // Permisos de notificación y guardar el token FCM de Firebase en el documento del usuario actual
    NotificationPermissionAndTokenSetup(currentUser?.id)

    // Si el usuario es desactivado (dado de baja), se le cierra la sesión
    LaunchedEffect(currentUser?.isActive) {
        if (currentUser?.isActive == false) {
            // Añadimos un retraso para evitar falsos positivos provocados por un rebote
            // de la persistencia de caché offline en Firestore al iniciar sesión tras un alta de admin.
            //kotlinx.coroutines.delay(2000)
            removeFcmTokenOnLogout(currentUser?.id) {
                authViewModel.logoutUser()
                authViewModel.resetUiState()
                if (currentRoute != StateNavigate.login.value) {
                    controller.navigate(StateNavigate.login.value) {
                        popUpTo(0)
                    }
                }
            }
        }
    }

    LaunchedEffect(authUiState) {
        when (authUiState) {
            is AuthUiState.Error -> {
                isErrorSnackbar = true
                if (currentRoute == StateNavigate.login.value ||
                    currentRoute == StateNavigate.registroPass.value ||
                    currentRoute == StateNavigate.perfil.value ||
                    currentRoute == StateNavigate.registro.value ||
                    currentRoute == StateNavigate.adminPerfilCliente.value ||
                    currentRoute == StateNavigate.adminGestionExamen.value ||
                    currentRoute == StateNavigate.olvidoPass.value
                ) {
                    snackbarHostState.showSnackbar(errorMessageText ?: "")
                    authViewModel.resetUiState() // IMPORTANTE: Reseteamos el estado para que detecte futuros errores iguales
                }
            }

            is AuthUiState.Success -> {
                // Solo lo enseñamos si hay mensaje y estamos en las pantallas indicadas
                if (!successMessageText.isNullOrEmpty() && (
                            currentRoute == StateNavigate.perfil.value ||
                                    currentRoute == StateNavigate.adminPerfilCliente.value ||
                                    currentRoute == StateNavigate.adminGestionExamen.value ||
                                    currentRoute == StateNavigate.contenido.value ||
                                    currentRoute == StateNavigate.olvidoPass.value
                            )
                ) {
                    isErrorSnackbar = false
                    snackbarHostState.showSnackbar(successMessageText)
                    // Si es la pantalla de OlvidoPass, reseteamos la Ui cuando se salga de dicha pantalla
                    if (currentRoute != StateNavigate.olvidoPass.value)
                        authViewModel.resetUiState()
                }
            }

            else -> {}
        }
    }

    Scaffold(
        snackbarHost = {
            // Definimos el estilo del SnackbarHost
            SnackbarHost(hostState = snackbarHostState) { snackbarData ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    containerColor = if (isErrorSnackbar) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.tertiary,
                    // Si es error se pone rojo, si no (éxito), se usa color tertiary
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = snackbarData.visuals.message,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        topBar = {
            // Si NO estamos navegando antes de entrar en la aplicación por las pantallas login, registro, etc
            // controller.popBackStack() --> Para volver atrás en caso de que la pantalla tenga flecha hacia atrás
            // Pasamos el nombre del cinturón para que se muestre cuando estemos en ListaContenido o Contenido
            if (currentRoute !in pantallasIniciales) TopBar(currentRoute, selectedBeltName) {
                controller.popBackStack()
                // Reseteamos los campos de AdminPerfilCliente a lo que ya exisitian, por si se habían cambiado sin Actualizar
                //if(currentRoute == StateNavigate.adminPerfilCliente.value) adminPerfilClienteViewModel.resetViewModelsStatesAdmin()
            }
            // Si lo estamos, no añadimos el TopBar
            else Spacer(Modifier.padding(bottom = 104.dp))
        },
        bottomBar = {
            // Si NO estamos navegando antes de entrar en la aplicación por las pantallas login, registro, etc
            if (currentRoute !in pantallasIniciales) {
                // Determinamos qué ítem del menú debe estar activo basado en la pantalla secundaria
                // Se divide la navegación entre los items del navBar y las pantallas dentro de cada uno de los items
                // De tal forma que conseguimos que el item se quede seleccionado en el último item que se haya pulsado
                val activeTab = if (currentRoute == StateNavigate.listaContenido.value ||
                    currentRoute == StateNavigate.contenido.value ||
                    currentRoute == StateNavigate.adminGestionExamen.value ||
                    currentRoute == StateNavigate.adminPerfilCliente.value
                ) {
                    beforeRoute
                } else currentRoute

                NavBar(
                    currentRoute = activeTab,
                    controller = {
                        beforeRoute = it
                        controller.navigate(it)
                    },
                    isAdmin = currentUser?.role == "TEACHER" || currentUser?.role == "SUPERADMIN"
                )
            }
            // Si lo estamos, no añadimos el NavBar
            else Spacer(Modifier.padding(bottom = 104.dp))
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            controller,
            startDestination = startDestination,
            enterTransition = {
                // Las pasamos a la función para obtener su índice
                val inicial = obtenerIndice(initialState.destination.route)
                val destino = obtenerIndice(targetState.destination.route)
                // Si la pantalla inicial y la de destino son iguales, no hacemos nada
                if (destino == inicial) {
                    EnterTransition.None

                    // Si las pantallas son las correspondientes a antes de iniciar sesión (login, registro, olvido contraseña, etc), hacemos fadeIn
                } else if (destino < 0 || inicial < 0) {
                    fadeIn(animationSpec = tween(100))

                    // Si no, hacemos transicion horizontal según la posición
                } else {
                    if (destino > inicial) {
                        // AVANCE (Ej: Favoritos -> Cinturones): Entra desde la DERECHA
                        slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(400))
                    } else {
                        // RETROCESO (Ej: Perfil -> Cinturones): Entra desde la IZQUIERDA
                        slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(400))
                    }
                }
            },
            exitTransition = {

                val inicial = obtenerIndice(initialState.destination.route)
                val destino = obtenerIndice(targetState.destination.route)
                // Si la pantalla inicial y la de destino son iguales, no hacemos nada
                if (destino == inicial) {
                    ExitTransition.None

                    // Si las pantallas son las correspondientes a antes de iniciar sesión (login, registro, olvido contraseña, etc), hacemos fadeOut
                } else if (inicial < 0 || destino < 0) {
                    fadeOut(animationSpec = tween(100))

                    // Si no, hacemos transicion horizontal según la posición
                } else {
                    if (destino > inicial) {
                        // AVANCE: La pantalla actual sale por la IZQUIERDA para dar con la nueva pantalla
                        slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(400))
                    } else {
                        // RETROCESO: La pantalla actual sale por la DERECHA para dar con la nueva pantalla
                        slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(400))
                    }
                }
            }
        ) {
            composable(StateNavigate.login.value) {
                Login(
                    innerPadding,
                    viewModel = authViewModel
                ) { controller.navigate(it) }
            }
            composable(StateNavigate.registro.value) {
                RegistroInfo(
                    innerPadding,
                    viewModel = authViewModel
                ) { controller.navigate(it) }
            }
            composable(StateNavigate.listaCinturones.value) {
                ListaCinturones(
                    paddingValues = innerPadding,
                    viewModel = beltsViewModel,
                    contentViewModel = contentViewModel
                ) { controller.navigate(it) }
            }
            composable(StateNavigate.perfil.value) {
                Perfil(
                    innerPadding,
                    viewModel = authViewModel
                    // en Perfil realizamos diferente a los demás porque necesitamos limpiar todas las pantallas para evitar errores
                ) { ruta ->
                    controller.navigate(ruta) {
                        // Si cerramos sesión desde el Perfil, limpiamos pantallas (viewModels)
                        if (ruta == StateNavigate.login.value) {
                            // Usamos el ID del grafo principal para asegurar que borra todo
                            popUpTo(controller.graph.id) {
                                inclusive = true
                            }
                        }
                        // Evita que se creen múltiples copias de la misma pantalla si se pulsa rápido
                        launchSingleTop = true
                    }
                }
            }
            composable(StateNavigate.favoritos.value) {
                Favoritos(
                    innerPadding,
                    authViewModel,
                    contentViewModel,
                    beltsViewModel
                ) {
                    controller.navigate(
                        it
                    )
                }
            }
            composable(StateNavigate.registroPass.value) {
                RegistroPass(
                    innerPadding,
                    viewModel = authViewModel,
                    { controller.popBackStack() }) { controller.navigate(it) }
            }
            composable(StateNavigate.listaContenido.value) {
                ListaContenido(
                    paddingValues = innerPadding,
                    viewModel = contentViewModel
                ) {
                    controller.navigate(
                        it
                    )
                }
            }
            composable(StateNavigate.contenido.value) {
                Contenido(
                    paddingValues = innerPadding,
                    viewModel = contentViewModel,
                    authViewModel = authViewModel
                )
            }
            composable(StateNavigate.adminListaClientes.value) {
                AdminListaClientes(
                    innerPadding,
                    viewModel = adminListaClientesViewModel,
                    adminPerfilViewModel = adminPerfilClienteViewModel
                ) { controller.navigate(it) }
            }
            composable(StateNavigate.adminGestionExamen.value) {
                AdminGestionExamen(
                    innerPadding,
                    viewModel = adminGestionExamenViewModel,
                    adminPerfilViewModel = adminPerfilClienteViewModel
                ) { controller.navigate(it) }
            }
            composable(StateNavigate.adminPerfilCliente.value) {
                AdminPerfilCliente(
                    innerPadding,
                    viewModel = adminPerfilClienteViewModel
                ) { controller.navigate(it) }
            }
            composable(StateNavigate.olvidoPass.value) {
                OlvidoPass(
                    innerPadding,
                    viewModel = authViewModel,
                    { controller.popBackStack() }) { controller.navigate(it) }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(currentRoute: String?, beltName: String = "", backNavigation: () -> Unit = {}) {
    TopAppBar(
        {
            if (currentRoute == StateNavigate.listaContenido.value || currentRoute == StateNavigate.contenido.value) {
                Text(
                    stringResource(tituloTopBar(currentRoute)) + beltName,
                    style = MaterialTheme.typography.titleMedium
                )
            } else {
                Text(
                    stringResource(tituloTopBar(currentRoute)),
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        navigationIcon = {
            // Si es una pantalla secundaría que proviene de una principal:
            /*
            Función "obtenerIndice(route)":
            - Principales de 0 a 3
            - Inciales de -1 a -4
            - Auxiliares de -4 para abajo
             */
            if (obtenerIndice(currentRoute) < -4) {
                // Flecha para atrás de navegación
                IconButton(onClick = { backNavigation() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver atrás",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}

@Composable
fun NavBar(
    currentRoute: String?,
    controller: (String) -> Unit,
    isAdmin: Boolean = false
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        // Si es admin y está activo, muestra este item
        if (isAdmin) {
            NavigationBarItem(
                selected = currentRoute == StateNavigate.adminListaClientes.value,
                { controller(StateNavigate.adminListaClientes.value) },
                icon = {
                    Icon(
                        Icons.Default.Book,
                        contentDescription = stringResource(R.string.navbar_admin)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.onSecondary
                ),
                label = { Text(stringResource(R.string.navbar_admin)) })
        }
        NavigationBarItem(
            selected = currentRoute == StateNavigate.favoritos.value,
            { controller(StateNavigate.favoritos.value) },
            icon = {
                Icon(
                    Icons.Default.Star,
                    contentDescription = stringResource(R.string.title_favoritos)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text(stringResource(R.string.title_favoritos)) })
        NavigationBarItem(
            selected = currentRoute == StateNavigate.listaCinturones.value,
            onClick = { controller(StateNavigate.listaCinturones.value) },
            icon = {
                Icon(
                    Icons.Default.MilitaryTech,
                    contentDescription = stringResource(R.string.title_cinturones)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text(stringResource(R.string.title_cinturones)) })
        NavigationBarItem(
            selected = currentRoute == StateNavigate.perfil.value,
            onClick = { controller(StateNavigate.perfil.value) },
            icon = {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.title_perfil)
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text(stringResource(R.string.title_perfil)) })
    }
}

// Notificación push (componente)
@Composable
fun NotificationPermissionAndTokenSetup(currentUserId: String?) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) updateFcmTokenInFirestore(currentUserId)
        }
    )
    // Lanzamos la función de actualizar token (comprobando permisos de Android 13 si fuera necesario)
    LaunchedEffect(currentUserId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            updateFcmTokenInFirestore(currentUserId)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoFinalTheme { App() }
}
