package vip.frendy.khttp

import okhttp3.*

/**
 * Created by frendy on 2017/9/18.
 */
class KSocket {
    var url: String? = null
    var mSocket: WebSocket? = null

    internal var _open: (webSocket: WebSocket?, response: Response?) -> Unit = { webSocket: WebSocket?, response: Response? -> }
    internal var _message: (webSocket: WebSocket?, text: String?) -> Unit = { webSocket: WebSocket?, text: String? -> }
    internal var _closing: (webSocket: WebSocket?, code: Int, reason: String?) -> Unit = { webSocket: WebSocket?, code: Int, reason: String? -> }
    internal var _closed: (webSocket: WebSocket?, code: Int, reason: String?) -> Unit = { webSocket: WebSocket?, code: Int, reason: String? -> }
    internal var _failure: (webSocket: WebSocket?, t: Throwable?, response: Response?) -> Unit = { webSocket: WebSocket?, t: Throwable?, response: Response? -> }

    fun onOpen(onOpen: (webSocket: WebSocket?, response: Response?) -> Unit) {
        _open = onOpen
    }
    fun onMessage(onMessage: (webSocket: WebSocket?, text: String?) -> Unit) {
        _message = onMessage
    }
    fun onClosing(onClosing: (webSocket: WebSocket?, code: Int, reason: String?) -> Unit) {
        _closing = onClosing
    }
    fun onClosed(onClosed: (webSocket: WebSocket?, code: Int, reason: String?) -> Unit) {
        _closed = onClosed
    }
    fun onFailure(onFailure: (webSocket: WebSocket?, t: Throwable?, response: Response?) -> Unit) {
        _failure = onFailure
    }

    fun connect() {
        val client = OkHttpClient()
        val request = try {
            Request.Builder().url(url).build()
        } catch (e: IllegalArgumentException) {
            error { "${e.message}" }
        }
        val listener = Listener(this)

        mSocket = client.newWebSocket(request, listener)
    }

    fun getSocket(): WebSocket? {
        return mSocket
    }
}

fun Socket(init: KSocket.() -> Unit): KSocket {
    val wrap = KSocket()
    wrap.init()
    wrap.connect()

    return wrap
}

class Listener(var wrap: KSocket): WebSocketListener() {
    override fun onOpen(webSocket: WebSocket?, response: Response?) {
        wrap._open(webSocket, response)
    }
    override fun onMessage(webSocket: WebSocket?, text: String?) {
        wrap._message(webSocket, text)
    }
    override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
        wrap._closing(webSocket, code, reason)
    }
    override fun onClosed(webSocket: WebSocket?, code: Int, reason: String?) {
        wrap._closed(webSocket, code, reason)
    }
    override fun onFailure(webSocket: WebSocket?, t: Throwable?, response: Response?) {
        wrap._failure(webSocket, t, response)
    }
}