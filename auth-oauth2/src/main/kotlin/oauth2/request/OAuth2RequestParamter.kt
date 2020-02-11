package oauth2.request

// TODO: move
interface OAuth2Parameter

// TODO: move
enum class OAuth2Endpoint(val path: String) {
    AUTHORIZATION("/authorize"),
    TOKEN("/token")
}

// TODO: move
enum class OAuth2GrantType(val type: String?) {
    AUTHORIZATION_CODE_GRANT("code"),
    IMPLICIT_GRANT(null),
    RESOURCE_OWNER_CREDENTIALS_GRANT("password"),
    CLIENT_CREDENTIALS_GRANT("client_credentials"),
}

// TODO: move
enum class OAuth2ResponseType(val type: String) {
    CODE("code"),
    TOKEN("token")
}

interface OAuth2RequestParameter : OAuth2Parameter {
    val value: String?
    val name: String
    fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint): Boolean = false // default not required
    fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint): Boolean = true // default validation ok
}

data class ClientId(override val value: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "client_id"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        grantType === OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint === OAuth2Endpoint.AUTHORIZATION
            || grantType === OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint === OAuth2Endpoint.TOKEN
            || grantType === OAuth2GrantType.IMPLICIT_GRANT && endpoint === OAuth2Endpoint.AUTHORIZATION
            || grantType === OAuth2GrantType.RESOURCE_OWNER_CREDENTIALS_GRANT && endpoint === OAuth2Endpoint.AUTHORIZATION
            || grantType === OAuth2GrantType.CLIENT_CREDENTIALS_GRANT && endpoint === OAuth2Endpoint.AUTHORIZATION
            || grantType === OAuth2GrantType.CLIENT_CREDENTIALS_GRANT && endpoint === OAuth2Endpoint.TOKEN

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
            !(isRequired(grantType, endpoint) && value.isNullOrEmpty())
                && (request.params.find { print(it); it?.name == ClientCredential.NAME } as ClientCredential?)?.clientId == value
}

data class RedirectUri(override val value: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "redirect_uri"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
                grantType === OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint === OAuth2Endpoint.TOKEN

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        (!isRequired(grantType, endpoint) || !value.isNullOrEmpty())
         // TODO: registered redirectUri
}

data class ResponseType(override val value: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "response_type"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
                grantType === OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint === OAuth2Endpoint.AUTHORIZATION
                || grantType === OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint === OAuth2Endpoint.TOKEN
                || grantType === OAuth2GrantType.IMPLICIT_GRANT && endpoint === OAuth2Endpoint.AUTHORIZATION
                || grantType === OAuth2GrantType.RESOURCE_OWNER_CREDENTIALS_GRANT && endpoint === OAuth2Endpoint.AUTHORIZATION

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        !(isRequired(grantType, endpoint) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class Scope(override val value: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "scope"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) = false

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        !(isRequired(grantType, endpoint) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class State(override val value: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "state"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) = false

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        !(isRequired(grantType, endpoint) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class GrantType(override val value: String?) : OAuth2RequestParameter {

    constructor(grantType: OAuth2GrantType): this(value = grantType.type)

    companion object {
        const val NAME = "grant_type"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        grantType === OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint === OAuth2Endpoint.TOKEN

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        !(isRequired(grantType, endpoint) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class Code(override val value: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "code"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        grantType === OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint === OAuth2Endpoint.TOKEN

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        !(isRequired(grantType, endpoint) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class Username(override val value: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "username"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        grantType === OAuth2GrantType.RESOURCE_OWNER_CREDENTIALS_GRANT && endpoint === OAuth2Endpoint.TOKEN

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        !(isRequired(grantType, endpoint) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class Password(override val value: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "password"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        grantType === OAuth2GrantType.RESOURCE_OWNER_CREDENTIALS_GRANT && endpoint === OAuth2Endpoint.TOKEN

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        !(isRequired(grantType, endpoint) && value.isNullOrEmpty()) // && checkFormat, checkRange
}

data class ClientCredential(override val value: String?, val clientId: String?, val clientSecret: String?) : OAuth2RequestParameter {

    companion object {
        const val NAME = "Authorization"
    }

    override val name
        get() = NAME

    override fun isRequired(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        grantType === OAuth2GrantType.AUTHORIZATION_CODE_GRANT && endpoint === OAuth2Endpoint.AUTHORIZATION

    override fun validate(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) =
        !(isRequired(grantType, endpoint) && value.isNullOrEmpty()) // && checkFormat, checkRange
}