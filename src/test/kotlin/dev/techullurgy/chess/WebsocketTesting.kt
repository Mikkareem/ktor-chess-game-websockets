package dev.techullurgy.chess

import dev.techullurgy.chess.events.constants.BaseEventConstants
import io.ktor.client.plugins.api.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.testing.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.Test


fun interceptor(clientId: String) = createClientPlugin("Intercept") {
    onRequest { request, _ ->
        request.parameter("client_id", clientId)
    }
}

class WebsocketTesting {

    @Test
    fun testWebSocketsConnection() = testApplication {
        application {
            module()
        }

        val client1 = createClient {
            install(interceptor("client1"))
            install(HttpCookies)
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }

        val client2 = createClient {
            install(interceptor("client2"))
            install(HttpCookies)
            install(WebSockets) {
                contentConverter = KotlinxWebsocketSerializationConverter(Json)
            }
        }

        runBlocking {
            launch {
                client1.webSocket("/join/ws") {
                    send("""{ "username": "abc", "roomId": "3rec", "type": "${BaseEventConstants.TYPE_JOIN_ROOM_HANDSHAKE}" }""")
                    for (frame in incoming) {
                        if(frame is Frame.Text) {
                            println("Received for abc: ${frame.readText()}")
                        }
                    }
                }
            }
            delay(5000)
            launch {
                client2.webSocket("/join/ws") {
                    send("""{ "username": "123", "roomId": "3rec", "type": "${BaseEventConstants.TYPE_JOIN_ROOM_HANDSHAKE}" }""")
                    for (frame in incoming) {
                        if(frame is Frame.Text) {
                            println("Received for 123: ${frame.readText()}")
                        }
                    }
                }
            }
        }.invokeOnCompletion { cause -> println("Coroutine completed") }
    }
}