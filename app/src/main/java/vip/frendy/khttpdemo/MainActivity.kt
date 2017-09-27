package vip.frendy.khttpdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.iimedia.appbase.extension.postDelayedToUI
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import vip.frendy.khttp.Callback
import vip.frendy.khttp.KSocket
import vip.frendy.khttp.Socket
import vip.frendy.khttpdemo.entity.News
import vip.frendy.khttpdemo.entity.UserID
import vip.frendy.khttpdemo.net.Request
import java.util.*

class MainActivity : AppCompatActivity() {
    private var mSocket: KSocket? = null

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

        mSocket = Socket {
            url = "ws://10.1.1.186:8010/multichat/chatroom/1"

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

        send.setOnClickListener {
            mSocket?.getWebSocket()?.send("Hello")
        }
    }
}
