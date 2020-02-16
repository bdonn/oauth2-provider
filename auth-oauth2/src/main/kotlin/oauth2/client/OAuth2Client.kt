package oauth2.client

import oauth2.request.OAuth2GrantType
import oauth2.request.OAuth2ResponseType

enum class OAuth2ClientProfile(val profile: String) {
    WEB_APP("web application"),
    USER_AGENT("user-agent-based application"),
    NATIVE_APP("native application")
}

enum class OAuth2ClientTokenEndpointAuthMethod {
    NONE,
    CLIENT_SECRET_POST,
    CLIENT_SECRET_BASIC
}

data class OAuth2ClientProps(
    val redirectUris: Set<String>,
    val tokenEndpointAuthMethod: OAuth2ClientTokenEndpointAuthMethod,
    val grantTypes: Set<OAuth2GrantType>,
    val responseTypes: Set<OAuth2ResponseType>,
    val clientName: String?,
    val clientUri: String,
    val logoUri: String?,
    val scope: String?,
    val contacts: List<String>?,
    val tosUri: String?,
    val policyUri: String?,
    val jwksUri: String?,
    val jwks: List<String>?,
    val softwareId: String, // required?
    val softwareVersion: String?,
    val profiles: Set<OAuth2ClientProfile>
) {
    constructor(request: OAuth2ClientRegistrationRequest) : this(
        redirectUris = request.redirectUris,
        tokenEndpointAuthMethod = request.tokenEndpointAuthMethod,
        grantTypes = request.grantTypes,
        responseTypes = request.responseTypes,
        clientName = request.clientName,
        clientUri = request.clientUri,
        logoUri = request.logoUri,
        scope = request.scope,
        contacts = request.contacts,
        tosUri = request.tosUri,
        policyUri = request.policyUri,
        jwksUri = request.jwksUri,
        jwks = request.jwks,
        softwareId = request.softwareId,
        softwareVersion = request.softwareVersion,
        profiles = request.profiles
    )
}

data class OAuth2Client(
    val clientId: String, // Issued by the authorization server
    val clientSecret: String?, // Issued by the authorization server
    val props: OAuth2ClientProps?
) {
    fun isConfidential(): Boolean = isConfidential(this.props?.tokenEndpointAuthMethod)

    companion object {
        fun isConfidential(tokenEndpointAuthMethod: OAuth2ClientTokenEndpointAuthMethod?): Boolean = tokenEndpointAuthMethod != OAuth2ClientTokenEndpointAuthMethod.NONE
    }
}