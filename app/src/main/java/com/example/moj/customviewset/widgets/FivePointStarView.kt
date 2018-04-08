package com.example.moj.customviewset.widgets

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View

/**
 * Created by moj on 2018/4/4.
 */
class FivePointStarView : View {
    private val mPaint: Paint = Paint()

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private fun init(){
        mPaint.color = Color.parseColor("#000000")
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeWidth = 10f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(600,400)
    }

    override fun onDraw(canvas: Canvas?) {
        var path = Path()
        path.moveTo(300f,0f)
        path.lineTo(100f,400f)
        path.lineTo(600f,150f)
        path.lineTo(0f,150f)
        path.lineTo(500f,400f)
        path.close()
        canvas?.drawPath(path,mPaint)
    }
}