package com.lvvi.vividtv.utils

import android.app.Application
import android.content.Context
import android.content.Intent
import cn.leancloud.AVOSCloud
import cn.leancloud.AVObject
import cn.leancloud.AVQuery
import com.lvvi.vividtv.entity.Entity
import com.lvvi.vividtv.model.VideoDataModelNew
import com.lvvi.vividtv.service.UpdateChannelInfoService
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import java.io.IOException
import java.nio.charset.Charset
import java.util.*

/**
 * Created by lvliheng on 2018/7/12 at 17:08.
 */
class MyApplication : Application() {

    private var sharedPreferences: MySharePreferences? = null
    private var gson: Gson? = null

    override fun onCreate() {
        super.onCreate()
        init()
        setVideoData()
    }

    private fun init() {
        AVOSCloud.initialize(this, Entity.LEANCLOUD_APP_ID, Entity.LEANCLOUD_APP_KEY)

        context = this
        sharedPreferences = MySharePreferences.getInstance(context)
        gson = Gson()
    }

    private fun setVideoData() {
        val query = AVQuery<AVObject>(Entity.AVOBJECT_CLASS_NAME)
        query.addAscendingOrder(Entity.AVOBJECT_ORDER)
        query.whereEqualTo(Entity.IS_SHOW, "1")

        query.findInBackground().subscribe(object : Observer<List<AVObject>> {
            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(avObjects: List<AVObject>) {
                val videoDataList = ArrayList<VideoDataModelNew>()
                var videoData : VideoDataModelNew?
                for (avObject in avObjects) {
                    videoData = VideoDataModelNew()
                    videoData.id = avObject.getString(Entity.AVOBJECT_ID)
                    videoData.name = avObject.getString(Entity.AVOBJECT_NAME)
                    videoData.icon = avObject.getString(Entity.AVOBJECT_ICON)
                    videoData.url1 = avObject.getString(Entity.AVOBJECT_URL1)
                    videoData.url2 = avObject.getString(Entity.AVOBJECT_URL2)
                    videoData.title = ""
                    videoData.startTime = ""
                    videoData.endTime = ""

                    videoDataList.add(videoData)
                }

                if (videoDataList.size > 0) {
                    if (sharedPreferences == null) {
                        sharedPreferences = MySharePreferences.getInstance(context)
                    }

                    sharedPreferences?.putString(Entity.MEDIA_DATA, gson!!.toJson(videoDataList))
                } else {
                    setLocalVideoData()
                }

                startUpdateChannelInfoService()
            }

            override fun onError(e: Throwable) {
                setLocalVideoData()
                startUpdateChannelInfoService()
            }

            override fun onComplete() {
            }
        })
    }

    private fun startUpdateChannelInfoService() {
        val updateChannelInfoService = Intent()
        updateChannelInfoService.setClass(context, UpdateChannelInfoService::class.java)
        startService(updateChannelInfoService)
    }

    private fun setLocalVideoData() {
        if (gson == null) {
            gson = Gson()
        }

        if (sharedPreferences == null) {
            sharedPreferences = MySharePreferences.getInstance(context)
        }

        val mediaData = sharedPreferences!!.getString(Entity.MEDIA_DATA)

        if (mediaData!!.isEmpty()) {
            var result = ""
            try {
                val inputStream = assets.open("video_data.json")
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()
                result = String(buffer, Charset.forName("UTF-8"))
            } catch (e: IOException) {
                e.printStackTrace()
            }

            if (result.isNotEmpty()) {
                sharedPreferences?.putString(Entity.MEDIA_DATA, result)
            }
        }

    }

    fun getVideoData(context: Context): List<VideoDataModelNew> {
        var channelsBeans: List<VideoDataModelNew> = ArrayList()
        if (sharedPreferences == null) {
            sharedPreferences = MySharePreferences.getInstance(context)
        }
        if (gson == null) {
            gson = Gson()
        }
        val mediaData = sharedPreferences!!.getString(Entity.MEDIA_DATA)
        if (mediaData != "") {
            channelsBeans = gson!!.fromJson(mediaData,
                    object : TypeToken<List<VideoDataModelNew>>() {

                    }.type)
        }
        return channelsBeans
    }

    fun setLastId(context: Context, id: String) {
        if (sharedPreferences == null) {
            sharedPreferences = MySharePreferences.getInstance(context)
        }
        sharedPreferences!!.putString(Entity.LAST_MEDIA_ID, id)
    }

    fun getLastId(context: Context): String? {
        if (sharedPreferences == null) {
            sharedPreferences = MySharePreferences.getInstance(context)
        }
        return sharedPreferences!!.getString(Entity.LAST_MEDIA_ID)
    }

    fun setLastUrl(context: Context, url: String) {
        if (sharedPreferences == null) {
            sharedPreferences = MySharePreferences.getInstance(context)
        }
        sharedPreferences!!.putString(Entity.LAST_MEDIA_URL, url)
    }

    fun getLastUrl(context: Context): String? {
        if (sharedPreferences == null) {
            sharedPreferences = MySharePreferences.getInstance(context)
        }
        return sharedPreferences!!.getString(Entity.LAST_MEDIA_URL)
    }

    companion object {

        lateinit var context: MyApplication
        private var myApplication: MyApplication? = null

            get() {
                if (field == null) {
                    field = MyApplication()
                }
                return field
            }
        fun get(): MyApplication {
            return myApplication!!
        }
    }


}
