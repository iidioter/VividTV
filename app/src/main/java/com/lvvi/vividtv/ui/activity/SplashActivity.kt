package com.lvvi.vividtv.ui.activity

import android.Manifest
import android.animation.Animator
import android.animation.ValueAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.provider.Settings
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.lvvi.vividtv.R
import com.lvvi.vividtv.utils.Constant
import com.lvvi.vividtv.utils.MyApplication
import com.lvvi.vividtv.utils.MySharePreferences
import com.lvvi.vividtv.utils.Utils
import java.lang.ref.WeakReference

class SplashActivity : Activity() {

    private lateinit var splashMeetDaysTv: TextView
    private lateinit var splashBirthDaysTv: TextView

    private lateinit var handler: MyHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_view)
        initView()
        startAnimation()
    }

    private fun startAnimation() {
        val animator = ValueAnimator.ofInt(0, Utils.meetDays)

        animator.duration = ANIMATION_DURATION.toLong()

        animator.addUpdateListener { animation ->
            splashMeetDaysTv.text = animation.animatedValue.toString()

            if (animation.animatedValue as Int <= Utils.birthDays) {
                splashBirthDaysTv.text = animation.animatedValue.toString()
            }
        }

        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                handler.sendEmptyMessageDelayed(HANDLER_ANIMATION_ENDED, HANDLER_DELAY_MILLIS.toLong())
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
                HANDLER_ANIMATION_ENDED -> {
                    activity.animationEnded()
                }
            }
        }
    }

    private fun animationEnded() {
        //phone
        if (MySharePreferences.getInstance(this).getString(Constant.NEED_CHECK_PERMISSION).isNullOrEmpty()) {
            checkPermission()
        } else {
            intentToMainActivity()
        }
    }

    private fun checkPermission() {
        if (!isPermissionEnabled()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showDialog()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_SETTINGS),
                    PERMISSION_REQUEST_WRITE_SETTINGS
                )
            }
        } else {
            intentToMainActivity()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun showDialog() {
        MySharePreferences.getInstance(this).putString(Constant.NEED_CHECK_PERMISSION, "0")

        val builder = AlertDialog.Builder(this)

        builder.setTitle(getString(R.string.dialog_title))
        builder.setMessage(getString(R.string.dialog_message))

        builder.setNegativeButton(getString(R.string.dialog_negative_button)) { p0, _ ->
            p0?.dismiss()
            Toast.makeText(this, getString(R.string.permission_tip), Toast.LENGTH_LONG).show()
            intentToMainActivity()
        }

        builder.setPositiveButton(getString(R.string.dialog_positive_button)) { p0, _ ->
            p0?.dismiss()
            intentToSettings()
        }

        builder.setCancelable(true)

        val dialog = builder.create()
        dialog.show()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun intentToSettings() {
        val intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS)
        intent.data = Uri.parse("package:$packageName")
        startActivityForResult(intent, PERMISSION_REQUEST_WRITE_SETTINGS)
    }

    private fun intentToMainActivity() {
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

    private fun isPermissionEnabled(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.System.canWrite(this)
        } else {
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS) ==
                    PackageManager.PERMISSION_GRANTED
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

        private const val HANDLER_ANIMATION_ENDED = 0
        private const val HANDLER_DELAY_MILLIS = 1000

        private const val ANIMATION_DURATION = 2000

        const val PERMISSION_REQUEST_WRITE_SETTINGS = 0
    }

}
