package oauth2.token



data class OAuth2AccessToken(val token: String, val expiresIn: Int)
data class OAuth2RefreshToken(val token: String)

interface OAuth2TokenManager {
    fun generateAccessToken(): OAuth2AccessToken
    fun generateRefreshToken(): OAuth2RefreshToken
}

// TODO: tokenGenerator - use delegation(by)

// TODO: JwtTokenGenerator

// TODO: JOSE 비대칭키 암호화 - public key open(endppoint)
// key management

object InMemoryTokenManager : OAuth2TokenManager {
    override fun generateAccessToken(): OAuth2AccessToken {
        // TODO
        return OAuth2AccessToken(token = "TMP_TOKEN_ACCESS" , expiresIn = 0)
    }

    override fun generateRefreshToken(): OAuth2RefreshToken {
        // TODO
        return OAuth2RefreshToken(token = "TMP_TOKEN_REFRESH")
    }
}