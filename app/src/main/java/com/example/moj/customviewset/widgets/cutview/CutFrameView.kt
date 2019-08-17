package com.example.moj.customviewset.widgets.cutview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.example.moj.customviewset.ScreenUtil

/**
 * @author : moj
 * @date : 2019/8/15
 * @description : 裁剪框
 */
class CutFrameView : View {

    private var mPaint: Paint? = null

    private var radius: Int = 0

    /**
     * 获取裁剪区域的Rect
     *
     * @return
     */
    val clipRect: Rect
        get() {
            val rect = Rect()

            rect.left = this.width / 2 - radius
            rect.right = this.width / 2 + radius
            rect.top = this.height / 2 - radius
            rect.bottom = this.height / 2 + radius

            return rect
        }

    val clipWidth: Int
        get() = radius * 2

    val clipHeight: Int
        get() = radius * 2

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        init(context)
    }

    private fun init(context: Context) {
        mPaint = Paint()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null, Canvas.ALL_SAVE_FLAG)

        canvas.drawColor(Color.parseColor("#a8000000"))

        mPaint!!.color = Color.parseColor("#00ffffff")
        mPaint!!.style = Paint.Style.FILL
        mPaint!!.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)

        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius.toFloat(), mPaint!!)

        mPaint!!.xfermode = null
        val w = ScreenUtil.dip2px(context, 5f)
        mPaint!!.strokeWidth = w.toFloat()
        mPaint!!.style = Paint.Style.STROKE
        mPaint!!.color = Color.WHITE
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius.toFloat(),
                mPaint!!)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        radius = width / 3
    }
}
