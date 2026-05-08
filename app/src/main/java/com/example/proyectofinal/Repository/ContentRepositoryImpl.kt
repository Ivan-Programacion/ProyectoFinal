package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Belt
import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.Content
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ContentRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ContentRepository {

    // Nombres de colecciones
    private val centers = "centers"
    private val belts = "belts"
    // Función para recoger la colección que se necesite indicar
    fun contentCollections(collection: String): CollectionReference {
        return db.collection(collection)
    }

    override suspend fun getCenters(): List<Center> {
        return try {
            val snapshot = contentCollections(centers).get().await()
            snapshot.toObjects(Center::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getBelts(): List<Belt> {
        return try {
            val snapshot = contentCollections(belts).get().await()
            snapshot.toObjects(Belt::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override fun getBeltsStream(): Flow<List<Belt>> = callbackFlow {
        val listener = contentCollections(belts)
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                // Tratamos la excepcion de permiso denegado al cerrar sesión
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val beltList = snapshot.toObjects(Belt::class.java)
                    trySend(beltList)
                }
            }
        awaitClose { listener.remove() }
    }

    override fun getContentStream(beltId: String): Flow<List<Content>> = callbackFlow {
        val listener = contentCollections("content")
            .whereEqualTo("beltId", beltId)
            // Eliminamos orderBy de la query de BD para evitar que requiera un índice compuesto
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    // Ordenamos localmente por la propiedad number
                    val contentList = snapshot.toObjects(Content::class.java).sortedBy { it.number }
                    trySend(contentList)
                }
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getContentByBelt(beltId: String, collectionName: String): List<Content> {
        return try {
            val snapshot = contentCollections(collectionName)
                .whereEqualTo("beltId", beltId)
                .get()
                .await()
            // Ordenamos localmente por la propiedad number
            snapshot.toObjects(Content::class.java).sortedBy { it.number }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
