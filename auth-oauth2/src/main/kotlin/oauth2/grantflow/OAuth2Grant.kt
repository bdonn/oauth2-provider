package oauth2.grantflow

import oauth2.request.OAuth2Request
import oauth2.response.OAuth2Response

interface OAuth2Grant<REQ: OAuth2Request , RES: OAuth2Response> {
    fun flow(request: REQ): RES
}