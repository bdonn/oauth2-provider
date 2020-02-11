package oauth2.request

import oauth2.exception.OAuth2InvalidRequestParameterException
import org.junit.Test

class OAuth2RequestTest {

    @Test(expected = OAuth2InvalidRequestParameterException::class)
    fun validateParams() {
        val request = OAuth2AuthorizationCodeGrantRequest.AuthorizationRequest(
            responseType = ResponseType("code"),
            redirectUri = RedirectUri(""),
            scope = Scope("openid"),
            clientId = ClientId(""),
            state = State("aaa"),
            clientCredential = ClientCredential("ddd", clientId = "", clientSecret = "")
            )

        request.validateParams()
    }

}