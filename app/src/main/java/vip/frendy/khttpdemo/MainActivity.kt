package vip.frendy.khttpdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import vip.frendy.khttp.Callback
import vip.frendy.khttpdemo.entity.News
import vip.frendy.khttpdemo.entity.UserID
import vip.frendy.khttpdemo.net.Request

class MainActivity : AppCompatActivity() {

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
    }
}
