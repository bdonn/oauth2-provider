package oauth2.client

import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.specs.FunSpec

class OAuth2ClientTest : FunSpec({
    test("""
        OAuth2 Client Test - isConfidential #1
        - tokenEndpointAuthMethod is NONE
    """) {
        // [GIVEN]
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
        val isConfidential = client.isConfidential()

        // [THEN]
        isConfidential.shouldBeFalse()
    }

    test("""
        OAuth2 Client Test - isConfidential #1
        - tokenEndpointAuthMethod is CLIENT_SECRET_POST
    """) {
        // [GIVEN]
        val client = OAuth2Client(
            clientId = "",
            clientSecret = null,
            props = OAuth2ClientProps(
                redirectUris = setOf(),
                tokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.CLIENT_SECRET_POST,
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
        val isConfidential = client.isConfidential()

        // [THEN]
        isConfidential.shouldBeTrue()
    }

    test("""
        OAuth2 Client Test - isConfidential #1
        - tokenEndpointAuthMethod is CLIENT_SECRET_BASIC
    """) {
        // [GIVEN]
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
        val isConfidential = client.isConfidential()

        // [THEN]
        isConfidential.shouldBeTrue()
    }
})