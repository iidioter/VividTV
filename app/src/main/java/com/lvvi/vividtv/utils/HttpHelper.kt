package com.lvvi.vividtv.utils


import android.text.TextUtils
import android.util.Log
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * Created by lvliheng on 2017/4/5 at 16:29.
 */
class HttpHelper private constructor() {

    fun request(url: String) {
        request(REQUEST_TYPE_GET, url, null, null)
    }

    fun request(url: String, requestBody: RequestBody) {
        request(REQUEST_TYPE_POST, url, requestBody, null)
    }

    fun request(url: String, callBack: HttpCallBack) {
        request(REQUEST_TYPE_GET, url, null, callBack)
    }

    fun request(url: String, requestBody: RequestBody, callBack: HttpCallBack) {
        request(REQUEST_TYPE_POST, url, requestBody, callBack)
    }

    /**
     * 网络请求
     * @param type 类型 0: GET; 1: POST; 2: DELETE
     * @param url 请求地址
     * @param requestBody 请求参数
     * @param callBack 回调方法
     */
    private fun request(type: Int, url: String, requestBody: RequestBody?, callBack: HttpCallBack?) {
        try {
            if (showLog) Log.e(TAG, "→→→ $url ←←←")
            if (url == "") {
                return
            }

            val okHttpClient = OkHttpClient.Builder()
                    .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .readTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .writeTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .build()

            var tempRequest = Request.Builder()
                    .url(url)
                    .build()
            when (type) {
                REQUEST_TYPE_GET -> tempRequest = Request.Builder()
                        .url(url)
                        .build()
                REQUEST_TYPE_POST -> if (requestBody != null) {
                    tempRequest = Request.Builder()
                            .url(url)
                            .post(requestBody)
                            .build()
                }
                REQUEST_TYPE_DELETE -> if (requestBody != null) {
                    tempRequest = Request.Builder()
                            .url(url)
                            .delete(requestBody)
                            .build()
                }
                else -> {
                }
            }
            val request = tempRequest

            val call = okHttpClient.newCall(request)
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (!TextUtils.isEmpty(e.message)) {
                        if (showLog) Log.e(TAG, e.message)
                    }
                    callBack?.onFailure(FAIL_MSG)
                }

                override fun onResponse(call: Call, response: Response) {
                    try {
                        val result = response.body()?.string()
                        if (showLog) Log.e(TAG, "↓↓↓ $url ↓↓↓")
                        if (showLog) Log.e(TAG, result)

                        if (result == null) {
                            callBack?.onError(ERROR_MSG)
                        } else {
                            callBack?.onSuccess(result)
                        }

                        response.body()?.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        callBack?.onError(ERROR_MSG)
                    }

                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            callBack?.onError(ERROR_MSG)
        }

    }

    interface HttpCallBack {
        fun onSuccess(result: String)

        fun onFailure(msg: String)

        fun onError(msg: String)
    }

    companion object {

        private const val TAG = "HttpHelper"
        private const val showLog = false

        private const val REQUEST_TYPE_GET = 0
        private const val REQUEST_TYPE_POST = 1
        private const val REQUEST_TYPE_DELETE = 2

        private const val FAIL_MSG = "网络异常"
        private const val ERROR_MSG = "数据异常"

        private const val CONNECT_TIMEOUT = 60


        private var httpHelper: HttpHelper? = null

            get(){
                if (field == null) {
                    field = HttpHelper()
                }
                return field
            }

        fun get(): HttpHelper {
            return httpHelper!!
        }
    }


}