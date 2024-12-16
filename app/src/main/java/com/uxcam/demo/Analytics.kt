import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject
import java.util.*

/**
 * AnalyticsSDK is a singleton class for managing analytics sessions and events.
 * It provides APIs to start/stop a session and log events with associated properties.
 */
class Analytics private constructor(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var sessionId: String? =
        null // Holds the current session ID, null if no session is active.

    companion object {
        private const val PREF_NAME = "AnalyticsSDK" // SharedPreferences name
        private const val SESSION_KEY = "current_session" // Key to store the current session ID
        private const val EVENTS_KEY = "session_events" // Key to store events data as JSON

        @Volatile
        private var instance: Analytics? = null // Volatile singleton instance

        /**
         * Retrieves the singleton instance of AnalyticsSDK.
         * If not created, it initializes a new instance in a thread-safe manner.
         */
        fun getInstance(context: Context): Analytics {
            return instance ?: synchronized(this) {
                instance ?: Analytics(context).also { instance = it }
            }
        }
    }

    /**
     * Start a new analytics session.
     * Generates a unique session ID and initializes the event storage.
     */
    fun startSession() {
        sessionId = UUID.randomUUID().toString() // Generate a unique session ID
        val editor = sharedPreferences.edit()
        editor.putString(SESSION_KEY, sessionId) // Save the session ID
        editor.putString(
            EVENTS_KEY,
            JSONObject().toString()
        ) // Initialize an empty JSON object for events
        editor.apply()
    }

    /**
     * Add an event to the current session.
     * @param eventName Name of the event (required).
     * @param properties A map of key-value pairs providing additional event data (optional).
     * @throws IllegalStateException if no active session exists.
     */
    fun addEvent(eventName: String, properties: Map<String, Any>?) {
        sessionId?.let {
            // Retrieve existing events from SharedPreferences
            val eventsJson = sharedPreferences.getString(EVENTS_KEY, JSONObject().toString())
            val eventsObject = JSONObject(eventsJson ?: JSONObject().toString())

            // Create a JSON object for the new event
            val eventDetails = JSONObject()
            eventDetails.put("timestamp", System.currentTimeMillis()) // Add timestamp to the event

            // Add properties to the event if provided
            properties?.let { props ->
                for ((key, value) in props) {
                    eventDetails.put(key, value)
                }
            }

            // Add the new event to the events JSON object
            eventsObject.put(eventName, eventDetails)

            // Save updated events to SharedPreferences
            sharedPreferences.edit().putString(EVENTS_KEY, eventsObject.toString()).apply()
        }
            ?: throw IllegalStateException("No active session. Start a session first.") // Error if no session exists
    }

    /**
     * End the current analytics session.
     * Removes the session ID from SharedPreferences and clears the active session.
     * @throws IllegalStateException if no active session exists.
     */
    fun endSession() {
        sessionId?.let {
            sessionId = null // Clear the current session ID
            sharedPreferences.edit().remove(SESSION_KEY)
                .apply() // Remove session ID from SharedPreferences
        } ?: throw IllegalStateException("No active session to end.") // Error if no session exists
    }

    /**
     * Retrieve the session data.
     * This can be used for debugging or exporting events data.
     * @return A JSON string containing all logged events for the current session.
     */
    fun getSessionData(): String {
        return sharedPreferences.getString(EVENTS_KEY, "{}")
            ?: "{}" // Default to an empty JSON object
    }
}
