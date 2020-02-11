package oauth2.grantflow.authorizationcode

import oauth2.client.ClientAuthenticationManager
import oauth2.client.ClientRegistrationManager
import oauth2.client.OAuth2ClientAuthenticationManager
import oauth2.client.OAuth2ClientRegistrationManager
import oauth2.code.AuthorizationCode
import oauth2.code.OAuth2AuthorizationCodeManager
import oauth2.code.InMemoryAuthorizationCodeManager
import oauth2.context.OAuth2ContextManager
import oauth2.context.InMemoryContextManager
import oauth2.exception.OAuth2ClientAuthenticationFailedException
import oauth2.exception.OAuth2ClientNotRegisteredException
import oauth2.exception.OAuth2ContextNotSetupException
import oauth2.grantflow.OAuth2GrantFlow
import oauth2.request.*
import oauth2.response.*
import oauth2.response.Code
import oauth2.response.Scope
import oauth2.response.State
import oauth2.token.InMemoryTokenManager
import oauth2.token.OAuth2TokenManager

typealias AuthorizationRequest = OAuth2AuthorizationCodeGrantRequest.AuthorizationRequest
typealias TokenRequest = OAuth2AuthorizationCodeGrantRequest.TokenRequest
typealias AuthorizationResponse = OAuth2AuthorizationCodeGrantResponse.AuthorizationResponse
typealias TokenResponse = OAuth2AuthorizationCodeGrantResponse.TokenResponse

object OAuth2AuthorizationCodeGrantFlow :
    OAuth2GrantFlow<OAuth2AuthorizationCodeGrantRequest, OAuth2AuthorizationCodeGrantResponse> {

    // FIXME: DI
    private val codeManager: OAuth2AuthorizationCodeManager = InMemoryAuthorizationCodeManager
    private val tokenManager: OAuth2TokenManager = InMemoryTokenManager
    private val contextManager: OAuth2ContextManager = InMemoryContextManager
    private val clientRegistrationManager: OAuth2ClientRegistrationManager = ClientRegistrationManager
    private val clientAuthenticationManager: OAuth2ClientAuthenticationManager = ClientAuthenticationManager

    override fun flow(
        request: OAuth2AuthorizationCodeGrantRequest
    ): OAuth2AuthorizationCodeGrantResponse {
        return when (request) {
            is AuthorizationRequest -> this.processAuthorizationRequest(request)
            is TokenRequest -> this.processTokenRequest(request)
        }
    }

    private fun processAuthorizationRequest(
        request: AuthorizationRequest
    ): AuthorizationResponse {

        // validate request parameters
        request.validateParams()

        // TODO: check if resource owner is authenticated

        // TODO: check if resource owner's approval is obtained

        // generate authorization code
        val code = codeManager.issueAuthorizationCode(request)

        // save context
        val context = contextManager.saveContext(request, property = Pair(AuthorizationCode.NAME, code))

        // create new Authorization Response and return it
        return AuthorizationResponse(
            context = context,
            code = Code(code),
            state = State(request.state)
        )
    }

    private fun processTokenRequest(request: TokenRequest): TokenResponse {

        // validate request parameters
        request.validateParams()

        // retrieve context
        val context = contextManager.retrieveContext(request)
        context ?: throw OAuth2ContextNotSetupException()

        // extract client information from request
        val clientId = request.clientId.value!! // not null after validation
        val clientSecret = request.clientCredential.clientSecret
        val redirectUri = request.redirectUri?.value

        // retrieve client
        val client = clientRegistrationManager.retrieveClient(clientId = clientId)
        client ?: throw OAuth2ClientNotRegisteredException(clientId = clientId)

        // authenticate client
        val authenticated = clientAuthenticationManager.authenticate(
            client = client,
            clientSecret = clientSecret,
            redirectUri = redirectUri
        )
        if(!authenticated) {
            throw OAuth2ClientAuthenticationFailedException(clientId = clientId)
        }

        // consume authorization code
        codeManager.consumeAuthorizationCode(value = request.code.value!!) // not null after validation

        // generate tokens
        val accessToken = tokenManager.generateAccessToken()
        val refreshToken = tokenManager.generateRefreshToken()

        // create new Token Response and return it
        return TokenResponse(
            context = context,
            accessToken = AccessToken(accessToken.token),
            tokenType = TokenType(OAuth2TokenType.BEARER.type),
            expiresIn = ExpiresIn(time = accessToken.expiresIn),
            refreshToken = RefreshToken(refreshToken.token),
            scope = Scope(context.scope)
        )
    }

}

