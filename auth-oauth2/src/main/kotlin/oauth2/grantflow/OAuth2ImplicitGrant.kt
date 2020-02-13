package oauth2.grantflow.implicit

import oauth2.grantflow.OAuth2Grant
import oauth2.request.*
import oauth2.response.*
import oauth2.response.State
import oauth2.token.InMemoryTokenManager
import oauth2.token.OAuth2TokenManager

typealias AuthorizationRequest = OAuth2ImplicitGrantRequest.AuthorizationRequest
typealias TokenResponse = OAuth2ImplicitGrantResponse.TokenResponse


object OAuth2ImplicitGrant : OAuth2Grant<OAuth2ImplicitGrantRequest, OAuth2ImplicitGrantResponse> {

    // FIXME: DI
    private val tokenManager: OAuth2TokenManager = InMemoryTokenManager

    override fun flow(request: OAuth2ImplicitGrantRequest): OAuth2ImplicitGrantResponse {
        return when (request) {
            is AuthorizationRequest -> this.handleAuthorizationRequest(request)
        }
    }

    private fun handleAuthorizationRequest(request: AuthorizationRequest): TokenResponse {

        // validate request parameters
        request.validateParams()

        // generate access token
        val accessToken = tokenManager.generateAccessToken()

        // create new Token Response and return it
        return TokenResponse(
            context = null,
            accessToken = AccessToken(accessToken),
            tokenType = TokenType(OAuth2TokenType.BEARER.type),
            expiresIn = ExpiresIn(time = accessToken.expiresIn),
            state = State(state = request.state),
            scope = null
        )
    }
}