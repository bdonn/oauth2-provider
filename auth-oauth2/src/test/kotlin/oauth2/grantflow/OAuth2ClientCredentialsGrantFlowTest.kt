package oauth2.grantflow

import oauth2.grantflow.clientcredentials.OAuth2ClientCredentialsGrantFlow
import oauth2.request.GrantType
import oauth2.request.OAuth2ClientCredentialsGrantRequest
import oauth2.request.OAuth2GrantType
import oauth2.response.OAuth2ClientCredentialsGrantResponse
import org.junit.Assert.*
import org.junit.Test

class OAuth2ClientCredentialsGrantFlowTest {

    @Test
    fun `Client Credentials grant flow - Token endpoint 테스트`() {

        /**
         * [Given]
         */
        // request
        val request = OAuth2ClientCredentialsGrantRequest.TokenRequest(
            grantType = GrantType(OAuth2GrantType.CLIENT_CREDENTIALS_GRANT),
            scope = null
        )

        /**
         * [When]
         */
        // grant flow
        val response = OAuth2ClientCredentialsGrantFlow.flow(
            request = request
        )

        /**
         * [Then]
         */
        // Token Response returned
        assertTrue(response is OAuth2ClientCredentialsGrantResponse.TokenResponse)

        // validation ok
        response.validateParams()
    }


}