package com.snail.commons.utils

import androidx.annotation.StringRes
import android.view.View
import android.widget.Toast
import com.snail.commons.AppHolder
import java.lang.ref.WeakReference


/**
 * 时间: 2017/10/10 15:10
 * 作者: 曾繁盛
 * 邮箱: 43068145@qq.com
 * 功能: 单例Toast工具类
 */

object ToastUtils {
    private var weakRef: WeakReference<View>? = null

    private object Holder {
        internal var toast = Toast.makeText(AppHolder.context, "", Toast.LENGTH_SHORT)
    }
    
    fun reset() {
        weakRef = null
        Holder.toast.cancel()
        Holder.toast = Toast.makeText(AppHolder.context, "", Toast.LENGTH_SHORT)
    }
    
    fun cancel() {
        Holder.toast.cancel()
    }

    /**
     * Set the margins of the view.
     *
     * @param horizontalMargin The horizontal margin, in percentage of the
     * container width, between the container's edges and the
     * notification
     * @param verticalMargin The vertical margin, in percentage of the
     * container height, between the container's edges and the
     * notification
     */
    fun setMargin(horizontalMargin: Float, verticalMargin: Float) {
        Holder.toast.setMargin(horizontalMargin, verticalMargin)
    }

    /**
     * Set the location at which the notification should appear on the screen.
     * @see android.view.Gravity
     *
     * @see Toast.getGravity
     */
    fun setGravity(gravity: Int, xOffset: Int, yOffset: Int) {
        Holder.toast.setGravity(gravity, xOffset, yOffset)
    }

    /**
     * Set the view to show.
     */
    fun setView(view: View) {
        weakRef = WeakReference(view)
        Holder.toast.view = view
    }

    /**
     * 显示时长为[Toast.LENGTH_SHORT]的Toast
     */
    fun showShort() {
        AppHolder.postToMainThread(Runnable { show("", Toast.LENGTH_SHORT) })
    }
    
    /**
     * 显示时长为[Toast.LENGTH_SHORT]的Toast
     */
    fun showShort(text: CharSequence) {
        AppHolder.postToMainThread(Runnable { show(text, Toast.LENGTH_SHORT) })
    }

    /**
     * 显示时长为[Toast.LENGTH_SHORT]的Toast
     */
    fun showShort(@StringRes resId: Int) {
        AppHolder.postToMainThread(Runnable { show(AppHolder.context.getText(resId), Toast.LENGTH_SHORT) })
    }

    /**
     * 显示时长为[Toast.LENGTH_LONG]的Toast
     */
    fun showLong() {
        AppHolder.postToMainThread(Runnable { show("", Toast.LENGTH_LONG) })
    }
    
    /**
     * 显示时长为[Toast.LENGTH_LONG]的Toast
     */
    fun showLong(text: CharSequence) {
        AppHolder.postToMainThread(Runnable { show(text, Toast.LENGTH_LONG) })
    }

    /**
     * 显示时长为[Toast.LENGTH_LONG]的Toast
     */
    fun showLong(@StringRes resId: Int) {
        AppHolder.postToMainThread(Runnable { show(AppHolder.context.getText(resId), Toast.LENGTH_LONG) })
    }
    
    private fun show(text: CharSequence, duration: Int) {
        Holder.toast.cancel()
        val toast = Toast.makeText(AppHolder.context, "", Toast.LENGTH_SHORT)
        toast.duration = duration
        toast.setText(text)
        if (weakRef != null && weakRef!!.get() != null) {
            toast.view = weakRef!!.get()
        }
        toast.setGravity(Holder.toast.gravity, Holder.toast.xOffset, Holder.toast.yOffset)
        toast.setMargin(Holder.toast.horizontalMargin, Holder.toast.verticalMargin)
        toast.show()
        Holder.toast = toast
    }
}
