package vip.frendy.khttp

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.io.FileOutputStream
import java.io.InputStream


/**
 * Created by frendy on 2017/10/31.
 */
class KDownload {
    var url: String? = null
    var filePath: String? = null
    var fileName: String? = null

    internal var _requestProgress: (Long, Long) -> Unit = { bytesWritten, contentLength -> }
    internal var _fail: (String?) -> Unit = { e -> }
    internal var _success: (String?) -> Unit = { file -> }

    fun onRequestProgress(onRequestProgress: (Long, Long) -> Unit) {
        _requestProgress = onRequestProgress
    }

    fun onFail(onFail: (String?) -> Unit) {
        _fail = onFail
    }

    fun onSuccess(onSuccess: (String?) -> Unit) {
        _success = onSuccess
    }
}

fun Download(init: KDownload.() -> Unit): Call? {
    val wrap = KDownload()
    wrap.init()

    return download(wrap)
}

private fun download(wrap: KDownload): Call? {
    if(wrap.filePath == null || wrap.fileName == null || wrap.url == null) return null

    val request = Request.Builder()
            .url(wrap.url!!)
            .build()

    val httpClient = KHttpClient.getInstance()
    val call = httpClient.newCall(request)

    call.enqueue(object : okhttp3.Callback {
        override fun onFailure(call: Call?, e: IOException?) {
            wrap._fail(e.toString())
        }
        override fun onResponse(call: Call?, response: Response?) {
            if(response == null || response.body() == null) {
                wrap._fail("response null")
                return
            }

            val file = File(wrap.filePath, wrap.fileName)
            if(file.exists()) file.delete()

            var input: InputStream? = null
            val buf = ByteArray(2048)
            var len = 0
            var fos: FileOutputStream? = null
            try {
                input = response.body()!!.byteStream()
                val contentLength = response.body()!!.contentLength()

                fos = FileOutputStream(file)
                var bytesWritten: Long = 0
                while (len != -1) {
                    fos.write(buf, 0, len)
                    bytesWritten += len.toLong()
                    len = input.read(buf)

                    wrap._requestProgress(bytesWritten, contentLength)
                }
                fos.flush()
                wrap._success(file.absolutePath)

            } catch (e: Exception) {
                wrap._fail(e.toString())

            } finally {
                try {
                    if (input != null) input.close()
                } catch (e: IOException) { }

                try {
                    if (fos != null) fos.close()
                } catch (e: IOException) { }
            }
        }
    })
    return call
}