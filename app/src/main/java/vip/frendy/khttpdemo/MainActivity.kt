package vip.frendy.khttpdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.iimedia.appbase.extension.postDelayedToUI
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.Call
import okhttp3.mockwebserver.MockWebServer
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import vip.frendy.khttp.Callback
import vip.frendy.khttp.KSocket
import vip.frendy.khttp.Socket
import vip.frendy.khttp.Upload
import vip.frendy.khttpdemo.entity.News
import vip.frendy.khttpdemo.entity.UserID
import vip.frendy.khttpdemo.mock.MockServer
import vip.frendy.khttpdemo.net.Request
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    private var mSocket: KSocket? = null
    private var mWebServer: MockWebServer? = null
    private var mUploadCall: Call? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Request(this).getNewsList("0", "0")

        Request(this).getNewsList("0", "0", object : Callback<ArrayList<News>> {
            override fun onSuccess(data: ArrayList<News>) {
                Log.i("", "GET = ${data[0].title}")
            }
            override fun onFail(error: String?) {
                Log.i("", "GET error = $error")
            }
        })

        Request(this).init(object : Callback<UserID>{
            override fun onSuccess(data: UserID) {
                Log.i("", "POST = ${data.user_id}")
            }
            override fun onFail(error: String?) {
                Log.i("", "POST error = $error")
            }
        })


        /**
         * Mock Server of WebSocket
         */
        startServer()

        socketSend.setOnClickListener {
            mSocket?.getWebSocket()?.send("Hello")
        }


        /**
         * Upload file
         */
        upload.setOnClickListener {
            mUploadCall = Upload {
                url = "http://api.myxianwen.cn/26/fileUpload.action?app_id=5020&userId=0"
                file = File("/storage/sdcard0/DCIM/ImageSelector_20170823_144056.mp4")
                fileKey = "file"

                params = HashMap<String, String>().apply {
                    put("test", "testfile")
                }

                onRequestProgress { bytesWritten, contentLength ->
                    Log.i("", "** UPLOAD - onRequestProgress : ${(bytesWritten * 100) / contentLength}%")
                }

                onFailure { err ->
                    Log.i("", "** UPLOAD - onFailure : ${err?.toString()}")
                }

                onResponse { response ->
                    if(response != null && response.body() != null) {
                        if(response.isSuccessful()) {
                            Log.i("", "** UPLOAD - onResponse : ${response.body()!!.string()}")
                        }
                    }
                }
            }
        }

        uploadCancle.setOnClickListener {
            mUploadCall?.cancel()
        }
    }

    private fun startServer() = doAsync {
        mWebServer = MockServer().getWebServer()

        val hostname = mWebServer?.hostName
        val port = mWebServer?.port

        uiThread {
            initSocket(hostname, port)
        }
    }

    private fun initSocket(hostname: String?, port: Int?) {
        mSocket = Socket {
//            url = "ws://commonother.myxianwen.cn/multichat/chatroom/1"
            url = "ws://${hostname}:${port}/"

            onOpen { webSocket, response ->
                runOnUiThread {
                    toast("Open")
                }
            }
            onMessage { webSocket, text ->
                runOnUiThread {
                    toast("Message: ${text}")
                }
            }
            onClosing { webSocket, code, reason ->
                runOnUiThread {
                    toast("Closing: ${code}, ${reason}")
                }
            }
            onClosed { webSocket, code, reason ->
                runOnUiThread {
                    toast("Closed: ${code}, ${reason}")
                }
            }
            onFailure { webSocket, t, response ->
                runOnUiThread {
                    toast("Failure")
                    postDelayedToUI({
                        mSocket?.connect()
                    }, 30000)
                }
            }
        }
    }
}
