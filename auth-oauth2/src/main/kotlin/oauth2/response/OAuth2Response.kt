package oauth2.response

import oauth2.context.OAuth2Context
import oauth2.exception.OAuth2InvalidResponseParameterException
import oauth2.request.OAuth2Endpoint
import oauth2.request.OAuth2GrantType

sealed class OAuth2Response(open val context: OAuth2Context?) {

    abstract val params: List<OAuth2ResponseParameter?>
    abstract fun validateParams()

    protected fun validateParams(grantType: OAuth2GrantType, endpoint: OAuth2Endpoint, context: OAuth2Context?) {
        params.forEach {
            if (it != null && !it.validate(
                    grantType = grantType,
                    endpoint = endpoint,
                    context = context
                )
            ) {
                throw OAuth2InvalidResponseParameterException(param = it)
            }
        }
    }

    data class ErrorResponse(
        override val context: OAuth2Context?,
        val error: Error, // REQUIRED
        val errorDescription: ErrorDescription, // OPTIONAL
        val errorUri: ErrorUri, // OPTIONAL
        val state: State // REQUIRED(if the state parameter was present in the client authorization request)
    ) : OAuth2Response(context = context) {
        override val params = listOf(error, errorDescription, errorUri, state)

        override fun validateParams() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }
}

sealed class OAuth2AuthorizationCodeGrantResponse(override val context: OAuth2Context?) : OAuth2Response(context = context) {
    data class AuthorizationResponse(
        override val context: OAuth2Context?,
        val code: Code, // REQUIRED
        val state: State // REQUIRED(if the state parameter was present in the client authorization request)
    ) : OAuth2AuthorizationCodeGrantResponse(context = context) {
        override val params = listOf(code, state)

        override fun validateParams() {
            // common validation
            super.validateParams(
                grantType = OAuth2GrantType.AUTHORIZATION_CODE_GRANT,
                endpoint = OAuth2Endpoint.AUTHORIZATION,
                context = context
            )

            // authorization endpoint specific validation
        }
    }

    data class TokenResponse(
        override val context: OAuth2Context?,
        val accessToken: AccessToken, // REQUIRED
        val tokenType: TokenType, // REQUIRED
        val expiresIn: ExpiresIn?, // RECOMMENDED
        val refreshToken: RefreshToken?, // OPTIONAL
        val scope: Scope? // OPTIONAL(if identical to the scope requested by the client), otherwise REQUIRED
    ) : OAuth2AuthorizationCodeGrantResponse(context = context) {
        override val params = listOf(accessToken, tokenType, expiresIn, refreshToken, scope)

        override fun validateParams() {
            // common validation
            super.validateParams(
                grantType = OAuth2GrantType.AUTHORIZATION_CODE_GRANT,
                endpoint = OAuth2Endpoint.TOKEN,
                context = context
            )

            // authorization endpoint specific validation
        }
    }
}

sealed class OAuth2ImplicitGrantResponse(override val context: OAuth2Context?) : OAuth2Response(context = context) {
    data class TokenResponse(
        override val context: OAuth2Context?,
        val accessToken: AccessToken, // REQUIRED
        val tokenType: TokenType, // REQUIRED
        val expiresIn: ExpiresIn?, // RECOMMENDED
        val scope: Scope?, // OPTIONAL
        val state: State // REQUIRED (if the "state" parameter was present in the client authorization request. The exact value received from the client.)
    ) : OAuth2ImplicitGrantResponse(context = context) {
        override val params = listOf(accessToken, tokenType, expiresIn, scope, state)

        override fun validateParams() {
            // common validation
            super.validateParams(
                grantType = OAuth2GrantType.IMPLICIT_GRANT,
                endpoint = OAuth2Endpoint.AUTHORIZATION,
                context = context
            )

            // authorization endpoint specific validation
        }
    }
}

sealed class OAuth2ResourceOwnerPasswordCredentialsGrantResponse(override val context: OAuth2Context?) : OAuth2Response(context = context) {
    data class TokenResponse(
        override val context: OAuth2Context?,
        val accessToken: AccessToken, // REQUIRED
        val tokenType: TokenType, // REQUIRED
        val expiresIn: ExpiresIn?, // RECOMMENDED
        val refreshToken: RefreshToken?, // OPTIONAL
        val scope: Scope? // OPTIONAL(if identical to the scope requested by the client), otherwise REQUIRED
    ) : OAuth2ResourceOwnerPasswordCredentialsGrantResponse(context = context) {
        override val params = listOf(accessToken)

        override fun validateParams() {
            // common validation
            super.validateParams(
                grantType = OAuth2GrantType.RESOURCE_OWNER_CREDENTIALS_GRANT,
                endpoint = OAuth2Endpoint.AUTHORIZATION,
                context = context
            )

            // token endpoint specific validation
        }
    }
}

sealed class OAuth2ClientCredentialsGrantResponse(override val context: OAuth2Context?) : OAuth2Response(context = context) {
    data class TokenResponse(
        override val context: OAuth2Context?,
        val accessToken: AccessToken, // REQUIRED
        val tokenType: TokenType, // REQUIRED
        val expiresIn: ExpiresIn?, // RECOMMENDED
        val refreshToken: RefreshToken?, // OPTIONAL
        val scope: Scope? // OPTIONAL(if identical to the scope requested by the client), otherwise REQUIRED
    ) : OAuth2ClientCredentialsGrantResponse(context = context) {
        override val params = listOf(accessToken)

        override fun validateParams() {
            // common validation
            super.validateParams(
                grantType = OAuth2GrantType.CLIENT_CREDENTIALS_GRANT,
                endpoint = OAuth2Endpoint.AUTHORIZATION,
                context = context
            )

            // token endpoint specific validation
        }
    }
}