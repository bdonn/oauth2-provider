package oauth2.grantflow.implicit

import oauth2.request.*
import oauth2.response.OAuth2ImplicitGrantResponse
import org.junit.Assert.*
import org.junit.Test

class OAuth2ImplicitGrantTest {

    @Test
    fun `Implicit grant flow - Authorization endpoint 테스트`() {
        val response = OAuth2ImplicitGrant.flow(
            request = OAuth2ImplicitGrantRequest.AuthorizationRequest(
                responseType = ResponseType(OAuth2ResponseType.TOKEN.type),
                redirectUri = RedirectUri("https://www.naver.com"),
                clientId = ClientId("client-2"),
                scope = null,
                state = null
            )
        )

        assertTrue(response is OAuth2ImplicitGrantResponse.TokenResponse)

        response.validateParams()
    }

    @Test
    fun `Kotlin Test`() {
        var a: String? = "222"

        print("[not null]")
        print(a?.length)

        a = null

        print("[null]")
        print(a?.length)
    }
}