package com.lvvi.vividtv.ui.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lvvi.vividtv.R
import com.lvvi.vividtv.model.VideoDataModelNew


class ChannelNameAdapter(private val context: Context) : BaseAdapter() {
    private var channelsBeans: List<VideoDataModelNew>? = null
    private var currId: String? = null
    private var checkedPosition: Int = 0

    fun setData(channelsBeans: List<VideoDataModelNew>) {
        this.channelsBeans = channelsBeans
        notifyDataSetChanged()
    }

    fun setCurrId(currId: String) {
        this.currId = currId
    }

    fun setCheckedPosition(checkedPosition: Int) {
        this.checkedPosition = checkedPosition
    }

    override fun getCount(): Int {
        return if (channelsBeans == null) 0 else channelsBeans!!.size
    }

    override fun getItem(i: Int): Any? {
        return null
    }

    override fun getItemId(i: Int): Long {
        return 0
    }

    override fun getView(i: Int, view: View?, viewGroup: ViewGroup): View {
        var mView = view
        val viewHolder: ViewHolder
        if (mView == null) {
            mView = View.inflate(context, R.layout.item_channel_name, null)
            viewHolder = ViewHolder()
            viewHolder.mainLl = mView!!.findViewById(R.id.main_ll)
            viewHolder.nameTv = mView.findViewById(R.id.name_tv)
            viewHolder.imageIv = mView.findViewById(R.id.image_iv)
            viewHolder.titleTv = mView.findViewById(R.id.title_tv)
            mView.tag = viewHolder
        } else {
            viewHolder = mView.tag as ViewHolder
        }

        if (checkedPosition == i) {
            viewHolder.mainLl!!.setBackgroundColor(ContextCompat.getColor(context, R.color.info_bg_color))
        } else {
            viewHolder.mainLl!!.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent))
        }

        viewHolder.nameTv!!.text = channelsBeans!![i].name

        if (currId != null) {
            if (currId == channelsBeans!![i].id) {
                viewHolder.imageIv!!.visibility = View.VISIBLE
            } else {
                viewHolder.imageIv!!.visibility = View.GONE
            }
        } else {
            viewHolder.imageIv!!.visibility = View.GONE
        }

        if (channelsBeans!![i].title == null || channelsBeans!![i].title == "") {
            channelsBeans!![i].title = channelsBeans!![i].name
        }
        viewHolder.titleTv!!.text = channelsBeans!![i].title

        return mView
    }

    internal class ViewHolder {
        var mainLl: LinearLayout? = null
        var nameTv: TextView? = null
        var imageIv: ImageView? = null
        var titleTv: TextView? = null
    }
}
