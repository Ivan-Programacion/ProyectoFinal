package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeoutOrNull

class UserRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) : UserRepository {

    // Nombre de la colección User
    private val collection = "users"

    // Referencia a la colección de User
    private val userCollection = db.collection(collection)

    override suspend fun getUser(userId: String): User? {
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
    override fun getUserStream(userId: String): Flow<User?> = callbackFlow {
        // addSnapshotListener escucha cambios en tiempo real. Y si lo hay, lo envía a la app
        val subscription = userCollection.document(userId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
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
            result ?: false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun deleteUser(userId: String): Boolean {
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
}
