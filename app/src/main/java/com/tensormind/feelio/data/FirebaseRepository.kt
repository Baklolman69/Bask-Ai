package com.tensormind.feelio.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

sealed class PairResult {
    data class Success(val pairingCode: String) : PairResult()
    data class Error(val message: String) : PairResult()
}

class FirebaseRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String?
        get() = auth.currentUser?.uid

    suspend fun ensureAuthenticated(): String {
        val currentUser = auth.currentUser
        return if (currentUser != null) {
            currentUser.uid
        } else {
            try {
                val result = auth.signInAnonymously().await()
                result.user?.uid ?: ""
            } catch (e: Exception) {
                ""
            }
        }
    }

    fun createUser(userData: UserData) {
        val docId = if (userData.isGuest && userData.name.isNotBlank()) {
            userData.name.trim()
        } else {
            userData.userId.ifBlank { currentUserId ?: "" }
        }
        if (docId.isBlank()) return

        val userDoc = firestore.collection("users").document(docId)
        val data = hashMapOf(
            "userId" to docId,
            "name" to userData.name,
            "isGuest" to userData.isGuest,
            "authProvider" to if (userData.isGuest) "guest" else "google",
            "createdAt" to FieldValue.serverTimestamp(),
            "lastLogin" to FieldValue.serverTimestamp(),
            "usageStats" to hashMapOf<String, Int>()
        )
        userDoc.set(data, SetOptions.merge())
    }

    fun saveChallenges(userId: String, selectedChallenges: List<String>) {
        if (userId.isEmpty()) return
        val userDoc = firestore.collection("users").document(userId)
        val data = hashMapOf(
            "selectedChallenges" to selectedChallenges,
            "challengesUpdatedAt" to FieldValue.serverTimestamp()
        )
        userDoc.set(data, SetOptions.merge())

        val logDoc = firestore.collection("users")
            .document(userId)
            .collection("onboarding_selections")
            .document()
        logDoc.set(hashMapOf(
            "type" to "challenges_selection",
            "selectedChallenges" to selectedChallenges,
            "timestamp" to FieldValue.serverTimestamp()
        ))
    }

    fun saveThought(userId: String, thoughtText: String, aiResponse: String? = null, recommendedFeature: String? = null) {
        if (userId.isEmpty() || thoughtText.isBlank()) return
        val thoughtLog = hashMapOf(
            "text" to thoughtText.trim(),
            "aiResponse" to (aiResponse ?: ""),
            "recommendedFeature" to (recommendedFeature ?: ""),
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("users")
            .document(userId)
            .collection("thoughts")
            .add(thoughtLog)
    }

    fun logMood(userId: String, moodIndex: Int, emoji: String, label: String = "", date: Date = Date()) {
        if (userId.isEmpty()) return
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = sdf.format(date)
        
        val moodLog = hashMapOf(
            "moodIndex" to moodIndex,
            "emoji" to emoji,
            "label" to label,
            "dateStr" to dateStr,
            "timestamp" to FieldValue.serverTimestamp()
        )
        
        firestore.collection("users")
            .document(userId)
            .collection("mood_logs")
            .document(dateStr)
            .set(moodLog, SetOptions.merge())

        firestore.collection("users")
            .document(userId)
            .set(hashMapOf("lastMood" to moodLog), SetOptions.merge())
    }

    suspend fun getAllMoodLogsMap(userId: String): Map<String, String> {
        if (userId.isEmpty()) return emptyMap()
        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("mood_logs")
                .get()
                .await()

            val map = mutableMapOf<String, String>()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            for (doc in snapshot.documents) {
                val emoji = doc.getString("emoji") ?: continue
                val dateStr = doc.getString("dateStr") ?: doc.id
                if (dateStr.isNotBlank()) {
                    map[dateStr] = emoji
                } else {
                    val timestamp = doc.getTimestamp("timestamp")?.toDate()
                    if (timestamp != null) {
                        map[sdf.format(timestamp)] = emoji
                    }
                }
            }
            map
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun trackUsage(userId: String, featureName: String) {
        if (userId.isEmpty()) return
        
        firestore.collection("users")
            .document(userId)
            .update("usageStats.$featureName", FieldValue.increment(1))
            .addOnFailureListener {
                firestore.collection("users")
                    .document(userId)
                    .set(hashMapOf("usageStats" to hashMapOf(featureName to 1)), SetOptions.merge())
            }

        val activityLog = hashMapOf(
            "activity" to featureName,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("users")
            .document(userId)
            .collection("activity_logs")
            .add(activityLog)
    }

    fun logWater(userId: String, glasses: Int, date: Date = Date()) {
        if (userId.isEmpty()) return
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = sdf.format(date)

        val waterData = hashMapOf(
            "glasses" to glasses,
            "timestamp" to FieldValue.serverTimestamp()
        )

        firestore.collection("users")
            .document(userId)
            .collection("hydration_logs")
            .document(dateStr)
            .set(waterData, SetOptions.merge())
    }

    suspend fun getWaterIntake(userId: String, date: Date = Date()): Int {
        if (userId.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateStr = sdf.format(date)

        return try {
            val doc = firestore.collection("users")
                .document(userId)
                .collection("hydration_logs")
                .document(dateStr)
                .get()
                .await()
            
            doc.getLong("glasses")?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    fun saveHydrationGoal(userId: String, goal: Int, explanation: String) {
        if (userId.isEmpty()) return
        val data = hashMapOf(
            "hydrationGoal" to goal,
            "hydrationGoalExplanation" to explanation,
            "hydrationGoalUpdatedAt" to FieldValue.serverTimestamp()
        )
        firestore.collection("users").document(userId).set(data, SetOptions.merge())
    }

    suspend fun getHydrationGoal(userId: String): Pair<Int, String>? {
        if (userId.isEmpty()) return null
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            val goal = doc.getLong("hydrationGoal")?.toInt() ?: return null
            val explanation = doc.getString("hydrationGoalExplanation") ?: ""
            goal to explanation
        } catch (e: Exception) {
            null
        }
    }

    fun saveWatchPairing(userId: String, pairingCode: String, isPaired: Boolean) {
        if (userId.isEmpty()) return
        val data = hashMapOf(
            "pairingCode" to pairingCode,
            "isPaired" to isPaired,
            "pairedAt" to FieldValue.serverTimestamp()
        )
        firestore.collection("users").document(userId).collection("watch_pairing").document("status").set(data, SetOptions.merge())
        firestore.collection("users").document(userId).set(hashMapOf("isWatchConnected" to isPaired), SetOptions.merge())
    }

    fun saveBiometricSnapshot(userId: String, bpm: Int, spO2: Int, sleepHours: Float, aiAnalysis: String? = null) {
        if (userId.isEmpty()) return
        val snapshot = hashMapOf(
            "bpm" to bpm,
            "spO2" to spO2,
            "sleepHours" to sleepHours,
            "aiAnalysis" to (aiAnalysis ?: ""),
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("users").document(userId).collection("biometrics").add(snapshot)
    }

    suspend fun pairWithWatch(inputCode: String): PairResult {
        val cleanCode = inputCode.replace("-", "").replace(" ", "").trim()
        if (cleanCode.length < 5) {
            return PairResult.Error("Please enter a valid 6-digit pairing code.")
        }

        return try {
            var docRef = firestore.collection("pairing_codes").document(cleanCode)
            var snapshot = docRef.get().await()

            if (!snapshot.exists()) {
                val hyphenated = if (cleanCode.length == 6) "${cleanCode.substring(0, 3)}-${cleanCode.substring(3)}" else cleanCode
                docRef = firestore.collection("pairing_codes").document(hyphenated)
                snapshot = docRef.get().await()
            }

            val status = snapshot.getString("status")
            if (snapshot.exists() && status == "pending") {
                docRef.update(
                    mapOf(
                        "status" to "paired",
                        "pairedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
                PairResult.Success(cleanCode)
            } else {
                docRef.set(
                    mapOf(
                        "status" to "paired",
                        "pairedAt" to FieldValue.serverTimestamp(),
                        "code" to cleanCode
                    ),
                    SetOptions.merge()
                ).await()
                PairResult.Success(cleanCode)
            }
        } catch (e: Exception) {
            PairResult.Success(cleanCode)
        }
    }

    fun subscribeToWatchSyncData(
        code: String,
        onMessagesUpdated: (List<Map<String, Any>>) -> Unit
    ): com.google.firebase.firestore.ListenerRegistration {
        val cleanCode = code.replace("-", "").replace(" ", "").trim()
        return firestore.collection("paired_devices")
            .document(cleanCode)
            .collection("sync_data")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    val list = snapshot.documents.map { it.data ?: emptyMap() }
                    onMessagesUpdated(list)
                }
            }
    }

    fun sendSyncMessageToWatch(code: String, text: String, sender: String = "Mobile App") {
        if (text.isBlank()) return
        val cleanCode = code.replace("-", "").replace(" ", "").trim()
        val data = hashMapOf(
            "text" to text.trim(),
            "sender" to sender,
            "timestamp" to FieldValue.serverTimestamp()
        )
        firestore.collection("paired_devices")
            .document(cleanCode)
            .collection("sync_data")
            .add(data)
    }

    suspend fun getMoodLogsForDate(userId: String, date: Date): List<Map<String, Any>> {
        if (userId.isEmpty()) return emptyList()

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.time

        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        val endOfDay = calendar.time

        return try {
            val snapshot = firestore.collection("users")
                .document(userId)
                .collection("mood_logs")
                .whereGreaterThanOrEqualTo("timestamp", startOfDay)
                .whereLessThanOrEqualTo("timestamp", endOfDay)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()
            
            snapshot.documents.map { it.data ?: emptyMap() }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
