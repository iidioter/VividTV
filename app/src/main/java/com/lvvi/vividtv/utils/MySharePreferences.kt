package com.lvvi.vividtv.utils

import android.content.Context
import android.content.SharedPreferences


/**
 * Created by lvliheng on 2018/7/12 at 18:38.
 */
class MySharePreferences private constructor() {

    fun putString(key: String, value: String) {
        editor!!.putString(key, value)
        editor!!.apply()
    }

    fun getString(key: String): String? {
        return sharedPreferences!!.getString(key, "")
    }

    fun putInt(key: String, value: Int): Boolean {
        editor!!.putInt(key, value)
        return editor!!.commit()
    }

    fun getInt(key: String): Int? {
        return sharedPreferences!!.getInt(key, 0)
    }

    companion object {

        private var sharedPreferences: SharedPreferences? = null
        private var editor: SharedPreferences.Editor? = null
        private var mySharePreferences: MySharePreferences? = null

        fun getInstance(context: Context): MySharePreferences {
            if (mySharePreferences == null) {
                mySharePreferences = MySharePreferences()
                sharedPreferences = context.getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE)
                editor = sharedPreferences!!.edit()
            }
            return mySharePreferences as MySharePreferences
        }
    }

}