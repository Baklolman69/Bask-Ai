package com.tensormind.feelio.data

import com.tensormind.feelio.BuildConfig
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object GroqRepository {
    private val API_KEY = BuildConfig.GROQ_API_KEY.ifBlank { "YOUR_GROQ_API_KEY" }
    private const val ENDPOINT = "https://api.groq.com/openai/v1/chat/completions"
    private const val MODEL = "llama-3.3-70b-versatile"

    data class AiReflection(
        val message: String,
        val recommendedFeature: String?
    )

    suspend fun getReflectionAndRecommendation(
        userThought: String,
        currentMoodEmoji: String? = null,
        userName: String? = null
    ): AiReflection = withContext(Dispatchers.IO) {
        try {
            val url = URL(ENDPOINT)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $API_KEY")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 15000
            }

            val systemPrompt = """
                You are Feelio's compassionate, warm AI wellness guide.
                The user shared their thought: "$userThought" ${if (currentMoodEmoji != null) "and their mood emoji is $currentMoodEmoji" else ""}.
                User's name: ${userName ?: "Friend"}.

                Write a gentle, empathetic 2-sentence reflection acknowledging their feelings.
                Then recommend ONE of these 4 Feelio features to support them:
                - "Meditation for focus" (if stressed, anxious, overwhelmed, or needing focus)
                - "Just need to talk" (if lonely, sad, needing to vent, or wanting a conversation)
                - "CBT test" (if having negative thoughts, self-doubt, or anxiety)
                - "Journal your day" (if reflective, grateful, or wanting to record memories)

                Format clearly:
                Write the 2-sentence empathetic reflection.
                Then include the exact recommended feature title.
            """.trimIndent()

            val rootJson = JSONObject().apply {
                put("model", MODEL)
                put("temperature", 0.7)
                put("max_tokens", 250)
                val messagesArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", systemPrompt)
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", userThought)
                    })
                }
                put("messages", messagesArray)
            }

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(rootJson.toString())
                writer.flush()
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)
                val choices = responseJson.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val content = choices.getJSONObject(0).getJSONObject("message").getString("content")
                    return@withContext parseAiResponse(content)
                }
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.use { it.readText() }
                Log.e("GroqRepository", "API Error ${connection.responseCode}: $errorText")
            }
        } catch (e: Exception) {
            Log.e("GroqRepository", "Exception calling Groq API", e)
        }

        return@withContext AiReflection(
            message = "Thank you for sharing your thought. Taking a moment to pause and listen to yourself is a wonderful step toward peace and clarity.",
            recommendedFeature = matchFeatureFallback(userThought)
        )
    }

    suspend fun getBiometricAnalysis(
        bpm: Int,
        spO2: Int,
        sleepHours: Float,
        moodEmoji: String? = null,
        userName: String? = null
    ): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(ENDPOINT)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $API_KEY")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
                connectTimeout = 10000
                readTimeout = 15000
            }

            val prompt = """
                You are Bask AI Wearable Health Analyst.
                Analyze the user's smartwatch biometric data:
                - Heart Rate: $bpm BPM
                - Blood Oxygen (SpO2): $spO2%
                - Sleep Duration: $sleepHours hours
                - Logged Mood: ${moodEmoji ?: "Neutral"}
                - User Name: ${userName ?: "Friend"}

                Provide a concise, inspiring 3-sentence analysis explaining how their sleep quality and heart rate correlate with their daily mood and energy, offering 1 actionable tip.
            """.trimIndent()

            val rootJson = JSONObject().apply {
                put("model", MODEL)
                put("temperature", 0.7)
                put("max_tokens", 250)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are an expert wearable biometrics and mental wellness AI analyst.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                })
            }

            OutputStreamWriter(connection.outputStream).use { it.write(rootJson.toString()); it.flush() }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val responseJson = JSONObject(responseText)
                val choices = responseJson.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    return@withContext choices.getJSONObject(0).getJSONObject("message").getString("content")
                }
            }
        } catch (e: Exception) {
            Log.e("GroqRepository", "Error in biometric analysis", e)
        }

        return@withContext "Your sleep duration of ${sleepHours}h and stable heart rate of ${bpm} BPM indicate healthy physiological recovery. Consistent sleep quality supports emotional stability throughout your day."
    }

    suspend fun getHydrationCompliment(glasses: Int, userName: String?): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(ENDPOINT)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $API_KEY")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            val prompt = "The user ${userName ?: "Friend"} just drank glass #$glasses of water today. Write a single, very short (max 10 words), warm, and encouraging compliment or fun fact about hydration."

            val rootJson = JSONObject().apply {
                put("model", MODEL)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply { put("role", "system"); put("content", "You are a warm, minimalist wellness assistant.") })
                    put(JSONObject().apply { put("role", "user"); put("content", prompt) })
                })
            }

            OutputStreamWriter(connection.outputStream).use { it.write(rootJson.toString()); it.flush() }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val choices = JSONObject(responseText).optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    return@withContext choices.getJSONObject(0).getJSONObject("message").getString("content").trim()
                }
            }
        } catch (e: Exception) {}
        return@withContext "Great job staying hydrated! 💧"
    }

    suspend fun getHydrationReminder(userName: String?): String = withContext(Dispatchers.IO) {
        try {
            val url = URL(ENDPOINT)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $API_KEY")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            val prompt = "Generate a creative, gentle, and warm 1-sentence reminder for ${userName ?: "Friend"} to drink a glass of water. Make it sound caring, not robotic."

            val rootJson = JSONObject().apply {
                put("model", MODEL)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply { put("role", "system"); put("content", "You are a caring wellness companion.") })
                    put(JSONObject().apply { put("role", "user"); put("content", prompt) })
                })
            }

            OutputStreamWriter(connection.outputStream).use { it.write(rootJson.toString()); it.flush() }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val choices = JSONObject(responseText).optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    return@withContext choices.getJSONObject(0).getJSONObject("message").getString("content").trim()
                }
            }
        } catch (e: Exception) {}
        return@withContext "Time for a quick water break! Your body will thank you. 💧"
    }

    data class HydrationGoalResult(val goal: Int, val explanation: String)

    suspend fun generateHydrationGoal(
        surveyData: Map<String, String>,
        userName: String?
    ): HydrationGoalResult = withContext(Dispatchers.IO) {
        try {
            val url = URL(ENDPOINT)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("Authorization", "Bearer $API_KEY")
                setRequestProperty("Content-Type", "application/json")
                doOutput = true
            }

            val systemPrompt = """
                You are Bask AI Wellness Consultant.
                Calculate a personalized daily water intake goal (in 250ml glasses) for ${userName ?: "User"}.
                Survey Data:
                - Motivation: ${surveyData["motivation"]}
                - Current Intake: ${surveyData["current_intake"]}
                - Health Condition: ${surveyData["health_condition"]}
                - Environment Temperature: ${surveyData["temperature"]}

                Output format:
                - Recommended Glasses: [Integer between 6 and 16]
                - Explanation: [A warm, 2-sentence explanation of why this goal was chosen based on their data.]
            """.trimIndent()

            val rootJson = JSONObject().apply {
                put("model", MODEL)
                put("messages", JSONArray().apply {
                    put(JSONObject().apply { put("role", "system"); put("content", "You are an expert hydration and health AI.") })
                    put(JSONObject().apply { put("role", "user"); put("content", "Please generate my hydration goal.") })
                })
                // In a real implementation, we'd pass the actual system prompt in the messages array.
                // For simplicity here, I'll combine it.
            }
            
            // Re-creating message array with proper system prompt
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
            }
            rootJson.put("messages", messages)

            OutputStreamWriter(connection.outputStream).use { it.write(rootJson.toString()); it.flush() }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val content = JSONObject(responseText).getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
                
                val glasses = Regex("Recommended Glasses: (\\d+)").find(content)?.groupValues?.get(1)?.toInt() ?: 8
                val explanation = Regex("Explanation: (.+)").find(content)?.groupValues?.get(1) ?: "Based on your input, staying hydrated will help you feel your best."
                
                return@withContext HydrationGoalResult(glasses, explanation)
            }
        } catch (e: Exception) {
            Log.e("GroqRepository", "Error generating hydration goal", e)
        }
        return@withContext HydrationGoalResult(8, "A standard daily intake of 8 glasses is a great starting point for your wellness journey.")
    }

    private fun parseAiResponse(content: String): AiReflection {
        val features = listOf("Meditation for focus", "Just need to talk", "CBT test", "Journal your day")
        var matchedFeature: String? = null
        for (f in features) {
            if (content.contains(f, ignoreCase = true)) {
                matchedFeature = f
                break
            }
        }
        return AiReflection(message = content, recommendedFeature = matchedFeature)
    }

    private fun matchFeatureFallback(thought: String): String {
        val lower = thought.lowercase()
        return when {
            lower.contains("talk") || lower.contains("lonely") || lower.contains("sad") -> "Just need to talk"
            lower.contains("stress") || lower.contains("anxious") || lower.contains("work") -> "Meditation for focus"
            lower.contains("think") || lower.contains("doubt") || lower.contains("worry") -> "CBT test"
            else -> "Journal your day"
        }
    }
}
