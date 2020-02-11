package oauth2.client

enum class OAuth2ClientType(val type: String) {
    CONFIDENTIAL("confidential"), // capable of maintaining the confidentiality of their credentials
    PUBLIC("public") // incapable of maintaining the confidentiality of their credentials
}

enum class OAuth2ClientProfile(val profile: String) {
    WEB_APP("web application"),
    USER_AGENT("user-agent-based application"),
    NATIVE_APP("native application")
}

data class OAuth2Client(
    val clientId: String, // Issued by the authorization server
    val clientSecret: String,
    val redirectUri: String,
    val type: OAuth2ClientType,
    val profiles: List<OAuth2ClientProfile>
)