package oauth2.grantflow.clientcredentials

import oauth2.grantflow.OAuth2Grant
import oauth2.request.*
import oauth2.response.*
import oauth2.response.Scope
import oauth2.token.InMemoryTokenManager
import oauth2.token.OAuth2TokenManager

typealias TokenRequest = OAuth2ClientCredentialsGrantRequest.TokenRequest
typealias TokenResponse = OAuth2ClientCredentialsGrantResponse.TokenResponse

object OAuth2ClientCredentialsGrant:
    OAuth2Grant<OAuth2ClientCredentialsGrantRequest, OAuth2ClientCredentialsGrantResponse> {

    // FIXME: DI
    private val tokenManager: OAuth2TokenManager = InMemoryTokenManager

    override fun flow(request: OAuth2ClientCredentialsGrantRequest): OAuth2ClientCredentialsGrantResponse {
        return when (request) {
            is TokenRequest -> this.handleTokenRequest(request)
        }
    }

    private fun handleTokenRequest(request: TokenRequest): TokenResponse {

        // validate request parameters
        request.validateParams()

        // generate tokens
        val accessToken = tokenManager.generateAccessToken()
        val refreshToken = tokenManager.generateRefreshToken()

        // create new Token Response and return it
        return TokenResponse(
            context = null,
            accessToken = AccessToken(accessToken),
            tokenType = TokenType(OAuth2TokenType.BEARER.type),
            expiresIn = ExpiresIn(time = accessToken.expiresIn),
            refreshToken = RefreshToken(refreshToken),
            scope = Scope(request.scope)
        )
    }
}