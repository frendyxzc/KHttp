package vip.frendy.khttp

import java.io.File
import vip.frendy.khttp.model.UploadProgressInterceptor
import okhttp3.*
import vip.frendy.khttp.model.CountingRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by frendy on 2017/10/30.
 */
class KUpload {
    var url: String? = null
    var file: File? = null
    var fileKey: String? = null
    var params: HashMap<String, String>? = null

    internal var _requestProgress: (Long, Long) -> Unit = { bytesWritten, contentLength -> }
    internal var _failure: (IOException?) -> Unit = { }
    internal var _response: (Response?) -> Unit = { }

    fun onRequestProgress(onRequestProgress: (Long, Long) -> Unit) {
        _requestProgress = onRequestProgress
    }

    fun onFailure(onFailure: (IOException?) -> Unit) {
        _failure = onFailure
    }

    fun onResponse(onResponse: (Response?) -> Unit) {
        _response = onResponse
    }
}

fun Upload(init: KUpload.() -> Unit): Call? {
    val wrap = KUpload()
    wrap.init()

    return upload(wrap)
}

private fun upload(wrap: KUpload): Call? {
    if(wrap.url == null || wrap.file == null) return null

    val requestBody = MultipartBody.Builder()
    requestBody.setType(MultipartBody.FORM)
    if(wrap.file != null) {
        val fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), wrap.file!!)
        requestBody.addFormDataPart(wrap.fileKey!!, wrap.file!!.name, fileBody)
    }
    if(wrap.params != null && wrap.params!!.isNotEmpty()) {
        for(param in wrap.params!!) {
            requestBody.addFormDataPart(param.key, param.value)
        }
    }

    val request = Request.Builder()
            .url(wrap.url!!)
            .post(requestBody.build())
            .build()

    val interceptor = UploadProgressInterceptor(object : CountingRequestBody.Listener {
        override fun onRequestProgress(bytesWritten: Long, contentLength: Long) {
            wrap._requestProgress(bytesWritten, contentLength)
        }
    })

    val httpBuilder = OkHttpClient.Builder()
    val httpClient = httpBuilder
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    val call = httpClient.newCall(request)

    call.enqueue(object : okhttp3.Callback {
        override fun onFailure(call: Call?, e: IOException?) {
            wrap._failure(e)
        }
        override fun onResponse(call: Call?, response: Response?) {
            wrap._response(response)
        }
    })
    return call
}