package oauth2.client

import io.kotlintest.*
import io.kotlintest.koin.KoinListener
import io.kotlintest.matchers.boolean.shouldBeFalse
import io.kotlintest.matchers.boolean.shouldBeTrue
import io.kotlintest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotlintest.matchers.string.shouldNotBeEmpty
import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.specs.FunSpec
import oauth2.exception.OAuth2ClientPropDuplicateException
import oauth2.exception.OAuth2InvalidClientPropException
import oauth2.request.OAuth2GrantType
import oauth2.request.OAuth2ResponseType
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import utils.string.RandomStringGenerator
import utils.string.RandomStringGeneratorImpl

val uriValid1 = "https://client.example1.com"
val uriValid2 = "https://client.example2.com"
val uriInvalid = "::"

val redirectUriValid1 = "$uriValid1/cb"
val redirectUriValid2 = "$uriValid2/cb"
val redirectUriInvalid = "$uriInvalid/cb"

val tokenEndpointAuthMethodNone = OAuth2ClientTokenEndpointAuthMethod.NONE // public client
val tokenEndpointAuthMethodBasic = OAuth2ClientTokenEndpointAuthMethod.CLIENT_SECRET_BASIC // confidential client
val tokenEndpointAuthMethodPost = OAuth2ClientTokenEndpointAuthMethod.CLIENT_SECRET_POST // confidential client

val clientName1 = "foo"
val clientName2 = "bar"
val clientName3 = "baz"

val logoUriValid1 = "$uriValid1/logo"
val logoUriValid2 = "$uriValid2/logo"
val logoUriInvalid = "$uriInvalid/logo"

val scopeEmpty = ""
val scopeOne = "s1"
val scopeTwo = "s1 s2"

val contact1 = ""
val contact2 = ""
val contact3 = ""

val tosUriValid1 = "$uriValid1/tos"
val tosUriValid2 = "$uriValid2/tos"
val tosUriInvalid = "$uriInvalid/tos"

val policyUriValid1 = "$uriValid1/policies"
val policyUriValid2 = "$uriValid2/policies"
val policyUriInvalid = "$uriInvalid/policies"

val jwkUriValid1 = "$uriValid1/jwk"
val jwkUriValid2 = "$uriValid2/jwk"
val jwkUriInvalid = "$uriInvalid/jwk"

val jwk1 = ""
val jwk2 = ""
val jwk3 = ""

val softwareId1 = ""
val softwareId2 = ""
val softwareId3 = ""

val softwareVersion1 = "1"
val softwareVersion2 = "2"
val softwareVersion3 = "3"

val profilesOne = setOf(OAuth2ClientProfile.WEB_APP)
val profilesTwo = setOf(OAuth2ClientProfile.WEB_APP , OAuth2ClientProfile.NATIVE_APP)
val profilesThree = setOf(OAuth2ClientProfile.WEB_APP , OAuth2ClientProfile.NATIVE_APP , OAuth2ClientProfile.USER_AGENT)

class OAuth2ClientRegistryTest: FunSpec(), KoinTest {

    private val testModule = module {
        single<OAuth2ClientRegistry> { InMemoryClientRegistry() }
    }

    override fun listeners() = listOf(KoinListener(testModule))

    init {

        /**
         * OAuth2 Client Registry Test - clear / isEmpty
         */
        test("""
            OAuth2 Client Registry Test - clear / isEmpty #1
            - empty registry
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()

            // [WHEN]
            val isEmptyBeforeClear = clientRegistry.isEmpty()

            clientRegistry.clear()

            val isEmptyAfterClear = clientRegistry.isEmpty()

            // [THEN]
            isEmptyBeforeClear.shouldBeTrue()
            isEmptyAfterClear.shouldBeTrue()
        }

        test("""
            OAuth2 Client Registry Test - clear / isEmpty #2
            - after add one client
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()
            clientRegistry.addClient(OAuth2Client(
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
            ))

            // [WHEN]
            val isEmptyBeforeClear = clientRegistry.isEmpty()

            clientRegistry.clear()

            val isEmptyAfterClear = clientRegistry.isEmpty()

            // [THEN]
            isEmptyBeforeClear.shouldBeFalse()
            isEmptyAfterClear.shouldBeTrue()
        }

        test("""
            OAuth2 Client Registry Test - clear / isEmpty #3
            - after add two clients
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()
            clientRegistry.addClient(OAuth2Client(
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
            ))

            clientRegistry.addClient(OAuth2Client(
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
            ))

            // [WHEN]
            val isEmptyBeforeClear = clientRegistry.isEmpty()

            clientRegistry.clear()

            val isEmptyAfterClear = clientRegistry.isEmpty()

            // [THEN]
            isEmptyBeforeClear.shouldBeFalse()
            isEmptyAfterClear.shouldBeTrue()
        }


        /**
         * OAuth2 Client Registry Test - checkRedirectUriDuplicate(String)
         */
        test("""
            OAuth2 Client Registry Test - checkRedirectUriDuplicate(String) #1
            - initial
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()

            // [WHEN]
            val isDuplicate = clientRegistry.checkRedirectUriDuplicate(redirectUriValid1)

            // [THEN]
            isDuplicate.shouldBeFalse()
        }

        test("""
            OAuth2 Client Registry Test - checkRedirectUriDuplicate(String) #2
            - after add one client(redirect uri가 비어있는)
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()
            clientRegistry.addClient(OAuth2Client(
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
            ))

            // [WHEN]
            val isDuplicate = clientRegistry.checkRedirectUriDuplicate(redirectUriValid1)

            // [THEN]
            isDuplicate.shouldBeFalse()
        }

        test("""
            OAuth2 Client Registry Test - checkRedirectUriDuplicate(String) #3
            - after add one client(target redirect uri를 가지지 않는)
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()
            clientRegistry.addClient(OAuth2Client(
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
            ))

            // [WHEN]
            val isDuplicate = clientRegistry.checkRedirectUriDuplicate(redirectUriValid2)

            // [THEN]
            isDuplicate.shouldBeFalse()
        }

        test("""
            OAuth2 Client Registry Test - checkRedirectUriDuplicate(String) #4
            - after add one client(target redirect uri를 가지는)
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()
            clientRegistry.addClient(OAuth2Client(
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
            ))

            // [WHEN]
            val isDuplicate = clientRegistry.checkRedirectUriDuplicate(redirectUriValid1)

            // [THEN]
            isDuplicate.shouldBeTrue()
        }

        /**
         * OAuth2 Client Registry Test - checkRedirectUriDuplicate(Set<String>)
         */
        test("""
            OAuth2 Client Registry Test - checkRedirectUriDuplicate(Set<String>) #1
            - initial
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()

            // [WHEN]
            val isDuplicate = clientRegistry.checkRedirectUriDuplicate(setOf(redirectUriValid1))

            // [THEN]
            isDuplicate.shouldBeFalse()
        }

        test("""
            OAuth2 Client Registry Test - checkRedirectUriDuplicate(Set<String>) #2
            - after add one client(redirect uri가 비어있는)
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()
            clientRegistry.addClient(OAuth2Client(
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
            ))

            // [WHEN]
            val isDuplicate = clientRegistry.checkRedirectUriDuplicate(setOf(redirectUriValid1))

            // [THEN]
            isDuplicate.shouldBeFalse()
        }

        test("""
            OAuth2 Client Registry Test - checkRedirectUriDuplicate(Set<String>) #3
            - after add one client(target redirect uri를 가지지 않는)
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()
            clientRegistry.addClient(OAuth2Client(
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
            ))

            // [WHEN]
            val isDuplicate = clientRegistry.checkRedirectUriDuplicate(setOf(redirectUriValid2))

            // [THEN]
            isDuplicate.shouldBeFalse()
        }

        test("""
            OAuth2 Client Registry Test - checkRedirectUriDuplicate(Set<String>) #4
            - after add one client(target redirect uri를 가지는)
        """) {
            // [GIVEN]
            val clientRegistry by inject<OAuth2ClientRegistry>()
            clientRegistry.addClient(OAuth2Client(
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
            ))

            // [WHEN]
            val isDuplicate = clientRegistry.checkRedirectUriDuplicate(setOf(redirectUriValid1))

            // [THEN]
            isDuplicate.shouldBeTrue()
        }
    }
}

class OAuth2ClientRegistrationTest: FunSpec(), KoinTest {

    val assertClientId = { clientId: String ->
        clientId.shouldNotBeEmpty()
        // TODO: validate by spec
    }

    val assertClientSecret = { clientSecret: String? , tokenEndpointAuthMethod: OAuth2ClientTokenEndpointAuthMethod? ->
        if(tokenEndpointAuthMethod != OAuth2ClientTokenEndpointAuthMethod.NONE) {
            clientSecret.shouldNotBeEmpty()
        }
        // TODO: validate by spec
    }

    val assertByRequest = { client: OAuth2Client, registrationRequest: OAuth2ClientRegistrationRequest ->

        assertClientId(client.clientId)
        assertClientSecret(client.clientSecret , client.props?.tokenEndpointAuthMethod)

        client.props?.tokenEndpointAuthMethod shouldBe registrationRequest.tokenEndpointAuthMethod
        client.props?.redirectUris shouldContainExactlyInAnyOrder registrationRequest.redirectUris
        client.props?.grantTypes shouldContainExactlyInAnyOrder registrationRequest.grantTypes
        client.props?.responseTypes shouldContainExactlyInAnyOrder registrationRequest.responseTypes
        client.props?.clientName shouldBe registrationRequest.clientName
        client.props?.clientUri shouldBe registrationRequest.clientUri
        client.props?.logoUri shouldBe registrationRequest.logoUri
        client.props?.scope shouldBe if(registrationRequest.scope.isNullOrEmpty()) "" else registrationRequest.scope

        if(registrationRequest.contacts == null) {
            client.props?.contacts.shouldBeNull()
        } else {
            client.props?.contacts shouldContainExactlyInAnyOrder registrationRequest.contacts!!
        }

        client.props?.tosUri shouldBe registrationRequest.tosUri
        client.props?.policyUri shouldBe registrationRequest.policyUri
        client.props?.jwksUri shouldBe registrationRequest.jwksUri

        if(registrationRequest.jwks == null) {
            client.props?.jwks.shouldBeNull()
        } else {
            client.props?.jwks shouldContainExactlyInAnyOrder registrationRequest.jwks!!
        }

        client.props?.softwareId shouldBe registrationRequest.softwareId
        client.props?.softwareVersion shouldBe registrationRequest.softwareVersion
        client.props?.profiles shouldContainExactlyInAnyOrder registrationRequest.profiles
    }

    private val testModule = module {
        single<RandomStringGenerator> { RandomStringGeneratorImpl }

        single<OAuth2ClientRegistrationManager> { ClientRegistrationManager }
    }

    override fun listeners() = listOf(KoinListener(testModule))

    private val clientRegistrationManager by inject<OAuth2ClientRegistrationManager>()

    override fun afterTest(testCase: TestCase, result: TestResult) {
        clientRegistrationManager.clear()
    }

    init {
        /**
         * register new clients
         */
        // TODO: The "jwks_uri" and "jwks" parameters MUST NOT both be present in the same request or response
        test("""
            OAuth2 Client Registration Test - registering new client #1
            - valid uri format
            - confidential type
            - one profile
        """) {
            // [GIVEN]
            val registration = OAuth2ClientRegistrationRequest(
                redirectUris = setOf(redirectUriValid1, redirectUriValid2),
                tokenEndpointAuthMethod = tokenEndpointAuthMethodBasic,
                grantTypes = setOf(OAuth2GrantType.AUTHORIZATION_CODE_GRANT),
                responseTypes = setOf(OAuth2ResponseType.CODE, OAuth2ResponseType.TOKEN),
                clientName = clientName1,
                clientUri = uriValid1,
                logoUri = logoUriValid1,
                scope = scopeEmpty,
                contacts = listOf(contact1, contact2),
                tosUri = tosUriValid1,
                policyUri = policyUriValid1,
                jwksUri = jwkUriValid1,
                jwks = null,
                softwareId = softwareId1,
                softwareVersion = softwareVersion1,
                profiles = profilesOne
            )

            // [WHEN]
            val created = clientRegistrationManager.registerClient(registration)

            // [THEN]
            assertByRequest(created , registration)

            val client = clientRegistrationManager.retrieveClient(clientId = created.clientId)

            client.shouldNotBeNull()
            created shouldBe client
        }

        test("""
            OAuth2 Client Registration Test - registering new client #2
            - valid uri format
            - public type
            - two profiles
        """) {
            // [GIVEN]
            val registration = OAuth2ClientRegistrationRequest(
                redirectUris = setOf(redirectUriValid1, redirectUriValid2),
                tokenEndpointAuthMethod = tokenEndpointAuthMethodBasic,
                grantTypes = setOf(OAuth2GrantType.AUTHORIZATION_CODE_GRANT),
                responseTypes = setOf(OAuth2ResponseType.CODE, OAuth2ResponseType.TOKEN),
                clientName = clientName1,
                clientUri = uriValid1,
                logoUri = logoUriValid1,
                scope = scopeEmpty,
                contacts = listOf(contact1, contact2),
                tosUri = tosUriValid1,
                policyUri = policyUriValid1,
                jwksUri = jwkUriValid1,
                jwks = null,
                softwareId = softwareId1,
                softwareVersion = softwareVersion1,
                profiles = profilesOne
            )

            // [WHEN]
            val created = clientRegistrationManager.registerClient(registration)

            // [THEN]
            assertByRequest(created , registration)

            val client = clientRegistrationManager.retrieveClient(clientId = created.clientId)

            client.shouldNotBeNull()
            created shouldBe client
        }

        test("""
            OAuth2 Client Registration Test - registering new client #3
            - invalid uri format
            - public type
            - three profiles
        """) {
            // [GIVEN]
            val registration = OAuth2ClientRegistrationRequest(
                redirectUris = setOf(redirectUriValid1, redirectUriInvalid),
                tokenEndpointAuthMethod = tokenEndpointAuthMethodBasic,
                grantTypes = setOf(OAuth2GrantType.AUTHORIZATION_CODE_GRANT),
                responseTypes = setOf(OAuth2ResponseType.CODE, OAuth2ResponseType.TOKEN),
                clientName = clientName1,
                clientUri = uriValid1,
                logoUri = logoUriValid1,
                scope = scopeEmpty,
                contacts = listOf(contact1, contact2),
                tosUri = tosUriValid1,
                policyUri = policyUriValid1,
                jwksUri = jwkUriValid1,
                jwks = null,
                softwareId = softwareId1,
                softwareVersion = softwareVersion1,
                profiles = profilesOne
            )

            // [THEN]
            shouldThrow<OAuth2InvalidClientPropException> {
                // [WHEN]
                clientRegistrationManager.registerClient(registration)
            }
        }

        test("""
            OAuth2 Client Registration Test - registering new client #4
            - duplicate redirectUri
        """) {
            // [GIVEN]
            val registration1 = OAuth2ClientRegistrationRequest(
                redirectUris = setOf(redirectUriValid1, redirectUriValid2),
                tokenEndpointAuthMethod = tokenEndpointAuthMethodBasic,
                grantTypes = setOf(OAuth2GrantType.AUTHORIZATION_CODE_GRANT),
                responseTypes = setOf(OAuth2ResponseType.CODE, OAuth2ResponseType.TOKEN),
                clientName = clientName1,
                clientUri = uriValid1,
                logoUri = logoUriValid1,
                scope = scopeEmpty,
                contacts = listOf(contact1, contact2),
                tosUri = tosUriValid1,
                policyUri = policyUriValid1,
                jwksUri = jwkUriValid1,
                jwks = null,
                softwareId = softwareId1,
                softwareVersion = softwareVersion1,
                profiles = profilesOne
            )

            val registration2 = OAuth2ClientRegistrationRequest(
                redirectUris = setOf(redirectUriValid1, redirectUriValid2),
                tokenEndpointAuthMethod = tokenEndpointAuthMethodBasic,
                grantTypes = setOf(OAuth2GrantType.AUTHORIZATION_CODE_GRANT),
                responseTypes = setOf(OAuth2ResponseType.CODE, OAuth2ResponseType.TOKEN),
                clientName = clientName1,
                clientUri = uriValid1,
                logoUri = logoUriValid1,
                scope = scopeEmpty,
                contacts = listOf(contact1, contact2),
                tosUri = tosUriValid1,
                policyUri = policyUriValid1,
                jwksUri = jwkUriValid1,
                jwks = null,
                softwareId = softwareId1,
                softwareVersion = softwareVersion1,
                profiles = profilesOne
            )

            clientRegistrationManager.registerClient(registration1)

            // [THEN]
            shouldThrow<OAuth2ClientPropDuplicateException> {
                // [WHEN]
                clientRegistrationManager.registerClient(registration2)
            }
        }

        /**
         * retrieve clients
         */
        test("""
            OAuth2 Client Registration Test - retrieving client #1
            - initial
            - empty string
        """) {
            // [GIVEN]

            // [WHEN]
            val client = clientRegistrationManager.retrieveClient(clientId = "")

            // [THEN]
            client.shouldBeNull()
        }
    }
}