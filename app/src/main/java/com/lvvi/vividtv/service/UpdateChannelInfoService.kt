package com.lvvi.vividtv.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.lvvi.vividtv.entity.Entity
import com.lvvi.vividtv.model.ChannelInfoModel
import com.lvvi.vividtv.model.VideoDataModelNew
import com.lvvi.vividtv.utils.HttpHelper
import com.lvvi.vividtv.utils.MySharePreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import kotlin.collections.ArrayList

class UpdateChannelInfoService : Service() {

    private var sharedPreferences: MySharePreferences? = null
    private var gson: Gson? = null

    private var nextUpdateTime: Long = 0

    private var timer: Timer? = null

    override fun onCreate() {
        super.onCreate()
        nextUpdateTime = 0
        getChannelInfo()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun getChannelInfo() {
        HttpHelper.get().request(Entity.CHANNEL_INFO_API_ALL, object : HttpHelper.HttpCallBack {
            override fun onSuccess(result: String) {
                updateChannelInfo(result)
            }

            override fun onFailure(msg: String) {

            }

            override fun onError(msg: String) {

            }
        })
    }

    private fun updateChannelInfo(channelInfo: String) {
        var channelsBeans: List<VideoDataModelNew> = ArrayList()
        if (sharedPreferences == null) {
            sharedPreferences = MySharePreferences.getInstance(applicationContext)
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

        var channelInfoModel = ChannelInfoModel()
        if (channelInfo != "") {
            channelInfoModel = gson!!.fromJson(channelInfo, ChannelInfoModel::class.java)
        }

        if (channelInfoModel.EPG == null || channelInfoModel.EPG!!.isEmpty()) {
            return
        }

        var lastMillis: String
        var minMillis = java.lang.Long.MAX_VALUE
        nextUpdateTime = 0

        for (i in channelsBeans.indices) {
            for (j in 0 until channelInfoModel.EPG!!.size) {
                if (channelsBeans[i].id == channelInfoModel.EPG!![j].channelId) {
                    channelsBeans[i].title = channelInfoModel.EPG!![j].title
                    channelsBeans[i].startTime = channelInfoModel.EPG!![j].startTime
                    channelsBeans[i].endTime = channelInfoModel.EPG!![j].endTime

                    lastMillis = channelInfoModel.EPG!![j].endTime!!
                    if (lastMillis != "") {
                        if (lastMillis.toLong() < minMillis) {
                            minMillis = lastMillis.toLong()
                            nextUpdateTime = minMillis * 1000 - System.currentTimeMillis()
                        }
                    }
                }

            }
        }
        sharedPreferences!!.putString(Entity.MEDIA_DATA, gson!!.toJson(channelsBeans))

        val delay = 2 * 60 * 1000
        nextUpdateTime += delay.toLong()
        if (nextUpdateTime < delay) {
            nextUpdateTime = delay.toLong()
        }
        startTimer()
    }

    private fun startTimer() {
        if (timer == null) {
            timer = Timer()
        }
        timer?.schedule(object : TimerTask() {
            override fun run() {
                getChannelInfo()
            }
        }, nextUpdateTime)
    }

    override fun onDestroy() {
        timer?.cancel()
        timer = null
        super.onDestroy()
    }
}
