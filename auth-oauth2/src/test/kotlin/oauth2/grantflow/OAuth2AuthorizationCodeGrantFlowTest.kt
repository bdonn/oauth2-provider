package oauth2.grantflow

import oauth2.client.ClientRegistrationManager
import oauth2.client.OAuth2ClientType
import oauth2.context.InMemoryContextManager
import oauth2.exception.OAuth2ContextNotSetupException
import oauth2.grantflow.authorizationcode.OAuth2AuthorizationCodeGrantFlow
import oauth2.request.*
import oauth2.response.OAuth2AuthorizationCodeGrantResponse
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class OAuth2AuthorizationCodeGrantFlowTest {

    @Before
    fun setUp() {
//        DaggerAuthorizationCodeComponent.builder().build()
    }

    @Test
    fun `Authorization code grant flow - Authorization endpoint 테스트`() {

        /**
         * [Given]
         */
        // request
        val request = OAuth2AuthorizationCodeGrantRequest.AuthorizationRequest(
            responseType = ResponseType("code"),
            state = State("abc"),
            clientId = ClientId("client-1"),
            redirectUri = RedirectUri("https://www.naver.com"),
            scope = Scope("openid"),
            clientCredential = ClientCredential(value = "qqq", clientId = "client-1", clientSecret = "111")
        )

        // client registration
        ClientRegistrationManager.registerClient(
            clientId = "client-1",
            clientSecret = "111",
            type = OAuth2ClientType.CONFIDENTIAL,
            profiles = listOf(),
            redirectUri = "https://www.naver.com"
        )

        /**
         * [When]
         */
        // grant flow
        val response = OAuth2AuthorizationCodeGrantFlow.flow(
            request = request
        )

        /**
         * [Then]
         */
        // Authorization Response
        assertTrue(response is OAuth2AuthorizationCodeGrantResponse.AuthorizationResponse)

        // parameter validation ok
        response.validateParams()
    }

    @Test
    fun `Authorization code grant flow - Token endpoint 테스트`() {

        /**
         * [Given]
         */
        // request
        val request = OAuth2AuthorizationCodeGrantRequest.TokenRequest(
            grantType = GrantType(OAuth2GrantType.AUTHORIZATION_CODE_GRANT.type),
            clientId = ClientId("client-1"),
            redirectUri = RedirectUri("https://www.naver.com"),
            code = Code("123123")
        )

        // context is set
        InMemoryContextManager.saveContext(request)


        /**
         * [When]
         */
        // grant flow
        val response = OAuth2AuthorizationCodeGrantFlow.flow(
            request = request
        )

        /**
         * [Then]
         */
        // Token Response
        assertTrue(response is OAuth2AuthorizationCodeGrantResponse.TokenResponse)

        // parameter validation ok
        response.validateParams()
    }

    @Test(expected = OAuth2ContextNotSetupException::class)
    fun `Authorization code grant flow - Token endpoint 테스트 - no context`() {

        /**
         * [Given]
         */
        // request
        val request = OAuth2AuthorizationCodeGrantRequest.TokenRequest(
            grantType = GrantType(OAuth2GrantType.AUTHORIZATION_CODE_GRANT.type),
            clientId = ClientId("client-1"),
            redirectUri = RedirectUri("https://www.naver.com"),
            code = Code("123123")
        )

        /**
         * [When]
         */
        // grant flow
        OAuth2AuthorizationCodeGrantFlow.flow(
            request = request
        )

        /**
         * [Then]
         */
        // OAuth2ContextNotSetupException thrown
    }
}