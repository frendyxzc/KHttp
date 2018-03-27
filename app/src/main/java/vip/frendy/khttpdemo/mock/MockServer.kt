package vip.frendy.khttpdemo.mock

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer



/**
 * Created by iiMedia on 2017/10/20.
 */
class MockServer() {
    private val mServer = MockWebServer()

    fun getWebServer(): MockWebServer = mServer

    init {
        mServer.enqueue(MockResponse().withWebSocketUpgrade(object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("server onOpen")
                println("server request header:" + response.request().headers())
                println("server response header:" + response.headers())
                println("server response:" + response)
            }

            override fun onMessage(webSocket: WebSocket?, string: String?) {
                println("server onMessage")
                println("message:" + string!!)
                webSocket!!.send("response-" + string)
            }

            override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                println("server onClosing")
                println("code:$code reason:$reason")
            }

            override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
                println("server onClosed")
                println("code:$code reason:$reason")
            }

            override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
                println("server onFailure")
                println("throwable:" + t)
                println("response:" + response)
            }
        }))
    }
}