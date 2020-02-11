package oauth2.grantflow

import oauth2.grantflow.resourceowner.OAuth2ResourceOwnerPasswordCredentialsGrantFlow
import oauth2.request.*
import oauth2.response.OAuth2ResourceOwnerPasswordCredentialsGrantResponse
import org.junit.Assert.*
import org.junit.Test

class OAuth2ResourceOwnerPasswordCredentialsGrantFlowTest {

    @Test
    fun `Resource Owner Password Credentials grant flow - Token endpoint 테스트`() {

        /**
         * [Given]
         */
        // request
        val request = OAuth2ResourceOwnerPasswordCredentialsGrantRequest.TokenRequest(
            grantType = GrantType(OAuth2GrantType.RESOURCE_OWNER_CREDENTIALS_GRANT),
            username = Username("test_id"),
            password = Password("test_pw"),
            scope = null
        )

        /**
         * [When]
         */
        // grant flow
        val response = OAuth2ResourceOwnerPasswordCredentialsGrantFlow.flow(
            request = request
        )

        /**
         * [Then]
         */
        // Token Response returned
        assertTrue(response is OAuth2ResourceOwnerPasswordCredentialsGrantResponse.TokenResponse)

        // validation ok
        response.validateParams()
    }
}