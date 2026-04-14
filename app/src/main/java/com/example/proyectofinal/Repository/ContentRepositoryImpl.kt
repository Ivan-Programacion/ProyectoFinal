package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Belt
import com.example.proyectofinal.Model.Center
import com.example.proyectofinal.Model.Content
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    override suspend fun getContentByBelt(beltId: String, collectionName: String): List<Content> {
        return try {
            val snapshot = contentCollections(collectionName)
                .whereEqualTo("beltId", beltId)
                .orderBy("number", Query.Direction.ASCENDING)
                .get()
                .await()
            snapshot.toObjects(Content::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
