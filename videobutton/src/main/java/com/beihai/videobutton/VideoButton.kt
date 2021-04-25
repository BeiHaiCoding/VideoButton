package com.beihai.videobutton

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Toast
import java.math.BigDecimal


class VideoButton @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    /**
     * 当前的按钮类型：
     *   1 : 拍照
     *   2 : 录像
     */
    private var mCurrentButtonType = 1

    /**
     * 内圈画笔
     */
    private var mInnerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 外圈画笔
     */
    private var mOuterCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 内部小方形画笔
     */
    private var mRectFPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 进度条画笔
     */
    private var mProgressBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 计时器画笔
     */
    private var mTimerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)


    /**
     * 拍照按钮内圈颜色
     */
    private var mInnerCircleColorPhoto = Color.parseColor("#FFFFFF")

    /**
     * 拍照按钮外圈颜色
     */
    private var mOuterCirCleColorPhoto = Color.parseColor("#AAFFFFFF")

    /**
     * 视频按钮内圈颜色
     */
    private var mInnerCircleColorVideo = Color.parseColor("#FF4500")

    /**
     * 视频按钮外圈颜色
     */
    private var mOuterCirCleColorVideo = Color.parseColor("#AAFFFFFF")

    /**
     * 进度条颜色
     */
    private var mProgressBarColor = Color.parseColor("#90EE90")

    /**
     * 计时器文字颜色
     */
    private var mTimerTextColor = Color.parseColor("#FFFFFF")


    /**
     * 计时器文本大小
     */
    private var mTimerTextSize = sp2px(14)

    /**
     * view宽度
     */
    private var mWidth = 300

    /**
     * 计时器文本显示所需要的预留空间
     */
    private var mExtraHeight = (1.5 * mTimerTextSize).toInt()

    /**
     * view高度
     * 高度默认等于 宽度+计时器文本长度+16px(16px是预留空间)
     */
    private var mHeight = mWidth + mExtraHeight

    /**
     * 内圆半径
     */
    private var mInnerCircleRadius = 82f

    /**
     * 外圆半径
     */
    private var mOuterCircleRadius = 105f

    /**
     * 临时内圆半径
     */
    private var mInnerCircleRadiusOld = mInnerCircleRadius

    /**
     * 临时外圆半径
     */
    private var mOuterCircleRadiusOld = mOuterCircleRadius

    /**
     * 进度条宽度
     */
    private var mProgressBarWidth = 18f

    /**
     * 内部小方形尺寸
     */
    private var mInnerRectFSize = 0f

    /**
     * 内部小方形（视频按钮动画后中心的小方形）
     */
    private var mInnerRectF: RectF = RectF()

    /**
     * 缩放动画默认时间300ms
     */
    private val mScaleAnimDuration: Long = 300

    /**
     * 进度条默认时间15s,单位秒
     */
    private var mProgressBarDuration: Int = 15

    /**
     * 录制视频的最小时间:3s 单位为秒
     */
    private var mMinRecordTime = 3

    /**
     * mProgressWidth进度条当前进度
     */
    private var mCurrentProgress = 0f

    /**
     * 是否超过最小录制时间
     */
    private var isOverMinRecordTime = false

    /**
     * 是否在录像
     */
    private var isRecording = false

    /**
     * 是否在做缩放动画
     */
    private var isScaling = false

    /**
     * mTimeText时间文本
     */
    private var mTimerText = "0秒"

    /**
     * 动画监听器
     */
    private var mListener: OnRecordListener? = null

    /**
     * 进度条动画
     */
    private var mProgressBarAnim: ObjectAnimator? = null

    /**
     * 缩放动画
     */
    private var mScaleAnim: ObjectAnimator? = null


    init {
        val typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.VideoButton)
        mCurrentButtonType =
            typedArray.getInt(R.styleable.VideoButton_buttonType, mCurrentButtonType)
        mInnerCircleColorPhoto = typedArray.getColor(
            R.styleable.VideoButton_innerCircleColorPhoto,
            mInnerCircleColorPhoto
        )
        mOuterCirCleColorPhoto = typedArray.getColor(
            R.styleable.VideoButton_outerCircleColorPhoto,
            mOuterCirCleColorPhoto
        )
        mInnerCircleColorVideo = typedArray.getColor(
            R.styleable.VideoButton_innerCircleColorVideo,
            mInnerCircleColorVideo
        )
        mOuterCirCleColorVideo = typedArray.getColor(
            R.styleable.VideoButton_outerCircleColorVideo,
            mOuterCirCleColorVideo
        )
        mProgressBarColor = typedArray.getColor(
            R.styleable.VideoButton_progressBarColor,
            mProgressBarColor
        )
        mTimerTextColor = typedArray.getColor(
            R.styleable.VideoButton_timerTextColor,
            mTimerTextColor
        )
        mProgressBarDuration = typedArray.getInt(
            R.styleable.VideoButton_progressBarDuration,
            mProgressBarDuration
        )
        mMinRecordTime = typedArray.getInt(
            R.styleable.VideoButton_recordMinTime,
            mMinRecordTime
        )
        typedArray.recycle()

        mInnerCirclePaint.color = mInnerCircleColorPhoto
        mOuterCirclePaint.color = mOuterCirCleColorPhoto
        mOuterCirclePaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OVER)
        mProgressBarPaint.color = mProgressBarColor

        //设置计时器文本画笔，位置居中
        mTimerTextPaint.color = mTimerTextColor
        mTimerTextPaint.textSize = mTimerTextSize
        mTimerTextPaint.textAlign = Paint.Align.CENTER

    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth = resolveSizeAndState(mWidth, widthMeasureSpec, 0)
        mHeight = resolveSizeAndState(mHeight, heightMeasureSpec, 0)
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mOuterCircleRadius = (mHeight - mExtraHeight) / 2 * 0.7f
        mInnerCircleRadius = (mHeight - mExtraHeight) / 2 * 0.55f
        mProgressBarWidth = (mOuterCircleRadius - mInnerCircleRadius) / 2
        mInnerRectFSize = mInnerCircleRadius * 0.55f

        //设置进度条画笔
        mProgressBarPaint.strokeWidth = mProgressBarWidth
        mProgressBarPaint.style = Paint.Style.STROKE
        mProgressBarPaint.strokeCap = Paint.Cap.ROUND

    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        when (mCurrentButtonType) {
            1 -> {
                val saved = canvas.saveLayer(null, null)
                mInnerCirclePaint.color = mInnerCircleColorPhoto
                mOuterCirclePaint.color = mOuterCirCleColorPhoto
                canvas.drawCircle(
                    mWidth / 2f,
                    (mHeight - mExtraHeight) / 2f + mExtraHeight,
                    mInnerCircleRadius,
                    mInnerCirclePaint
                )
                canvas.drawCircle(
                    mWidth / 2f,
                    (mHeight - mExtraHeight) / 2f + mExtraHeight,
                    mOuterCircleRadius,
                    mOuterCirclePaint
                )
                canvas.restoreToCount(saved)
            }
            2 -> {
                val saved = canvas.saveLayer(null, null)
                mInnerCirclePaint.color = mInnerCircleColorVideo
                mOuterCirclePaint.color = mOuterCirCleColorVideo
                mRectFPaint.color = mInnerCircleColorVideo

                //画内圆
                canvas.drawCircle(
                    mWidth / 2f,
                    (mHeight - mExtraHeight) / 2f + mExtraHeight,
                    mInnerCircleRadius,
                    mInnerCirclePaint
                )

                //设置方形尺寸
                mInnerRectF.set(
                    mWidth / 2f - mInnerRectFSize / 2f,
                    (mHeight - mExtraHeight) / 2f + mExtraHeight - mInnerRectFSize / 2f,
                    mWidth / 2f + mInnerRectFSize / 2f,
                    (mHeight - mExtraHeight) / 2f + mExtraHeight + mInnerRectFSize / 2f
                )

                //画内部小正方形
                canvas.drawRoundRect(
                    mInnerRectF,
                    mInnerRectFSize / 4,
                    mInnerRectFSize / 4,
                    mRectFPaint
                )

                //画外圆
                canvas.drawCircle(
                    mWidth / 2f,
                    (mHeight - mExtraHeight) / 2f + mExtraHeight,
                    mOuterCircleRadius,
                    mOuterCirclePaint
                )

                //开启动画
                if (isRecording) {
                    drawProgress(canvas)
                    drawTimerText(canvas)
                }

                canvas.restoreToCount(saved)
            }
            else -> {
                throw IllegalArgumentException("View type is illegal")
            }
        }

    }


    /**
     * 绘制时间文本
     * @param canvas
     * mRectF.centerX() - mInnerRadius - mRectSize / 2-16
     */
    private fun drawTimerText(canvas: Canvas) {
        canvas.drawText(
            mTimerText,
            mWidth / 2.0f,
            (mHeight / 2.0f + mExtraHeight) - mOuterCircleRadius - mTimerTextSize,
            mTimerTextPaint
        )
    }

    /**
     * 绘制圆形进度
     * @param canvas
     */
    private fun drawProgress(canvas: Canvas) {
        //用于定义的圆弧的形状和大小的界限
        val oval = RectF(
            mWidth / 2 - (mOuterCircleRadius - mProgressBarWidth / 2),
            (mHeight - mExtraHeight) / 2f + mExtraHeight - (mOuterCircleRadius - mProgressBarWidth / 2),
            mWidth / 2 + (mOuterCircleRadius - mProgressBarWidth / 2),
            (mHeight - mExtraHeight) / 2f + mExtraHeight + (mOuterCircleRadius - mProgressBarWidth / 2)
        )
        //根据进度画圆弧
        canvas.drawArc(oval, -90f, mCurrentProgress, false, mProgressBarPaint)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mCurrentButtonType == 1) {
            if (event?.action == MotionEvent.ACTION_UP) {
                performClick()
            }
        } else if (mCurrentButtonType == 2) {
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (!isRecording) {
                        //判断点击是否在view的范围内
                        return event.x <= mWidth / 2 + mOuterCircleRadius
                                && event.x >= mWidth / 2 - mOuterCircleRadius
                                && event.y <= mHeight / 2 + mExtraHeight + mOuterCircleRadius
                                && event.y >= mHeight / 2 + mExtraHeight - mOuterCircleRadius
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (isScaling) {
                        return true
                    }
                    if (!isRecording) {
                        //判断抬起手指后是否在view的范围内
                        if (event.x <= mWidth / 2 + mOuterCircleRadius
                            && event.x >= mWidth / 2 - mOuterCircleRadius
                            && event.y <= mHeight / 2 + mExtraHeight + mOuterCircleRadius
                            && event.y >= mHeight / 2 + mExtraHeight - mOuterCircleRadius
                        ) {
                            performClick()
                            isRecording = true
                            mCurrentProgress = 0f
                            mTimerText = "0秒"
                            isOverMinRecordTime = false
                            startScaleAnim()
                        }
                    } else {
                        if (event.x <= mWidth
                            && event.x >= 0
                            && event.y <= mHeight
                            && event.y >= 0
                        ) {
                            if (isOverMinRecordTime) {
                                endProgressAnim()
                            } else {
                                Toast.makeText(context, "录制时长不能小于3秒", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                MotionEvent.ACTION_CANCEL -> {

                }

            }
        }
        return true
    }

    private fun startScaleAnim() {
        mInnerCircleRadiusOld = mInnerCircleRadius
        mOuterCircleRadiusOld = mOuterCircleRadius
        val holder1 = PropertyValuesHolder.ofFloat(
            "mInnerCircleRadius",
            mInnerCircleRadius,
            0f
        )
        val holder2 = PropertyValuesHolder.ofFloat(
            "mOuterCircleRadius",
            mOuterCircleRadius,
            mOuterCircleRadius + (mHeight - mExtraHeight) / 2 * 0.3f
        )
        mScaleAnim = ObjectAnimator.ofPropertyValuesHolder(this, holder1, holder2)

        mScaleAnim?.duration = mScaleAnimDuration
        mScaleAnim?.interpolator = LinearInterpolator()
        mScaleAnim?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                isScaling = true
            }

            override fun onAnimationEnd(animation: Animator?) {
                isScaling = false
                startProgressAnim()
            }

            override fun onAnimationCancel(animation: Animator?) {
                isScaling = false
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        mScaleAnim?.start()

    }

    private fun endScaleAnim() {
        val holder1 =
            PropertyValuesHolder.ofFloat(
                "mInnerCircleRadius",
                mInnerCircleRadius,
                mInnerCircleRadiusOld
            )
        val holder2 =
            PropertyValuesHolder.ofFloat(
                "mOuterCircleRadius",
                mOuterCircleRadius,
                mOuterCircleRadiusOld
            )
        val anim = ObjectAnimator.ofPropertyValuesHolder(this, holder1, holder2)
        anim.duration = mScaleAnimDuration
        anim.interpolator = LinearInterpolator()
        anim.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
                isScaling = true
            }

            override fun onAnimationEnd(animation: Animator?) {
                isScaling = false
            }

            override fun onAnimationCancel(animation: Animator?) {
                isScaling = false
            }

            override fun onAnimationRepeat(animation: Animator?) {
            }
        })
        anim.start()
    }

    private fun startProgressAnim() {
        mProgressBarAnim = ObjectAnimator.ofFloat(this, "mCurrentProgress", 0f, 360f)
        mProgressBarAnim?.duration = mProgressBarDuration*1000L
        mProgressBarAnim?.interpolator = LinearInterpolator()
        val updateListener = ValueAnimator.AnimatorUpdateListener {
            setMTimerText(
                BigDecimal(it.currentPlayTime / 1000.00).setScale(1, BigDecimal.ROUND_DOWN)
                    .toString() + "秒"
            )
            if (it.currentPlayTime / 1000.00 >= mMinRecordTime) {
                isOverMinRecordTime = true
            }
        }
        mProgressBarAnim?.addUpdateListener(updateListener)

        mProgressBarAnim?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                isRecording = false
                mListener?.onTimeFinished()
                endScaleAnim()
            }

            override fun onAnimationCancel(animation: Animator?) {

            }

            override fun onAnimationRepeat(animation: Animator?) {

            }
        })
        mProgressBarAnim?.start()
    }

    fun addOnRecordListener(listener: OnRecordListener) {
        this.mListener = listener
    }

    private fun endProgressAnim() {
        mProgressBarAnim?.cancel()
    }

    private fun setMCurrentProgress(size: Float) {
        this.mCurrentProgress = size
        invalidate()
    }

    private fun setMInnerCircleRadius(size: Float) {
        this.mInnerCircleRadius = size
        invalidate()
    }

    private fun setMTimerText(text: String) {
        this.mTimerText = text
        invalidate()
    }

    private fun setMOuterCircleRadius(size: Float) {
        this.mOuterCircleRadius = size
        invalidate()
    }

    private fun setMCurrentButtonType(type: Int) {
        this.mCurrentButtonType = type
        invalidate()
    }

    interface OnRecordListener {
        fun onTimeFinished()
    }


    private fun sp2px(sp: Int) =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            Resources.getSystem().displayMetrics
        )

    private fun dp2px(dp: Int) =
        TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            Resources.getSystem().displayMetrics
        )


}