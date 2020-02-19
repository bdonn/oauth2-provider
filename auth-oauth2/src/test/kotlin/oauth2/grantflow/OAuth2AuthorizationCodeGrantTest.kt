package oauth2.grantflow.authorizationcode

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.koin.KoinListener
import io.kotlintest.shouldThrow
import io.kotlintest.specs.FunSpec
import oauth2.client.*
import oauth2.code.InMemoryAuthorizationCodeManager
import oauth2.code.OAuth2AuthorizationCodeManager
import oauth2.context.InMemoryContextManager
import oauth2.context.OAuth2ContextManager
import oauth2.exception.*
import oauth2.request.ClientId
import oauth2.request.OAuth2GrantType
import oauth2.request.OAuth2ResponseType
import oauth2.request.ResponseType
import oauth2.token.InMemoryTokenManager
import oauth2.token.OAuth2TokenManager
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import utils.string.RandomStringGenerator
import utils.string.RandomStringGeneratorImpl

class OAuth2AuthorizationCodeGrantTest: FunSpec(), KoinTest {

    private val testModule = module {
        single<OAuth2ClientRegistrationManager> { ClientRegistrationManager }
        single<OAuth2ClientAuthenticationManager> { ClientAuthenticationManager }
        single<OAuth2AuthorizationCodeManager> { InMemoryAuthorizationCodeManager }
        single<OAuth2TokenManager> { InMemoryTokenManager }
        single<OAuth2ContextManager> { InMemoryContextManager }
        single<RandomStringGenerator> { RandomStringGeneratorImpl }

        single { OAuth2AuthorizationCodeGrant(get(), get(), get(), get(), get()) }
    }

    override fun listeners() = listOf(KoinListener(testModule))

    private val clientRegistrationManager by inject<OAuth2ClientRegistrationManager>()
    private val authorizationCodeGrant by inject<OAuth2AuthorizationCodeGrant>()

    init {
        test("""
            OAuth2 Authorization Code Grant Test - Authorization endpoint #1
            - request validation
        """) {
            // [GIVEN]
            val request = AuthorizationRequest(
                responseType = ResponseType(OAuth2ResponseType.CODE),
                clientId = ClientId("")
            )

            // [THEN]
            shouldThrow<OAuth2InvalidRequestParameterException> {
                // [WHEN]
                authorizationCodeGrant.flow(request)
            }
        }

        test("""
            OAuth2 Authorization Code Grant Test - Authorization endpoint #2
            - client not registered
        """) {
            // [GIVEN]
            val request = AuthorizationRequest(
                responseType = ResponseType(OAuth2ResponseType.CODE),
                clientId = ClientId("abc")
            )

            // [THEN]
            shouldThrow<OAuth2ClientNotRegisteredException> {
                // [WHEN]
                authorizationCodeGrant.flow(request)
            }
        }

        test("""
            OAuth2 Authorization Code Grant Test - Authorization endpoint #3
            - 등록된 client의 response type에 없음
        """) {
            // [GIVEN]
            val client = clientRegistrationManager.registerClient(
                OAuth2ClientRegistrationRequest(
                    redirectUris = setOf("http://client.example.com"),
                    grantTypes = setOf(OAuth2GrantType.AUTHORIZATION_CODE_GRANT),
                    responseTypes = setOf(OAuth2ResponseType.TOKEN)
                )
            )


            val request = AuthorizationRequest(
                responseType = ResponseType(OAuth2ResponseType.CODE),
                clientId = ClientId(client.clientId)
            )

            // [THEN]
            shouldThrow<OAuth2ClientResponseTypeNotMatchedException> {
                // [WHEN]
                authorizationCodeGrant.flow(request)
            }
        }

        test("""
            OAuth2 Authorization Code Grant Test - Authorization endpoint #4
            - normal case
        """) {
            // [GIVEN]
            val client = clientRegistrationManager.registerClient(
                OAuth2ClientRegistrationRequest(
                    redirectUris = setOf("http://client.example.com"),
                    grantTypes = setOf(OAuth2GrantType.AUTHORIZATION_CODE_GRANT),
                    responseTypes = setOf(OAuth2ResponseType.CODE)
                )
            )


            val request = AuthorizationRequest(
                responseType = ResponseType(OAuth2ResponseType.CODE),
                clientId = ClientId(client.clientId)
            )

            // [WHEN]
            val response = authorizationCodeGrant.flow(request)

            // [THEN]
            response.validateParams()
        }
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        clientRegistrationManager.clear()
    }
}