package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Exam
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await

class ExamRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ExamRepository {

    private val examCollection = db.collection("exam")

    override suspend fun getExam(centerId: String): Exam? {
        return try {
            val snapshot = examCollection.document(centerId).get().await()
            if (snapshot.exists()) {
                snapshot.toObject(Exam::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun observeExam(centerId: String): kotlinx.coroutines.flow.Flow<Exam?> = kotlinx.coroutines.flow.callbackFlow {
        val listener = examCollection.document(centerId).addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(null)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                trySend(snapshot.toObject(Exam::class.java))
            } else {
                trySend(Exam(currentStatus = "CLOSED"))
            }
        }
        awaitClose { listener.remove() }
    }

    override suspend fun updateExamStatus(centerId: String, currentStatus: String, infoMessage: String) {
        try {
            val updates = mutableMapOf<String, Any>(
                "currentStatus" to currentStatus,
                "infoMessage" to infoMessage
            )

            examCollection.document(centerId).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
