import android.content.Context

class UXCamSDK private constructor() {

    class Builder {
        private var apiKey: String? = null

        fun apiKey(apiKey: String) = apply {
            this.apiKey = apiKey
        }

        fun build(): UXCamSDK {
            val apiKey = this.apiKey
            require(apiKey != null) {
                "The property \"apiKey\" is null. " +
                        "Please set the value by \"apiKey()\". " +
                        "The property \"apiKey\" is required."
            }
            return UXCamSDK()
        }
    }

    fun start(context: Context): Analytics {
        return Analytics.getInstance(context);
    }
}
