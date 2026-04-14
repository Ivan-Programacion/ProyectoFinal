package com.example.proyectofinal.Repository

import com.example.proyectofinal.Model.Exam
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ExamRepositoryImpl(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) : ExamRepository {

    private val examCollection = db.collection("exam")

    private val currentExamId = "current_exam"

    override suspend fun getExam(): Exam? {
        return try {
            val snapshot = examCollection.document(currentExamId).get().await()
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
}
