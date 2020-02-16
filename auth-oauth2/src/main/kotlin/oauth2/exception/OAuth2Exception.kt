package oauth2.exception

import oauth2.client.OAuth2Client
import oauth2.request.OAuth2RequestParameter
import oauth2.response.OAuth2ResponseParameter

open class OAuth2Exception: Exception {
    constructor(description: String?): super(description)
}

/**
 * OAuth2.0 Request Exceptions
 */
open class OAuth2RequestException: OAuth2Exception {
    constructor(param: OAuth2RequestParameter): super(description = "${param.name}: ${param.value}")
}

open class OAuth2InvalidRequestParameterException(param: OAuth2RequestParameter): OAuth2RequestException(param = param)


/**
 * OAuth2.0 Response Exceptions
 */
open class OAuth2ResponseException: OAuth2Exception {
    constructor(param: OAuth2ResponseParameter): super(description = "${param.name}: ${param.value}")
}

open class OAuth2InvalidResponseParameterException(param: OAuth2ResponseParameter): OAuth2ResponseException(param = param)


/**
 * OAuth2.0 Context Exceptions
 */
open class OAuth2ContextException: OAuth2Exception {
    constructor(): super(description = null)
}

open class OAuth2ContextNotSetupException: OAuth2ContextException {
    constructor(): super()
}

/**
 * OAuth2.0 Client Exceptions
 */
open class OAuth2ClientException: OAuth2Exception {
    constructor(client: OAuth2Client): super(description = "${client.clientId}")

    constructor(description: String): super(description = description)
}

open class OAuth2ClientPropDuplicateException: OAuth2ClientException {
    constructor(vararg props: Pair<String, Any>): super(description = props.joinToString { "${it.first}: ${it.second}" })
}

open class OAuth2ClientNotRegisteredException: OAuth2ClientException {
    constructor(clientId: String): super(description = "ClientId: $clientId")
}

open class OAuth2ClientAuthenticationFailedException: OAuth2ClientException {
    constructor(clientId: String): super(description = "ClientId: $clientId")
}

open class OAuth2InvalidClientPropException: OAuth2ClientException {
    constructor(propName: String, propValue: Any): super(description = "$propName: $propValue")
}

/**
 * OAuth2.0 Authorization Exceptions
 */
open class OAuth2AuthorizationException: OAuth2Exception {
    constructor(description: String): super(description = description)
}
open class OAuth2AuthorizationFailedException: OAuth2AuthorizationException {
    constructor(code: String): super(description = "$code")
}