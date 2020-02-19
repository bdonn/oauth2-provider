package oauth2.client

import oauth2.exception.OAuth2ClientRedirectUriNotEstablishedException
import oauth2.exception.OAuth2ClientResponseTypeNotMatchedException
import oauth2.request.OAuth2GrantType
import oauth2.request.OAuth2ResponseType
import oauth2.request.RedirectUri
import oauth2.request.ResponseType

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
    val redirectUris: Set<String> = setOf(),
    val tokenEndpointAuthMethod: OAuth2ClientTokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.NONE,
    val grantTypes: Set<OAuth2GrantType> = setOf(),
    val responseTypes: Set<OAuth2ResponseType> = setOf(),
    val clientName: String? = null,
    val clientUri: String = "",
    val logoUri: String? = null,
    val scope: String? = null,
    val contacts: List<String>? = null,
    val tosUri: String? = null,
    val policyUri: String? = null,
    val jwksUri: String? = null,
    val jwks: List<String>? = null,
    val softwareId: String = "", // required?
    val softwareVersion: String? = null,
    val profiles: Set<OAuth2ClientProfile>? = null
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
    val clientSecret: String? = null, // Issued by the authorization server
    val props: OAuth2ClientProps = OAuth2ClientProps()
) {
    fun isConfidential(): Boolean = isConfidential(this.props.tokenEndpointAuthMethod)

    fun validateAndResolveRedirectUri(redirectUri: RedirectUri?) = this.validateAndResolveRedirectUri(redirectUri?.value)

    fun validateAndResolveRedirectUri(redirectUri: String?): String {
        if(redirectUri != null && !this.props.redirectUris.isNullOrEmpty() && this.props.redirectUris.contains(redirectUri)) {
            throw OAuth2ClientRedirectUriNotEstablishedException(clientId = this.clientId)
        }

        return redirectUri ?: this.props.redirectUris.first()
    }

    fun validateResponseType(responseType: ResponseType) {
        if(!this.props.responseTypes.any { it.type == responseType.value }) {
            throw OAuth2ClientResponseTypeNotMatchedException(clientId = clientId, responseType = responseType)
        }
    }

    companion object {
        fun isConfidential(tokenEndpointAuthMethod: OAuth2ClientTokenEndpointAuthMethod?): Boolean = tokenEndpointAuthMethod != OAuth2ClientTokenEndpointAuthMethod.NONE
    }
}