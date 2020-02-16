package oauth2.client

import io.kotlintest.koin.KoinListener
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.FunSpec
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject

class OAuth2ClientAuthenticationTest: FunSpec(), KoinTest {

    private val testModule = module {
        single<OAuth2ClientAuthenticationManager> { ClientAuthenticationManager }
    }

    override fun listeners() = listOf(KoinListener(testModule))

    private val clientAuthenticationManager by inject<OAuth2ClientAuthenticationManager>()

    init {
        test("""
        OAuth2 Client Authentication Test - authenticate #1
        - public client
        - no client secret
        - no redirect uri
    """) {
            // [GIVEN]
            // TODO: common provider
            val client = OAuth2Client(
                clientId = "",
                clientSecret = null,
                props = OAuth2ClientProps(
                    redirectUris = setOf(),
                    tokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.NONE,
                    grantTypes = setOf(),
                    responseTypes = setOf(),
                    clientName = "",
                    clientUri = "",
                    logoUri = "",
                    scope = "",
                    contacts = null,
                    tosUri = null,
                    policyUri = null,
                    jwksUri = null,
                    jwks = null,
                    softwareId = "",
                    softwareVersion = null,
                    profiles = setOf()
                )
            )

            // [WHEN]
            val result = clientAuthenticationManager.authenticate(client = client, clientSecret = null, redirectUri = null)

            // [THEN]
            result.shouldBeTrue()
        }

        test("""
            OAuth2 Client Authentication Test - authenticate #2
            - confidential client
            - no client secret
            - no redirect uri
        """) {
            // [GIVEN]
            // TODO: common provider
            val client = OAuth2Client(
                clientId = "",
                clientSecret = null,
                props = OAuth2ClientProps(
                    redirectUris = setOf(),
                    tokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.CLIENT_SECRET_BASIC,
                    grantTypes = setOf(),
                    responseTypes = setOf(),
                    clientName = "",
                    clientUri = "",
                    logoUri = "",
                    scope = "",
                    contacts = null,
                    tosUri = null,
                    policyUri = null,
                    jwksUri = null,
                    jwks = null,
                    softwareId = "",
                    softwareVersion = null,
                    profiles = setOf()
                )
            )

            // [WHEN]
            val result = clientAuthenticationManager.authenticate(client = client, clientSecret = null, redirectUri = null)

            // [THEN]
            result.shouldBeFalse()
        }

        test("""
            OAuth2 Client Authentication Test - authenticate #3
            - confidential client
            - no client secret
            - redirect uri contained
        """) {
            // [GIVEN]
            // TODO: common provider
            val client = OAuth2Client(
                clientId = "",
                clientSecret = null,
                props = OAuth2ClientProps(
                    redirectUris = setOf("http://client.example.com"),
                    tokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.CLIENT_SECRET_BASIC,
                    grantTypes = setOf(),
                    responseTypes = setOf(),
                    clientName = "",
                    clientUri = "",
                    logoUri = "",
                    scope = "",
                    contacts = null,
                    tosUri = null,
                    policyUri = null,
                    jwksUri = null,
                    jwks = null,
                    softwareId = "",
                    softwareVersion = null,
                    profiles = setOf()
                )
            )

            // [WHEN]
            val result = clientAuthenticationManager.authenticate(client = client, clientSecret = null, redirectUri = "http://client.example.com")

            // [THEN]
            result.shouldBeFalse()
        }

        test("""
            OAuth2 Client Authentication Test - authenticate #4
            - confidential client
            - wrong client secret
            - redirect uri contained
        """) {
            // [GIVEN]
            // TODO: common provider
            val client = OAuth2Client(
                clientId = "",
                clientSecret = "abc123!",
                props = OAuth2ClientProps(
                    redirectUris = setOf("http://client.example.com"),
                    tokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.CLIENT_SECRET_BASIC,
                    grantTypes = setOf(),
                    responseTypes = setOf(),
                    clientName = "",
                    clientUri = "",
                    logoUri = "",
                    scope = "",
                    contacts = null,
                    tosUri = null,
                    policyUri = null,
                    jwksUri = null,
                    jwks = null,
                    softwareId = "",
                    softwareVersion = null,
                    profiles = setOf()
                )
            )

            // [WHEN]
            val result = clientAuthenticationManager.authenticate(client = client, clientSecret = "123abc!", redirectUri = "http://client.example.com")

            // [THEN]
            result.shouldBeFalse()
        }

        test("""
            OAuth2 Client Authentication Test - authenticate #5
            - confidential client
            - correct client secret
            - redirect uri contained
        """) {
            // [GIVEN]
            // TODO: common provider
            val client = OAuth2Client(
                clientId = "",
                clientSecret = "abc123!",
                props = OAuth2ClientProps(
                    redirectUris = setOf("http://client.example.com"),
                    tokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.CLIENT_SECRET_BASIC,
                    grantTypes = setOf(),
                    responseTypes = setOf(),
                    clientName = "",
                    clientUri = "",
                    logoUri = "",
                    scope = "",
                    contacts = null,
                    tosUri = null,
                    policyUri = null,
                    jwksUri = null,
                    jwks = null,
                    softwareId = "",
                    softwareVersion = null,
                    profiles = setOf()
                )
            )

            // [WHEN]
            val result = clientAuthenticationManager.authenticate(client = client, clientSecret = "abc123!", redirectUri = "http://client.example.com")

            // [THEN]
            result.shouldBeTrue()
        }

        test("""
            OAuth2 Client Authentication Test - authenticate #6
            - confidential client
            - correct client secret
            - redirect uri not contained
        """) {
            // [GIVEN]
            // TODO: common provider
            val client = OAuth2Client(
                clientId = "",
                clientSecret = "abc123!",
                props = OAuth2ClientProps(
                    redirectUris = setOf("http://client.example1.com"),
                    tokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.CLIENT_SECRET_BASIC,
                    grantTypes = setOf(),
                    responseTypes = setOf(),
                    clientName = "",
                    clientUri = "",
                    logoUri = "",
                    scope = "",
                    contacts = null,
                    tosUri = null,
                    policyUri = null,
                    jwksUri = null,
                    jwks = null,
                    softwareId = "",
                    softwareVersion = null,
                    profiles = setOf()
                )
            )

            // [WHEN]
            val result = clientAuthenticationManager.authenticate(client = client, clientSecret = "abc123!", redirectUri = "http://client.example2.com")

            // [THEN]
            result.shouldBeFalse()
        }
    }
}