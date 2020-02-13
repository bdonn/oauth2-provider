package oauth2.grantflow.resourceowner

import oauth2.grantflow.OAuth2Grant
import oauth2.request.*
import oauth2.response.*
import oauth2.token.InMemoryTokenManager
import oauth2.token.OAuth2TokenManager

typealias TokenRequest = OAuth2ResourceOwnerPasswordCredentialsGrantRequest.TokenRequest
typealias TokenResponse = OAuth2ResourceOwnerPasswordCredentialsGrantResponse.TokenResponse

object OAuth2ResourceOwnerPasswordCredentialsGrant:
    OAuth2Grant<OAuth2ResourceOwnerPasswordCredentialsGrantRequest, OAuth2ResourceOwnerPasswordCredentialsGrantResponse> {

    // FIXME: DI
    private val tokenManager: OAuth2TokenManager = InMemoryTokenManager

    override fun flow(request: OAuth2ResourceOwnerPasswordCredentialsGrantRequest): OAuth2ResourceOwnerPasswordCredentialsGrantResponse {
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
            tokenType = TokenType(OAuth2TokenType.BEARER),
            expiresIn = ExpiresIn(time = accessToken.expiresIn),
            scope = null,
            refreshToken = RefreshToken(refreshToken)
        )
    }
}