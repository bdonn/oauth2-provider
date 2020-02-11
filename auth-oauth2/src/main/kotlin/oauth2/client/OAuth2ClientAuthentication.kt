package oauth2.client

// TODO: client authentication

interface OAuth2ClientAuthenticationManager {
    fun authenticate(client: OAuth2Client, clientSecret: String?, redirectUri: String?): Boolean
}

object ClientAuthenticationManager: OAuth2ClientAuthenticationManager {
    override fun authenticate(client: OAuth2Client, clientSecret: String?, redirectUri: String?): Boolean {
        // FIXME: SHA256 hash
        return client.type == OAuth2ClientType.PUBLIC
                || client.clientSecret == clientSecret && client.redirectUri == redirectUri
    }
}