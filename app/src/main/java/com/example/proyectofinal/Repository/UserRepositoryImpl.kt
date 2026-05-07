package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull
import java.net.ConnectException

class UserRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) : UserRepository {

    // Nombre de la colección User
    private val collection = "users"

    // Referencia a la colección de User
    private val userCollection = db.collection(collection)

    override suspend fun getUser(userId: String): User? {
        if (userId.isBlank()) return null
        return try {
            val snapshot = userCollection.document(userId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /*
    Cada vez que surja un cambio en Firestore para ese usuario , el listener captura el snapshot
    (el nuevo estado) y lo envía a la app a través del canal (trySend(user)) en tiempo real.
     */
    override fun getUserStream(userId: String): Flow<User?> = if (userId.isBlank()) flowOf(null) else callbackFlow {
        // addSnapshotListener escucha cambios en tiempo real. Y si lo hay, lo envía a la app
        val subscription = userCollection.document(userId).addSnapshotListener { snapshot, error ->
            // Tratamos la excepcion de permiso denegado al cerrar sesión
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val user = snapshot.toObject(User::class.java)
                trySend(user)
            } else {
                trySend(null)
            }
        }
        awaitClose { subscription.remove() }
    }

    override suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = userCollection.get().await()
            snapshot.toObjects(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun createUser(user: User): Boolean {
        return try {
            userCollection.document(user.id).set(user).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun updateUser(user: User): Boolean {
        return try {
            // Firestore espera infinitamente si no hay internet al hacer .await(). 
            // Por ello, le ponemos un límite de 5 segundos. Si en 5s no se ha conectado, consideramos fallo de red.
            val result = withTimeoutOrNull(5000) {
                userCollection.document(user.id).set(user).await()
                true
            }
            result ?: true // Siempre true aunque haya fallo de conexión porque cuando recupere la conexión, se producirá el cambio
        } catch (e: FirebaseNetworkException) {
            e.printStackTrace()
            true
        } catch (e: ConnectException) {
            e.printStackTrace()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteUser(userId: String): Boolean {
        if (userId.isBlank()) return false
        return try {
            userCollection.document(userId).delete().await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun getCenters(): List<Center> {
        return try {
            val snapshot = db.collection("centers").get().await()
            snapshot.toObjects(Center::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getTeachersByCenter(centerId: String): List<User> {
        if (centerId.isBlank()) return emptyList()
        return try {
            val snapshot = userCollection
                .whereEqualTo("role", "TEACHER")
                .whereEqualTo("centerId", centerId)
                .get()
                .await()
            snapshot.toObjects(User::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getStudentsByTeacherStream(teacherId: String): Flow<List<User>> = if (teacherId.isBlank()) flowOf(emptyList()) else callbackFlow {
        val subscription = userCollection
            .whereEqualTo("clientApproved", true)
            .whereArrayContains("teacherIds", teacherId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val users = snapshot.toObjects(User::class.java)
                    trySend(users)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getStudentsForExam(centerId: String, teacherId: String): Flow<List<User>> = if (centerId.isBlank() || teacherId.isBlank()) flowOf(emptyList()) else callbackFlow {
        val query = userCollection
            .whereEqualTo("centerId", centerId)
            // Se puede filtrar por professor también
            .whereEqualTo("teacherId", teacherId)

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val users = snapshot.toObjects(User::class.java)
                trySend(users)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateStudentExamStatus(userId: String, status: String, text: String) {
        if (userId.isBlank()) return
        try {
            userCollection.document(userId).update(
                mapOf("examStatus" to status, "examText" to text)
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun updateAllStudentsExamStatusByCenter(centerId: String, oldStatus: String, newStatus: String, text: String) {
        if (centerId.isBlank()) return
        try {
            val students = userCollection
                .whereEqualTo("centerId", centerId)
                .whereEqualTo("examStatus", oldStatus)
                .get().await()
            // Utilización de rubBatch para actualizar los documentos de usuarios en cascada
            db.runBatch { batch ->
                students.documents.forEach { doc ->
                    batch.update(doc.reference, "examStatus", newStatus)
                    batch.update(doc.reference, "examText", text)
                }
            }.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun passExamAndUpgradeBelt(userId: String, newBeltId: String, status: String, text: String) {
        if (userId.isBlank()) return
        try {
            userCollection.document(userId).update(
                mapOf("examStatus" to status, "examText" to text, "beltId" to newBeltId)
            ).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
