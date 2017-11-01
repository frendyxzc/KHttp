package vip.frendy.khttp

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import java.util.concurrent.TimeUnit



/**
 * Created by frendy on 2017-7-5.
 */
class KRequest {
    var url: String? = null
    var method: String? = null
    var body: RequestBody? = null

    internal var _success: (String) -> Unit = { }
    internal var _fail: (Throwable) -> Unit = { }
    internal var _execute: () -> Unit = { }

    fun onExecute(onExecute: () -> Unit) {
        _execute = onExecute
    }
    fun onSuccess(onSuccess: (String) -> Unit) {
        _success = onSuccess
    }
    fun onFail(onError: (Throwable) -> Unit) {
        _fail = onError
    }
}

interface Callback<T> {
    fun onSuccess(data: T)
    fun onFail(error: String?)
}

val JSON: MediaType = MediaType.parse("application/json; charset=utf-8")!!

fun http(init: KRequest.() -> Unit) {
    val wrap = KRequest()
    wrap.init()
    executeForResult(wrap)
}



private fun executeForResult(wrap: KRequest) {
    Flowable.create<Response>(
            { e -> e.onNext(onExecute(wrap)) },
            BackpressureStrategy.BUFFER
    ).subscribeOn(
            Schedulers.io()
    ).subscribe(
            { resp -> wrap._success(resp.body()!!.string()) },
            { e -> wrap._fail(e) }
    )
}



private fun onExecute(wrap: KRequest): Response {
    var req: Request? = null
    when (wrap.method) {
        "get", "Get", "GET" -> req = Request.Builder().url(wrap.url).build()
        "post", "Post", "POST" -> req = Request.Builder().url(wrap.url).post(wrap.body).build()
        "put", "Put", "PUT" -> req = Request.Builder().url(wrap.url).put(wrap.body).build()
        "delete", "Delete", "DELETE" -> req = Request.Builder().url(wrap.url).delete(wrap.body).build()
    }
    wrap._execute()

    val httpClient = KHttpClient.getInstance()
    val resp = httpClient.newCall(req).execute()
    return resp
}