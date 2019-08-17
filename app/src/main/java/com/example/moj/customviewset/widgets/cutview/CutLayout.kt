package com.example.moj.customviewset.widgets.cutview

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.os.Environment
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import com.example.moj.customviewset.R

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

/**
 * @author : moj
 * @date : 2019/8/15
 * @description : 裁剪视图，包含图像和裁剪框
 */
class CutLayout : FrameLayout{

    /**
     * 图像显示View
     */
    private var mImageView: ImageView? = null
    /**
     * 裁剪框
     */
    private var mCmCutFrameViewView: CutFrameView? = null
    /**
     * 拖拽状态
     */
    private var mState: DRAG_STATE? = null
    /**
     * 两个手指初始距离
     */
    private var mDistance: Float = 0.toFloat()
    /**
     * 记录单个手指按下时的x坐标
     */
    private var mDownX: Float = 0.toFloat()
    /**
     * 记录单个手指按下时的y坐标
     */
    private var mDownY: Float = 0.toFloat()
    /**
     * 记录手指按下时的image矩阵
     */
    private var mMatrix = Matrix()
    /**
     * 临时记录image矩阵
     */
    private val mTemp = Matrix()

    private val mMid = PointF()

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        init(context)
    }

    private fun init(context: Context) {
        mImageView = ImageView(context)
        //如果要用矩阵操控图像，必须设置ImageView的scaleType为matrix
        mImageView!!.scaleType = ImageView.ScaleType.MATRIX

        mCmCutFrameViewView = CutFrameView(context)
        addView(mImageView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        addView(mCmCutFrameViewView, LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    }

    fun setImageResource(id:Int){
        mImageView?.apply {
            imageMatrix = Matrix()
            setImageResource(id)
        }

    }

    fun setIamgeBitmap(b:Bitmap){
        mImageView?.apply {
            imageMatrix = Matrix()
            setImageBitmap(b)
        }

    }

    /**
     * 实现centerCrop效果
     */
    private fun centerCrop() {

        if(mImageView!!.drawable == null){
            return
        }

        mMatrix = mImageView!!.imageMatrix

        val vw = mImageView!!.width
        val vh = mImageView!!.height
        val dw = mImageView!!.drawable.intrinsicWidth
        val dh = mImageView!!.drawable.intrinsicHeight

        //图像需要放大的倍数
        var scale = 1f
        //图像需要平移的x，y分量
        var dx = 0
        var dy = 0

        //判断宽高比，取长边缩放
        //由于是先放大在平移，所以平移要按放大后的宽高值来算
        //这里用乘法的好处是：不用担心分母为0导致的异常
        if (vw * dh > dw * vh) {
            scale = vh.toFloat() / dh
            dx = ((vw - dw * scale) * 0.5f).toInt()
        } else {
            scale = vw.toFloat() / dw
            dy = ((vh - dh * scale) * 0.5f).toInt()
        }

        mMatrix.postScale(scale, scale)
        mMatrix.postTranslate(dx.toFloat(), dy.toFloat())
        mImageView!!.imageMatrix = mMatrix
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        //layout后能准确获取view和drawable的宽高
        centerCrop()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        //多点触控必须用getActionMasked获取事件类型
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                mState = DRAG_STATE.STATE_DRAG
                mDownX = event.x
                mDownY = event.y

                mTemp.set(mMatrix)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                //多点触控为缩放状态
                if (event.pointerCount == 2) {
                    //先获取两指间的中点坐标，以这个中点坐标为中心进行缩放
                    midPoint(mMid, event)
                    //获取初始状态下两指间距离，以此作为缩放倍数的依据
                    mDistance = spacing(event)
                    //重置mTemp，目的是解决在拖拽状态下变换为缩放状态时，图像位置闪烁的问题
                    mTemp.set(mMatrix)
                    mState = DRAG_STATE.STATE_ZOOM
                }
            }
            MotionEvent.ACTION_MOVE -> if (mState == DRAG_STATE.STATE_ZOOM) {
                val r = spacing(event) / mDistance
                mMatrix.set(mTemp)
                mMatrix.postScale(r, r, mMid.x, mMid.y)
            } else if (mState == DRAG_STATE.STATE_DRAG) {
                mMatrix.set(mTemp)
                mMatrix.postTranslate(event.x - mDownX, event.y - mDownY)
            }
            MotionEvent.ACTION_POINTER_UP -> if (event.pointerCount <= 2) {
                //只剩一个手指时，要变为拖拽状态，同时要重置mTemp， 不然会导致缩放效果消失
                mState = DRAG_STATE.STATE_DRAG
                mTemp.set(mMatrix)
            }
            MotionEvent.ACTION_UP -> mState = DRAG_STATE.STATE_IDLE
            MotionEvent.ACTION_CANCEL -> mState = DRAG_STATE.STATE_IDLE
            else -> {
            }
        }

        mImageView!!.imageMatrix = mMatrix
        return true
    }

    /**
     * 求两点间距离
     *
     * @param event 事件
     * @return
     */
    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt((x * x + y * y).toDouble()).toFloat()
    }

    /**
     * 求两点中间坐标
     *
     * @param point 结果
     * @param event 事件
     */
    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point.set(x / 2, y / 2)
    }

    /**
     * 裁剪图片
     */
    fun clip(activity: Activity) {
        //暂时不使用PixelCopy方法，因为这种方法有个问题是，会把裁剪框也包含进图片里


        //        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

        //            int[] location = new int[2];
        //            this.getLocationInWindow(location);
        //
        //            final Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888, true);
        //            PixelCopy.request(activity.getWindow(), new Rect(location[0], location[1],
        //                    location[0] + getWidth(), location[1] + getHeight()), bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
        //                @Override
        //                public void onPixelCopyFinished(int copyResult) {
        //                    if(copyResult==PixelCopy.SUCCESS){
        //                        saveImage(bitmap);
        //                    }else{
        //                        Toast.makeText(getContext(), "clip error.", Toast.LENGTH_SHORT).show();
        //                    }
        //                }
        //            }, new Handler());
        //        }else{
        mImageView!!.isDrawingCacheEnabled = true
        val bitmap = mImageView!!.drawingCache
        if (bitmap != null) {
            saveImage(bitmap)
            mImageView!!.destroyDrawingCache()
        } else {
            Toast.makeText(context, "clip error.", Toast.LENGTH_SHORT).show()
        }
        //        }
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap 图片数据
     */
    private fun saveImage(bitmap: Bitmap) {
        Thread(Runnable {
            val rect = mCmCutFrameViewView!!.clipRect

            val file = File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis().toString() + ".png")

            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(file)


                val b = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(),
                        rect.height())

                val c = toRoundBitmap(b)

                val matrix = Matrix()
                matrix.postScale(mCmCutFrameViewView!!.clipWidth * 1.0f / c.width,
                        mCmCutFrameViewView!!.clipHeight * 1.0f / c.height)

                val d = Bitmap.createBitmap(c, 0, 0, c.width, c.height, matrix, false)

                d.compress(Bitmap.CompressFormat.PNG, 90, out)

                b.recycle()
                c.recycle()
                d.recycle()
                Toast.makeText(context, "clip success.", Toast.LENGTH_SHORT).show()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } finally {
                try {
                    out!!.flush()
                    out.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }).start()

    }

    /**
     * Bitmap切成圆形显示    
     */
    private fun toRoundBitmap(bitmap: Bitmap): Bitmap {
        //圆形图片宽高
        val width = bitmap.width
        val height = bitmap.height
        //正方形的边长
        var r = 0
        //取最短边做边长
        if (width > height) {
            r = height
        } else {
            r = width
        }
        //构建一个bitmap
        val backgroundBmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        //new一个Canvas，在backgroundBmp上画图
        val canvas = Canvas(backgroundBmp)
        val paint = Paint()
        //设置边缘光滑，去掉锯齿
        paint.isAntiAlias = true
        //宽高相等，即正方形
        val rect = RectF(0f, 0f, r.toFloat(), r.toFloat())
        //通过制定的rect画一个圆角矩形，当圆角X轴方向的半径等于Y轴方向的半径时，
        //且都等于r/2时，画出来的圆角矩形就是圆形
        canvas.drawRoundRect(rect, (r / 2).toFloat(), (r / 2).toFloat(), paint)
        //设置当两个图形相交时的模式，SRC_IN为取SRC图形相交的部分，多余的将被去掉
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        //canvas将bitmap画在backgroundBmp上
        canvas.drawBitmap(bitmap, null, rect, paint)
        //返回已经绘画好的backgroundBmp
        return backgroundBmp
    }

    /**
     * 状态枚举
     */
    private enum class DRAG_STATE {
        /**
         * 静止状态
         */
        STATE_IDLE,
        /**
         * 拖拽状态
         */
        STATE_DRAG,
        /**
         * 缩放状态
         */
        STATE_ZOOM
    }
}
