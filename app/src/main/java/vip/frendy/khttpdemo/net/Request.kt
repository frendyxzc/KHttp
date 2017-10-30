package vip.frendy.khttpdemo.net

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import okhttp3.RequestBody
import org.json.JSONObject
import vip.frendy.khttp.Callback
import vip.frendy.khttp.JSON
import vip.frendy.khttp.http
import vip.frendy.khttpdemo.entity.*
import vip.frendy.khttpdemo.util.DeviceInfo

/**
 * Created by iiMedia on 2017/7/5.
 */

class Request(val context: Context, val gson: Gson = Gson()) {

    fun getNewsList(uid: String, cid: String) {
        val url_get = RequestCommon.GET_NEWS_LIST + "&uid=${uid}&type_id=${cid}"
        var startTime = 0L
        var endTime = 0L

        http {
            url = url_get
            method = "get"
            onExecute {
                startTime = System.currentTimeMillis()
            }
            onSuccess { jsonStr ->
                endTime = System.currentTimeMillis()
                Log.i("", "** Success!! cost : ${endTime - startTime}ms")
                Log.i("", jsonStr)
            }
            onFail { e ->
                endTime = System.currentTimeMillis()
                Log.i("", "** Fail!! cost : ${endTime - startTime}ms")
                Log.i("", e.message)
            }
        }
    }

    fun getNewsList(uid: String, cid: String, callback: Callback<ArrayList<News>>) {
        val url_get = RequestCommon.GET_NEWS_LIST + "&uid=${uid}&type_id=${cid}"

        http {
            url = url_get
            method = "get"
            onSuccess { jsonStr ->
                callback.onSuccess(gson.fromJson(jsonStr, RespGetNews::class.java).data)
            }
            onFail { e ->
                callback.onFail(e.message)
            }
        }
    }

    fun init(callback: Callback<UserID>) {
        val url_post = RequestCommon.BASE_URL

        val json = JSONObject()
        val params = ReqInit(DeviceInfo.getAndroidID(context))
        json.put("params", gson.toJson(params))
        json.put("action", "init")
        json.put("app_id", Constants.APP_ID)
        json.put("app_key", Constants.APP_KEY)
        json.put("equip_type", 0)
        val postBody = RequestBody.create(JSON, json.toString())

        http {
            url = url_post
            method = "post"
            body = postBody
            onSuccess { jsonStr ->
                callback.onSuccess(gson.fromJson(jsonStr, RespInit::class.java).data)
            }
            onFail { e ->
                callback.onFail(e.message)
            }
        }
    }
}