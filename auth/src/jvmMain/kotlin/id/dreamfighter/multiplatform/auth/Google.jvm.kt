package id.dreamfighter.multiplatform.auth

import id.dreamfighter.multiplatform.api.model.Request
import id.dreamfighter.multiplatform.api.req
import id.dreamfighter.multiplatform.auth.model.AccountService
import id.dreamfighter.multiplatform.auth.model.GoogleUser
import id.dreamfighter.multiplatform.auth.utils.FileUtil
import id.dreamfighter.multiplatform.auth.utils.FileUtil.toObject
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.server.application.install
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.netty.NettyApplicationEngine
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GoogleJvm : Google {
    private val mainScope = CoroutineScope(Dispatchers.Default)

    private fun startServer(code:(String)-> Unit){
        var server: EmbeddedServer<NettyApplicationEngine,NettyApplicationEngine.Configuration>? = null
        val successHtml = FileUtil.readFile("/assets/success-page.html")
        server = embeddedServer(Netty, host = "127.0.0.1", port = 9002) {
            install(WebSockets)
            routing {
                get("/auth"){
                    println(call.parameters["code"])
                    call.parameters["code"]?.let { code(it) }
                    if (successHtml != null) {
                        val message = TextContent(successHtml, ContentType("text","html"), HttpStatusCode.OK)
                        call.respond(message)
                    }
                    server?.run {
                        println("stopping server")
                        stop()
                    }
                }
                webSocket("/auth") {
                    println(call.parameters["code"])
                    for (frame in incoming) {
                        frame as? Frame.Text ?: continue
                        val receivedText = String(frame.data)
                        if (receivedText.trim() == "bye") {
                            close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                        } else {
                            send(Frame.Text("Hi, $receivedText!"))
                        }
                    }
                }
            }
        }

        server.start(wait = false)
        println("the server is stopped")

    }

    override fun auth(user: (GoogleUser) -> Unit, error: (Exception) -> Unit?) {
        val json = FileUtil.readFile("/assets/google-services.json")
        println("json $json")
        json?.let {
            val accountService: AccountService = json.toObject<AccountService>()

            val rt = Runtime.getRuntime()
            val clientId = accountService.clientId
            val redirectUri = "http://localhost:9002/auth"
            val responseType = "code"
            val state = "auth"
            val scope = accountService.scopes
            val url = "https://accounts.google.com/o/oauth2/v2/auth?client_id=${clientId}&redirect_uri=${redirectUri}&response_type=${responseType}&state=${state}&scope=${scope}"
            val browsers = arrayOf(
                "google-chrome", "firefox", "mozilla", "epiphany", "konqueror",
                "netscape", "opera", "links", "lynx"
            )

            val cmd = StringBuffer()
            for (i in browsers.indices) {
                if (i == 0) {
                    cmd.append(
                        String.format(
                            "%s \"%s\"",
                            browsers[i],
                            url
                        )
                    )
                } else {
                    cmd.append(String.format(" || %s \"%s\"", browsers[i], url))
                }
            }

            // If the first didn't work, try the next browser and so on
            rt.exec(arrayOf("sh", "-c", cmd.toString()))

            startServer {
                val payload = mapOf(
                    "code" to it,
                    "client_id" to accountService.clientId,
                    "client_secret" to accountService.clientSecret,
                    "grant_type" to "authorization_code",
                    "redirect_uri" to "http://localhost:9002/auth"
                )

                mainScope.launch{
                    req<GoogleUser>(getGooleToken(payload))
                }
            }
        }
    }
}

actual val google: Google = GoogleJvm()

fun getGooleToken(payload:Map<String, String>): Request {
    return Request(url = "https://oauth2.googleapis.com/token", method = "POST", path = mapOf(),query = mapOf(), body = payload, headers = mapOf("Content-Type" to "application/x-www-form-urlencoded"))
}