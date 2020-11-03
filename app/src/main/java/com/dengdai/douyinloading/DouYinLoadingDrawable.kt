package com.dengdai.douyinloading

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.view.animation.AccelerateDecelerateInterpolator


class DouYinLoadingDrawable:Drawable(),Animatable {

    private var leftBallPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private  var rightBallPaint:Paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private  var coincideBallPaint:Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var leftBallPath: Path = Path()
    private  var rightBallPath: Path = Path()
    private  var coincideBallPath:Path = Path()

    private var translate = 0f
    private  var scale:Float = 0f

    var mCurrentDirection: Direction

    private var animatorSet: AnimatorSet? = null

    private var mWidth = 0f
    private  var mHeight:Float = 0f
    private var centerX = 0f
    private  var centerY:Float = 0f
    private val radius = 20f


    init {

        leftBallPaint.isAntiAlias = true
        leftBallPaint.color = Color.parseColor("#FF00EEEE")
        leftBallPaint.style = Paint.Style.FILL

        rightBallPaint.isAntiAlias = true
        rightBallPaint.color = Color.parseColor("#FFFF4040")
        rightBallPaint.style = Paint.Style.FILL

        coincideBallPaint.isAntiAlias = true
        coincideBallPaint.color = Color.BLACK
        coincideBallPaint.style = Paint.Style.FILL

        mCurrentDirection = Direction.LEFT

    }



    override fun draw(canvas: Canvas) {

        if (mCurrentDirection == Direction.LEFT) {
            canvas.save()
            leftBallPath.reset()
            leftBallPath.addCircle(
                centerX - radius + translate,
                centerY,
                radius,
                Path.Direction.CCW
            )
            canvas.drawPath(leftBallPath, leftBallPaint)
            canvas.restore()

            canvas.save()
            rightBallPath.reset()
            rightBallPath.addCircle(
                centerX + radius - translate,
                centerY,
                radius * scale,
                Path.Direction.CCW
            )
            canvas.drawPath(rightBallPath, rightBallPaint)
            canvas.restore()

            canvas.save()
            coincideBallPath.reset()
            coincideBallPath.op(leftBallPath, rightBallPath, Path.Op.INTERSECT)
            canvas.drawPath(coincideBallPath, coincideBallPaint)
            canvas.restore()
        } else if (mCurrentDirection == Direction.RIGHT) {
            canvas.save()
            rightBallPath.reset()
            rightBallPath.addCircle(centerX - radius + translate, centerY, 20f, Path.Direction.CCW)
            canvas.drawPath(rightBallPath, rightBallPaint)
            canvas.restore()

            canvas.save()
            leftBallPath.reset()
            leftBallPath.addCircle(
                centerX + radius - translate,
                centerY,
                20 * scale,
                Path.Direction.CCW
            )
            canvas.drawPath(leftBallPath, leftBallPaint)
            canvas.restore()
            canvas.save()
            coincideBallPath.reset()
            coincideBallPath.op(leftBallPath, rightBallPath, Path.Op.INTERSECT)
            canvas.drawPath(coincideBallPath, coincideBallPaint)
            canvas.restore()
        }

    }

    override fun setAlpha(alpha: Int) {}

    override fun setColorFilter(colorFilter: ColorFilter?) {}

    @SuppressLint("WrongConstant")
    override fun getOpacity(): Int {
        return PixelFormat.RGBA_8888
    }

    override fun start() {
        startAnimation()
    }

    override fun stop() {
        if (animatorSet != null && animatorSet!!.isRunning) {
            animatorSet!!.end()
        }
    }

    override fun isRunning(): Boolean {
        return animatorSet!!.isRunning
    }


    override fun onBoundsChange(bounds: Rect?) {
        super.onBoundsChange(bounds)
        mWidth = bounds!!.width().toFloat()
        mHeight = bounds.height().toFloat()
        centerX = mWidth / 2
        centerY = mHeight / 2
    }

    private fun startAnimation() {
        leftAnimation()
    }


    private fun leftAnimation() {
        val translateAnimator = ValueAnimator.ofFloat(0f, 2 * radius)
        translateAnimator.startDelay = 200
        translateAnimator.duration = 350
        translateAnimator.interpolator = AccelerateDecelerateInterpolator()
        translateAnimator.addUpdateListener {
            translate = translateAnimator.animatedValue as Float
            invalidateSelf()
        }
        val scaleAnimator = ValueAnimator.ofFloat(1f, 0.5f, 1f)
        scaleAnimator.startDelay = 200
        scaleAnimator.duration = 350
        scaleAnimator.interpolator = AccelerateDecelerateInterpolator()
        scaleAnimator.addUpdateListener {
            scale = scaleAnimator.animatedValue as Float
            invalidateSelf()
        }
        translateAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mCurrentDirection = if (mCurrentDirection == Direction.LEFT) Direction.RIGHT else Direction.LEFT
                translate = 0f
                leftAnimation()
            }
        })
        animatorSet = AnimatorSet()
        animatorSet!!.playTogether(translateAnimator, scaleAnimator)
        animatorSet!!.start()
    }


    enum class Direction {
        LEFT, RIGHT
    }

}