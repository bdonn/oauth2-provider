package oauth2.response

import oauth2.code.AuthorizationCode
import oauth2.context.OAuth2Context
import oauth2.request.*
import oauth2.request.Scope
import oauth2.request.State
import oauth2.token.OAuth2AccessToken
import oauth2.token.OAuth2RefreshToken

// TODO: move
enum class OAuth2TokenType(val type: String) {
    BEARER("Bearer"),
    MAC("Mac"); // Message Authentication Code
}

// TODO: move
enum class OAuth2Error(val code: String?) {
    INVALID_REQUEST("invalid_request"),
    INVALID_CLIENT("invalid_client"),
    INVALID_GRANT("invalid_grant"),
    UNAUTHORIZED_CLIENT("unauthorized_client"),
    UNSUPPORTED_GRANT_TYPE("unsupported_grant_type"),
    INVALID_SCOPE("invalid_scope");

    companion object {
        const val NAME = "error"
    }
}

interface OAuth2ResponseParameter: OAuth2Parameter {
    val value: String?
    val name: String
    fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?): Boolean = false // default not required
    fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?): Boolean = true // default validation ok
}

//enum class StatusCode(val code: Int): OAuth2ResponseParameter {
//    OK(200),
//    FOUND(302),
//    BAD_REQUEST(400);
//}

data class AccessToken(override val value: String): OAuth2ResponseParameter {

    constructor(accessToken: OAuth2AccessToken): this(accessToken.token)

    companion object {
        const val NAME = "access_token"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        endpoint == OAuth2Endpoint.TOKEN // required for all token response

    override fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        !(isRequired(grantType, endpoint, context) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class RefreshToken(override val value: String): OAuth2ResponseParameter {

    constructor(refreshToken: OAuth2RefreshToken): this(refreshToken.token)

    companion object {
        const val NAME = "refresh_token"
    }

    override val name
        get() = NAME

    override fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        !(isRequired(grantType, endpoint, context) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class TokenType(override val value: String?): OAuth2ResponseParameter {

    constructor(tokenType: OAuth2TokenType): this(tokenType.type)

    companion object {
        const val NAME = "token_type"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) = true // required

    override fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        !(isRequired(grantType, endpoint, context) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class ExpiresIn(val time: Int?): OAuth2ResponseParameter {

    override val value: String? = null // not used

    companion object {
        const val NAME = "expires_in"
    }

    override val name
        get() = NAME

    override fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        !(isRequired(grantType, endpoint, context) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class Scope(override val value: String?): OAuth2ResponseParameter {
    constructor(scope: Scope?): this(scope?.value)

    companion object {
        const val NAME = "scope"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        (
         grantType == OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint == OAuth2Endpoint.TOKEN
         || grantType == OAuth2GrantType.IMPLICIT_GRANT && endpoint == OAuth2Endpoint.AUTHORIZATION
         || grantType == OAuth2GrantType.RESOURCE_OWNER_CREDENTIALS_GRANT && endpoint == OAuth2Endpoint.TOKEN
         || grantType == OAuth2GrantType.CLIENT_CREDENTIALS_GRANT && endpoint == OAuth2Endpoint.TOKEN
        ) &&  context?.scope.let { it?.value != value } // 요청 scope와 동일할 경우 OPTIONAL, 다를 경우 REQUIRED

    override fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        !(isRequired(grantType, endpoint, context) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class State(override val value: String?): OAuth2ResponseParameter {
    constructor(state: State?): this(state?.value)

    companion object {
        const val NAME = "state"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        (grantType == OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint == OAuth2Endpoint.AUTHORIZATION
            || grantType == OAuth2GrantType.IMPLICIT_GRANT && endpoint == OAuth2Endpoint.AUTHORIZATION)
         && context?.state?.let { it?.value.isNullOrEmpty() } ?: false

    override fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        !(isRequired(grantType, endpoint, context) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class Code(override val value: String): OAuth2ResponseParameter {

    companion object {
        const val NAME = "code"
    }

    override val name
        get() = NAME

    constructor(authorizationCode: AuthorizationCode): this(authorizationCode.code)

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        grantType == OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint == OAuth2Endpoint.AUTHORIZATION

    override fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        !(isRequired(grantType, endpoint, context) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

interface OAuth2ErrorResponseParameter: OAuth2ResponseParameter

data class Error(override val value: String?): OAuth2ErrorResponseParameter {

    companion object {
        const val NAME = "error"
    }

    override val name: String
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) = true

    override fun validate(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) =
        !(isRequired(grantType, endpoint, context) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class ErrorDescription(override val value: String?): OAuth2ErrorResponseParameter {

    companion object {
        const val NAME = "error_description"
    }

    override val name
        get() = NAME
}

data class ErrorUri(override val value: String?): OAuth2ErrorResponseParameter {

    companion object {
        const val NAME = "error_uri"
    }

    override val name
        get() = NAME
}