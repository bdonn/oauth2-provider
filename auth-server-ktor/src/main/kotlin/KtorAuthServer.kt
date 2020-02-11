import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty


fun main() {
    val port = 8080
    val server = embeddedServer(Netty , port = port , module = Application::AuthServer)

    server.start(wait = true)
}

fun Application.AuthServer() {

}