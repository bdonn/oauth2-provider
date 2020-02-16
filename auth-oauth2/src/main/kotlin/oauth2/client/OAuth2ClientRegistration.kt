package oauth2.client

import oauth2.exception.OAuth2ClientPropDuplicateException
import oauth2.exception.OAuth2InvalidClientPropException
import oauth2.request.OAuth2GrantType
import oauth2.request.OAuth2ResponseType
import org.koin.core.KoinComponent
import org.koin.core.inject
import utils.string.RandomStringGenerator
import utils.string.RandomStringSpec
import java.net.URI
import java.net.URISyntaxException

// TODO: scope 등록, 관리

/**
 * RFC 7591: OAuth2.0 Dynamic Client Registration
 */

data class OAuth2ClientRegistrationRequest(
    val redirectUris: Set<String>,
    val tokenEndpointAuthMethod: OAuth2ClientTokenEndpointAuthMethod = OAuth2ClientTokenEndpointAuthMethod.NONE,
    val grantTypes: Set<OAuth2GrantType>,
    val responseTypes: Set<OAuth2ResponseType>,
    val clientName: String?,
    val clientUri: String,
    val logoUri: String?,
    val scope: String?,
    val contacts: List<String>?,
    val tosUri: String?,
    val policyUri: String?,
    val jwksUri: String?,
    val jwks: List<String>?,
    val softwareId: String, // required?
    val softwareVersion: String?,
    val profiles: Set<OAuth2ClientProfile>
)

interface OAuth2ClientRegistrationManager {
    fun registerClient(request: OAuth2ClientRegistrationRequest): OAuth2Client

    fun unregisterClient(client: OAuth2Client)

    fun retrieveClient(clientId: String): OAuth2Client?

    fun clear()
}

internal object ClientRegistrationManager: OAuth2ClientRegistrationManager, KoinComponent {

    private val randomStringGenerator by inject<RandomStringGenerator>()

    override fun registerClient(request: OAuth2ClientRegistrationRequest): OAuth2Client {

        // validate request input
        this.validate(request)

        // generate new clientId
        val clientId = this.generateClientId()

        // generate new clientSecret
        val clientSecret = if(OAuth2Client.isConfidential(request.tokenEndpointAuthMethod)) this.generateClientSecret() else null

        // check redirect uris
        if(clientRegistry.checkRedirectUriDuplicate(redirectUris = request.redirectUris)) {
            throw OAuth2ClientPropDuplicateException(Pair("redirectUri", request.redirectUris)) // FIXME: prop name
        }

        // check client uri
        if(clientRegistry.checkClientUriDuplicate(clientUri = request.clientUri)) {
            throw OAuth2ClientPropDuplicateException(Pair("clientUri", request.clientUri)) // FIXME: prop name
        }

        val client = OAuth2Client(
            clientId = clientId,
            clientSecret = clientSecret,
            props = OAuth2ClientProps(request)
        )

        // add client to registry
        clientRegistry.addClient(client)

        return client
    }

    override fun retrieveClient(clientId: String): OAuth2Client? {
        return clientRegistry.findClient(clientId = clientId)
    }

    override fun unregisterClient(client: OAuth2Client) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun clear() {
        clientRegistry.clear()
    }

    private val clientRegistry: OAuth2ClientRegistry = InMemoryClientRegistry()

    private fun generateClientId(): String {
        // FIXME: config by env
        val clientIdSpec = RandomStringSpec(length = 15)

        return randomStringGenerator.generate(clientIdSpec)
    }

    private fun generateClientSecret(): String {
        // FIXME: config by env
        val clientSecretSpec = RandomStringSpec(length = 12)

        return randomStringGenerator.generate(clientSecretSpec)
    }

    private fun validate(request: OAuth2ClientRegistrationRequest) {

        // validate redirect uris
        for(redirectUri in request.redirectUris) {
            try {
                URI(redirectUri)
            } catch(e: URISyntaxException) {
                throw OAuth2InvalidClientPropException(propName = "redirectUris", propValue = redirectUri) // FIXME: prop name
            }
        }

        // validate client uri
        try {
            URI(request.clientUri)
        } catch(e: URISyntaxException) {
            throw OAuth2InvalidClientPropException(propName = "clientUri", propValue = request.clientUri) // FIXME: prop name
        }
    }
}

interface OAuth2ClientRegistry {

    fun addClient(client: OAuth2Client)

    fun removeClient(client: OAuth2Client)

    fun findClient(clientId: String): OAuth2Client?

    fun checkClientRegistered(clientId: String): Boolean

    fun checkRedirectUriDuplicate(redirectUri: String): Boolean

    fun checkRedirectUriDuplicate(redirectUris: Set<String>): Boolean

    fun checkClientUriDuplicate(clientUri: String): Boolean

    fun isEmpty(): Boolean

    fun clear()
}

class InMemoryClientRegistry: OAuth2ClientRegistry {

    private val map = mutableMapOf<String, OAuth2Client>()

    override fun addClient(client: OAuth2Client) { map += mapOf(client.clientId to client) }

    override fun findClient(clientId: String): OAuth2Client? = map[clientId]

    override fun removeClient(client: OAuth2Client) { map -= client.clientId }

    override fun checkClientRegistered(clientId: String) = clientId in map

    override fun checkRedirectUriDuplicate(redirectUri: String) = map.any { it.value.props?.redirectUris?.contains(redirectUri) ?: false }

    override fun checkRedirectUriDuplicate(redirectUris: Set<String>) = map.any { it.value.props?.redirectUris?.intersect(redirectUris)?.isNotEmpty() ?: false}

    override fun checkClientUriDuplicate(clientUri: String) = map.any { it.value.props?.clientUri == clientUri }

    override fun isEmpty(): Boolean = map.isEmpty()

    override fun clear() {
        map.clear()
    }
}