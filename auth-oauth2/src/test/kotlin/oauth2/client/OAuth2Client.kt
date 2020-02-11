package oauth2.client

import oauth2.exception.OAuth2ClientAlreadyRegisteredException
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OAuth2ClientTest {

    @Before
    fun setUp() {

    }

    @After
    fun tearDown() {
        ClientRegistrationManager.clear()
    }

    @Test
    fun `테스트 OAuth2ClientRegistrationManager - registerClient`() {

        /**
         * [Given]
         */
        // client information
        val clientId = "client-0101"
        val clientSecret = "123123"
        val redirectUri = "auth.malka.today"


        /**
         * [When]
         */
        // register client
        val client = ClientRegistrationManager.registerClient(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            profiles = listOf(),
            type = OAuth2ClientType.CONFIDENTIAL
        )

        /**
         * [Then]
         */
        // new client with given information returned
        assertEquals(clientId, client.clientId)
        assertEquals(clientSecret, client.clientSecret)
        assertEquals(redirectUri, client.redirectUri)
        assertEquals(OAuth2ClientType.CONFIDENTIAL, client.type)
    }

    @Test(expected = OAuth2ClientAlreadyRegisteredException::class)
    fun `테스트 OAuth2ClientRegistrationManager - registerClient - duplicate`() {

        /**
         * [Given]
         */
        // client information
        val clientId = "client-0101"
        val clientSecret = "123123"
        val redirectUri = "auth.malka.today"

        // register client
        ClientRegistrationManager.registerClient(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            profiles = listOf(),
            type = OAuth2ClientType.CONFIDENTIAL
        )

        /**
         * [When]
         */
        // register client again
        ClientRegistrationManager.registerClient(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            profiles = listOf(),
            type = OAuth2ClientType.CONFIDENTIAL
        )


        /**
         * [Then]
         */
        // OAuth2ClientAlreadyRegisteredException thrown
    }
}