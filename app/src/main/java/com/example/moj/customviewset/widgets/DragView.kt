package com.example.moj.customviewset.widgets

import android.content.Context
import android.support.v4.widget.ViewDragHelper
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout

/**
 * Created by moj on 2018/4/13.
 * 测试DragHelper
 */
class DragView : RelativeLayout{

    private var mInitX:Int = 0
    private var mInitY:Int = 0
    private lateinit var mDragHelper:ViewDragHelper

    constructor(context: Context?) : this(context,null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs,0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
      init()
    }

    private fun init(){
        mDragHelper = ViewDragHelper.create(this,1f,object : ViewDragHelper.Callback(){

            //确定要拖拽的子view
            override fun tryCaptureView(child: View?, pointerId: Int): Boolean {
                return true
            }

            //控制view垂直移动的范围
            override fun clampViewPositionVertical(child: View?, top: Int, dy: Int): Int {
                return top
            }

            //控制view水平移动的范围
            override fun clampViewPositionHorizontal(child: View?, left: Int, dx: Int): Int {
                return left
            }

            //拖拽结束时调用
            override fun onViewReleased(releasedChild: View?, xvel: Float, yvel: Float) {
                super.onViewReleased(releasedChild, xvel, yvel)
                mDragHelper.settleCapturedViewAt(mInitX,mInitY)
                invalidate()
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return mDragHelper.shouldInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        mDragHelper.processTouchEvent(event)
        return true
    }

    //要实现弹性滑动，需要重写此方法
    override fun computeScroll() {
        if(mDragHelper.continueSettling(true)){
            invalidate()
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        mInitX = getChildAt(0).left
        mInitY = getChildAt(0).top
    }
}