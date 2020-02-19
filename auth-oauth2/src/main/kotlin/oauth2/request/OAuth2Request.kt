package oauth2.request

import oauth2.exception.OAuth2InvalidRequestParameterException

sealed class OAuth2Request {

    abstract fun validate()
    abstract val params: List<OAuth2RequestParameter?>

    protected fun validateParams(request: OAuth2Request, grantType: OAuth2GrantType, endpoint: OAuth2Endpoint) {
        params.forEach {
            if (it != null && !it.validate(request = request, grantType = grantType, endpoint = endpoint)) {
                throw OAuth2InvalidRequestParameterException(param = it)
            }
        }
    }
}

sealed class OAuth2AuthorizationCodeGrantRequest : OAuth2Request() {
    data class AuthorizationRequest(
        val responseType: ResponseType, // REQUIRED
        val clientId: ClientId, // REQUIRED
        val redirectUri: RedirectUri? = null, // OPTIONAL
        val scope: Scope? = null, // OPTIONAL
        val state: State? = null// RECOMMENDED
    ) : OAuth2AuthorizationCodeGrantRequest() {
        override val params = listOf(responseType, scope, clientId, redirectUri, state)

        override fun validate() {
            // common validation
            super.validateParams(
                request = this,
                grantType = OAuth2GrantType.AUTHORIZATION_CODE_GRANT,
                endpoint = OAuth2Endpoint.AUTHORIZATION
            )

            // specific validation
        }
    }

    data class TokenRequest(
        val grantType: GrantType, // REQUIRED
        val code: Code, // REQUIRED
        val redirectUri: RedirectUri, // REQUIRED
        val clientId: ClientId, // REQUIRED
        val clientCredential: ClientCredential // REQUIRED
    ) : OAuth2AuthorizationCodeGrantRequest() {
        override val params = listOf(grantType, code, redirectUri, clientId, clientCredential)

        override fun validate() {
            // common validation
            super.validateParams(
                request = this,
                grantType = OAuth2GrantType.AUTHORIZATION_CODE_GRANT,
                endpoint = OAuth2Endpoint.TOKEN
            )

            // specific validation
        }
    }
}

sealed class OAuth2ImplicitGrantRequest : OAuth2Request() {
    data class AuthorizationRequest(
        val responseType: ResponseType, // REQUIRED
        val clientId: ClientId, // REQUIRED
        val redirectUri: RedirectUri?, // OPTIONAL
        val scope: Scope?, // OPTIONAL
        val state: State? // RECOMMENDED
    ) : OAuth2ImplicitGrantRequest() {
        override val params = listOf(responseType, clientId, redirectUri, scope, state)

        override fun validate() {
            // common validation
            super.validateParams(
                request = this,
                grantType = OAuth2GrantType.IMPLICIT_GRANT,
                endpoint = OAuth2Endpoint.AUTHORIZATION
            )

            // specific validation
        }
    }

    // ImplicitGrant - no token endpoint request.
}

sealed class OAuth2ResourceOwnerPasswordCredentialsGrantRequest : OAuth2Request() {
    // Resource Owner Password Credentials Grant - authorization endpoint request is not defined.

    data class TokenRequest(
        val grantType: GrantType, // REQUIRED
        val username: Username, // REQUIRED
        val password: Password, // REQUIRED
        val scope: Scope? // OPTIONAL
    ) : OAuth2ResourceOwnerPasswordCredentialsGrantRequest() {
        override val params = listOf(grantType, username, password, scope)

        override fun validate() {
            // common validation
            super.validateParams(
                request = this,
                grantType = OAuth2GrantType.RESOURCE_OWNER_CREDENTIALS_GRANT,
                endpoint = OAuth2Endpoint.TOKEN
            )

            // specific validation
        }
    }
}

sealed class OAuth2ClientCredentialsGrantRequest : OAuth2Request() {
    // Client Credentials Grant - no additional authorization request is needed.

    data class TokenRequest(
        val grantType: GrantType, // REQUIRED
        val scope: Scope? // OPTIONAL
    ) : OAuth2ClientCredentialsGrantRequest() {
        override val params = listOf(grantType, scope)

        override fun validate() {
            // common validation
            super.validateParams(
                request = this,
                grantType = OAuth2GrantType.CLIENT_CREDENTIALS_GRANT,
                endpoint = OAuth2Endpoint.TOKEN
            )

            // specific validation
        }
    }
}
