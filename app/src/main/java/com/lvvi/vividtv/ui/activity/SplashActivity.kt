package com.lvvi.vividtv.ui.activity

import android.Manifest
import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lvvi.vividtv.R
import com.lvvi.vividtv.utils.MyApplication
import com.lvvi.vividtv.utils.Utils
import java.lang.ref.WeakReference

/**
 * Created by lvliheng on 16/11/24.
 */
class SplashActivity : Activity() {

    private lateinit var splashMeetDaysTv: TextView
    private lateinit var splashBirthDaysTv: TextView

    private lateinit var handler: MyHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash_view)
        hideVirtualButton()
        initView()
        startMeetDaysAnimation()
        startBirthDaysAnimation()
    }

    private fun hideVirtualButton() {
        val decorView = window.decorView
        val uiOptions = (View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_FULLSCREEN)
        decorView.systemUiVisibility = uiOptions
    }

    private fun startMeetDaysAnimation() {
        val animator = ValueAnimator()
        animator.setObjectValues(0, Utils.meetDays)
        animator.duration = ANIMATION_DURATION.toLong()
        animator.addUpdateListener { animation -> splashMeetDaysTv.text =
                animation.animatedValue.toString() }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                splashMeetDaysTv.tag = ""
                handler.sendEmptyMessageDelayed(HANDLER_INTENT_MAIN, HANDLER_DELAY_MILLIS.toLong())
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        animator.start()
    }

    private fun startBirthDaysAnimation() {
        val animator = ValueAnimator()
        animator.setObjectValues(0, Utils.birthDays)
        animator.duration = ANIMATION_DURATION.toLong()
        animator.addUpdateListener { animation -> splashBirthDaysTv.text =
                animation.animatedValue.toString() }
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                splashBirthDaysTv.tag = ""
                handler.sendEmptyMessageDelayed(HANDLER_INTENT_MAIN, HANDLER_DELAY_MILLIS.toLong())
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })
        animator.start()
    }

    private fun initView() {
        handler = MyHandler(this@SplashActivity)
        splashMeetDaysTv = findViewById(R.id.splash_meet_days_tv)
        splashBirthDaysTv = findViewById(R.id.splash_birth_days_tv)
    }

    private class MyHandler(activity: SplashActivity) : Handler() {

        internal var weakReference: WeakReference<SplashActivity> = WeakReference(activity)

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val activity = weakReference.get()?:return
            when (msg.what) {
                HANDLER_INTENT_MAIN -> if (activity.splashMeetDaysTv.tag != null
                        && activity.splashBirthDaysTv.tag != null) {
                    activity.intentToMainActivity()
                }
            }
        }
    }

    private fun intentToMainActivity() {
        if (!isPermissionEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
                startActivityForResult(intent, PERMISSION_REQUEST_WRITE_SETTINGS)
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_SETTINGS),
                    PERMISSION_REQUEST_WRITE_SETTINGS
                )
            }
        } else {
            splashMeetDaysTv.tag = null
            splashBirthDaysTv.tag = null

            val intent = Intent()
            intent.setClass(this@SplashActivity, MediaPlayerActivity::class.java)

            val channelsBeans = MyApplication.get().getVideoData(this@SplashActivity)
            val lastId = MyApplication.get().getLastId(this@SplashActivity)
            var lastUrl = MyApplication.get().getLastUrl(this@SplashActivity)
            if (lastUrl == "" && channelsBeans.isNotEmpty()
                && channelsBeans[0].url1!!.isNotEmpty()) {
                lastUrl = channelsBeans[0].url1
            }
            intent.putExtra(MediaPlayerActivity.EXTRA_ID, lastId)
            intent.putExtra(MediaPlayerActivity.EXTRA_URL, lastUrl)

            startActivity(intent)

            finish()
            overridePendingTransition(R.anim.fade_in, R.anim.no_change)
        }
    }

    private fun isPermissionEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(this)
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_SETTINGS -> {
                intentToMainActivity()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PERMISSION_REQUEST_WRITE_SETTINGS -> {
                intentToMainActivity()
            }
        }
    }

    companion object {

        private const val HANDLER_INTENT_MAIN = 0
        private const val HANDLER_DELAY_MILLIS = 1000

        private const val ANIMATION_DURATION = 2000

        const val PERMISSION_REQUEST_WRITE_SETTINGS = 0
    }

}
