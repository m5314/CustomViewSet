package com.example.moj.customviewset.widgets

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.R.attr.angle
import android.R.attr.radius
import android.R.attr.centerY
import android.R.attr.centerX
import android.util.Log


/**
* Created by moj on 2018/4/4.
*/
class RadarView : View{
    //绘制边框的画笔
    private val mPaint = Paint()
    //绘制文字的画笔
    private val mTextPaint = Paint()
    //绘制占比图形的画笔
    private val mDataPaint = Paint()
    //雷达图数据
    private val mData:ArrayList<Ability> = ArrayList()
    private var mWidth:Int = 0
    private var mHeight:Int = 0
    private var mRadius:Float = 0f
    private var mAngle:Float = 0f

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private fun init(){
        mTextPaint.textSize = 13f
        mTextPaint.style = Paint.Style.FILL_AND_STROKE

        mPaint.color = Color.parseColor("#000000")
        mPaint.style = Paint.Style.STROKE
        //抗锯齿
        mPaint.isAntiAlias =true
        mPaint.strokeWidth = 3f

        mDataPaint.style = Paint.Style.FILL_AND_STROKE
        mDataPaint.color = Color.GREEN

    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        calculateRadiusAndAngle()
    }

    //根据当前雷达的维数计算边长和角度
    private fun calculateRadiusAndAngle(){
        mRadius = (mWidth / mData.size).toFloat() * 2
        mAngle = (2*Math.PI / mData.size).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
//        //首先移动坐标原点到中心
        canvas?.translate((mWidth/2).toFloat(), (mHeight/2).toFloat())
        drawRadar(canvas)
        drawLine(canvas)
        drawText(canvas)
        drawPercent(canvas)
//        path.close()


    }

    //绘制雷达边框（其实就是正多边形）
    private fun drawRadar(canvas: Canvas?){
//        var temp = mRadius
        for ( i in 1..mData.size) {
            val path = Path()
            val temp = i * (mRadius/mData.size)
            path.moveTo(temp, 0f)
            for (i in 1..mData.size) {
                path.lineTo((temp * Math.cos(mAngle * i.toDouble())).toFloat(), (temp * Math.sin(mAngle * i.toDouble())).toFloat())
            }
            canvas?.drawPath(path,mPaint)
        }
    }

    //绘制线条（其实就是将多边形的定点连起来的线段）
    private fun drawLine(canvas: Canvas?){
        for ( i in 1..mData.size) {
            val path = Path()
            path.lineTo((mRadius * Math.cos(mAngle * i.toDouble())).toFloat(), (mRadius * Math.sin(mAngle * i.toDouble())).toFloat())
            canvas?.drawPath(path,mPaint)
        }
    }

    //绘制各定点对应的文字
    private fun drawText(canvas: Canvas?){
        val fontMetrics = mTextPaint.fontMetrics
        val fontHeight = fontMetrics.descent - fontMetrics.ascent
        for (i in 0 until mData.size) {
            val x = ((mRadius + fontHeight / 2) * Math.cos((mAngle * i).toDouble())).toFloat()
            val y = ((mRadius + fontHeight / 2) * Math.sin((mAngle * i).toDouble())).toFloat()
            if (mAngle * i >= 0 && mAngle * i <= Math.PI / 2) {
                canvas?.drawText(mData[i].name, x, y, mTextPaint)
            } else if (mAngle * i >= 3 * Math.PI / 2 && mAngle * i <= Math.PI * 2) {
                canvas?.drawText(mData[i].name, x, y, mTextPaint)
            } else if (mAngle * i > Math.PI / 2 && mAngle * i <= Math.PI) {
                val dis = mTextPaint.measureText(mData[i].name)//文本长度
                canvas?.drawText(mData[i].name, x - dis, y, mTextPaint)
            } else if (mAngle * i >= Math.PI && mAngle * i < 3 * Math.PI / 2) {
                val dis = mTextPaint.measureText(mData[i].name)//文本长度
                canvas?.drawText(mData[i].name, x - dis, y, mTextPaint)
            }
        }
    }

    //绘制占比图
    private fun drawPercent(canvas: Canvas?){
        val path = Path()
        mDataPaint.alpha = 255
        for (i in 0 until mData.size){
//            val path = Path()
            val x = (mRadius * Math.cos(mAngle * i.toDouble()) * mData[i].point/100).toFloat()
            val y = (mRadius * Math.sin(mAngle * i.toDouble()) * mData[i].point/100).toFloat()
            if(i==0){
                path.moveTo(x, y)
            }else {
                path.lineTo(x, y)
            }
            canvas?.drawCircle(x,y,5f,mDataPaint)
        }
        mDataPaint.alpha = 127
        canvas?.drawPath(path,mDataPaint)
    }

    fun setData(data:ArrayList<Ability>){
        if(data.size < 4){
            throw Exception("至少需要设置4个数据才能绘制雷达图！") as Throwable
        }
        Log.i("RadarView","size:"+data.size)
        mData.clear()
        mData.addAll(data)
        if(mWidth>0){
            calculateRadiusAndAngle()
        }
        invalidate()
    }

    data class Ability(var name:String,var point:Double)
}