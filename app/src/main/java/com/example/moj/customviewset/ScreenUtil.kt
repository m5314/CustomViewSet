package com.example.moj.customviewset

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

/**
 * 获取屏幕分辨率
 */
object ScreenUtil {

    /**
     * @param context 上下文
     * @return DisplayMetrics对象
     */
    fun getDisplayMetrics(context: Context): DisplayMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics
    }

    /**
     * 获取屏幕分辨率-宽
     *
     * @param context 上下文
     * @return 宽
     */
    fun getScreenWidth(context: Context): Int {
        val metrics = getDisplayMetrics(context)
        return metrics.widthPixels
    }

    /**
     * 获取屏幕分辨率-高
     *
     * @param context 上下文
     * @return 高
     */
    fun getScreenHeight(context: Context): Int {
        val metrics = getDisplayMetrics(context)
        return metrics.heightPixels
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context 上下文
     * @param dpValue 值
     * @return 转换结果
     */
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context 上下文
     * @param pxValue 值
     * @return 转换结果
     */
    fun px2dip(context: Context, pxValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * sp转px
     *
     * @param context 上下文
     * @param spValue 值
     * @return 转换结果
     */
    fun sp2px(context: Context, spValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (spValue * fontScale + 0.5f).toInt()
    }

    /**
     * px转sp
     *
     * @param context 上下文
     * @param pxValue 值
     * @return 转换结果
     */
    fun px2sp(context: Context, pxValue: Float): Int {
        val fontScale = context.resources.displayMetrics.scaledDensity
        return (pxValue / fontScale + 0.5f).toInt()
    }
}