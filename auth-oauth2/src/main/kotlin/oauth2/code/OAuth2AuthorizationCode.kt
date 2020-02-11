package oauth2.code

import dagger.Component
import dagger.Module
import dagger.Provides
import oauth2.context.OAuth2Context
import oauth2.exception.OAuth2AuthorizationFailedException
import oauth2.request.OAuth2Request
import javax.inject.Singleton

data class AuthorizationCode(val code: String) {
    companion object {
        const val NAME = "code"
    }
}

interface OAuth2AuthorizationCodeGenerator {
    fun generateCode(): String
}

object AuthorizationCodeGenerator: OAuth2AuthorizationCodeGenerator {
    override fun generateCode(): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}


interface OAuth2AuthorizationCodeManager {
    fun issueAuthorizationCode(request: OAuth2Request): AuthorizationCode

    fun consumeAuthorizationCode(value: String)
}

// TODO: codeGenerator - use delegation(by)

object InMemoryAuthorizationCodeManager: OAuth2AuthorizationCodeManager {

    // FIXME: DI
    private val codeGenerator: OAuth2AuthorizationCodeGenerator = AuthorizationCodeGenerator

    private val store = mutableMapOf<String, AuthorizationCode>()

    override fun issueAuthorizationCode(request: OAuth2Request): AuthorizationCode {

        // TODO: generate code value
        // duplicate?
        val value = codeGenerator.generateCode()

        val code = AuthorizationCode(value)

        // store
        // TODO: store code with request(client) information
        store[value] = code

        // issue authorization code
        return code
    }

    override fun consumeAuthorizationCode(value: String) {
        // take code from store
        val code = store.remove(value)

        // check if code was issued
        code ?: throw OAuth2AuthorizationFailedException(code = value)

        // TODO: check if code is associated with request(client)
    }
}

@Module
class AuthorizationCodeModule {

    @Provides
    @Singleton
    fun provideAuthorizationCodeManager() = InMemoryAuthorizationCodeManager
}

@Singleton
@Component(modules = [AuthorizationCodeModule::class])
interface AuthorizationCodeComponent {

    @Component.Builder
    interface Builder {
        fun build(): AuthorizationCodeComponent
    }
}