package com.example.proyectofinal.ViewModel

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
import com.example.proyectofinal.ViewModel.AuthUiState
import com.example.proyectofinal.ViewModel.AuthViewModelFactory
import com.example.proyectofinal.ViewModel.BeltsViewModel
import com.example.proyectofinal.ViewModel.BeltsViewModelFactory
import com.example.proyectofinal.ViewModel.AdminListaClientesViewModel
import com.example.proyectofinal.ViewModel.AdminListaClientesViewModelFactory
import com.example.proyectofinal.ViewModel.AdminPerfilClienteViewModel
import com.example.proyectofinal.ViewModel.AdminPerfilClienteViewModelFactory
import com.example.proyectofinal.ViewModel.AdminGestionExamenViewModel
import com.example.proyectofinal.ViewModel.AdminGestionExamenViewModelFactory
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Para la pantalla de carga
        val splasScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Comprobación de token para login automático o no
        val authRepository: AuthRepository = AuthRepositoryImpl()
        val startDestination = if (authRepository.getCurrentUserUid() != null) {
            "listaCinturones"
        } else {
            "login"
        }

        setContent {
            ProyectoFinalTheme(dynamicColor = false) {
                App(startDestination = startDestination)
            }
        }
    }
}

// RequieresApi -> Necesario en la app para que funcione las fechas
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun App(startDestination: String = "login") {
    // Creamos la factoría y el ViewModel para poder usar AuthRepository y UserRepository en cada
    // uno de los ViewModel que utilicemos
    val context = LocalContext.current
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

    val adminListaClientesViewModel: AdminListaClientesViewModel = viewModel(
        factory = AdminListaClientesViewModelFactory(
            authRepository = authRepository,
            userRepository = userRepository
        )
    )

    val adminPerfilClienteViewModel: AdminPerfilClienteViewModel = viewModel(
        factory = AdminPerfilClienteViewModelFactory(
            userRepository = userRepository,
            contentRepository = contentRepository
        )
    )

    val adminGestionExamenViewModel: AdminGestionExamenViewModel = viewModel(
        factory = AdminGestionExamenViewModelFactory(
            examRepository = examRepository,
            userRepository = userRepository,
            authRepository = authRepository,
            contentRepository = contentRepository
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

    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val currentUser by authViewModel.currentUserState.collectAsStateWithLifecycle()

    LaunchedEffect(authUiState) {
        when (authUiState) {
            is AuthUiState.Error -> {
                isErrorSnackbar = true
                if (currentRoute == StateNavigate.login.value || currentRoute == StateNavigate.registroPass.value || currentRoute == StateNavigate.perfil.value || currentRoute == StateNavigate.registro.value) {
                    snackbarHostState.showSnackbar((authUiState as AuthUiState.Error).message)
                    authViewModel.resetUiState() // IMPORTANTE: Reseteamos el estado para que detecte futuros errores iguales
                }
            }

            is AuthUiState.Success -> {
                val message = (authUiState as AuthUiState.Success).message
                // Solo lo enseñamos si hay mensaje y estamos en el perfil
                if (message.isNotEmpty() && currentRoute == StateNavigate.perfil.value) {
                    isErrorSnackbar = false
                    snackbarHostState.showSnackbar(message)
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
            if (currentRoute !in pantallasIniciales) TopBar(currentRoute) {
                controller.popBackStack()
                // Reseteamos los campos de AdminPerfilCliente a lo que ya exisitian, por si se habían cambiado sin Actualizar
                //if(currentRoute == StateNavigate.adminPerfilCliente.value) adminPerfilClienteViewModel.resetViewModelsStatesAdmin()
            }
            // Si lo estamos, no añadimos el TopBar
            else Spacer(Modifier.padding(bottom = 104.dp))
        },
        bottomBar = {
            // Si NO estamos navegando antes de entrar en la aplicación por las pantallas login, registro, etc
            if (currentRoute !in pantallasIniciales) NavBar(
                currentRoute = currentRoute,
                controller = {
                    beforeRoute = it
                    controller.navigate(it)
                },
                isAdmin = currentUser?.role == "TEACHER" || currentUser?.role == "SUPERADMIN"
            )
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
                    viewModel = beltsViewModel
                ) { controller.navigate(it) }
            }
            composable(StateNavigate.perfil.value) {
                Perfil(
                    innerPadding,
                    viewModel = authViewModel
                ) { controller.navigate(it) }
            }
            composable(StateNavigate.favoritos.value) {
                Favoritos(innerPadding) {
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
                ListaContenido(innerPadding) {
                    controller.navigate(
                        it
                    )
                }
            }
            composable(StateNavigate.contenido.value) { Contenido(innerPadding) }
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
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(currentRoute: String?, backNavigation: () -> Unit = {}) {
    TopAppBar(
        { Text(tituloTopBar(currentRoute), style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            // Si es una pantalla secundaría que proviene de una principal:
            /*
            Función "obtenerIndice(route)":
            - Principales de 0 a 3
            - Inciales de -1 a -3
            - Auxiliares de -4 para abajo
             */
            if (obtenerIndice(currentRoute) < -3) {
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
fun NavBar(currentRoute: String?, controller: (route: String) -> Unit, isAdmin: Boolean = false) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
        // Si es admin, muestra este item
        if (isAdmin) {
            NavigationBarItem(
                selected = currentRoute == StateNavigate.adminListaClientes.value,
                { controller(StateNavigate.adminListaClientes.value) },
                icon = { Icon(Icons.Default.Book, contentDescription = "Gestion exámenes") },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.onSecondary
                ),
                label = { Text("Gestión") })
        }
        NavigationBarItem(
            selected = currentRoute == StateNavigate.favoritos.value,
            { controller(StateNavigate.favoritos.value) },
            icon = { Icon(Icons.Default.Star, contentDescription = "Favoritos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text("Favoritos") })
        NavigationBarItem(
            selected = currentRoute == StateNavigate.listaCinturones.value,
            onClick = { controller(StateNavigate.listaCinturones.value) },
            icon = { Icon(Icons.Default.MilitaryTech, contentDescription = "Cinturones") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text("Cinturones") })
        NavigationBarItem(
            selected = currentRoute == StateNavigate.perfil.value,
            onClick = { controller(StateNavigate.perfil.value) },
            icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.onSecondary
            ),
            label = { Text("Perfil") })
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoFinalTheme { App() }
}
