package oauth2.client

import oauth2.exception.OAuth2ClientAlreadyRegisteredException

// TODO: client registration(dynamic) - ODCR
// id, secret, redirectUri, scope(resource)

interface OAuth2ClientRegistrationManager {
    fun registerClient(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        type: OAuth2ClientType,
        profiles: List<OAuth2ClientProfile>
    ): OAuth2Client

    fun unregisterClient(client: OAuth2Client)

    fun retrieveClient(clientId: String): OAuth2Client?

    fun clear()
}

object ClientRegistrationManager: OAuth2ClientRegistrationManager {

    private val clientRegistry: OAuth2ClientRegistry = InMemoryClientRegistry()

    override fun registerClient(
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        type: OAuth2ClientType,
        profiles: List<OAuth2ClientProfile>
    ): OAuth2Client {

        // check if client is registered
        if(clientRegistry.checkClientRegistered(clientId = clientId, redirectUri = redirectUri)) {
            // throw exception if already registered
            throw OAuth2ClientAlreadyRegisteredException(clientId = clientId)
        }

        val client = OAuth2Client(
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = redirectUri,
            type = type,
            profiles = profiles
        )

        // add client to registry
        clientRegistry.addClient(client)

        return client
    }

    override fun retrieveClient(clientId: String): OAuth2Client? = clientRegistry.findClient(clientId = clientId)

    override fun unregisterClient(client: OAuth2Client) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clear() {
        clientRegistry.clear()
    }
}

interface OAuth2ClientRegistry {

    fun addClient(client: OAuth2Client)

    fun removeClient(client: OAuth2Client)

    fun findClient(clientId: String): OAuth2Client?

    fun checkClientRegistered(clientId: String, redirectUri: String): Boolean

    fun clear()
}

class InMemoryClientRegistry: OAuth2ClientRegistry {

    private val map = mutableMapOf<String, OAuth2Client>()

    override fun addClient(client: OAuth2Client) { map += mapOf(client.clientId to client) }

    override fun findClient(clientId: String): OAuth2Client? = map[clientId]

    override fun removeClient(client: OAuth2Client) { map -= client.clientId }

    override fun checkClientRegistered(clientId: String, redirectUri: String) = clientId in map || map.any { it.value.redirectUri == redirectUri }

    override fun clear() {
        map.clear()
    }
}