package com.bernaferrari.emojislider

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.BlurMaskFilter.Blur
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.bernaferrari.emojislider.drawables.TrackDrawable
import kotlin.math.roundToInt


class EmojiSlider @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        const val INITIAL_POSITION = 0.5f
    }

    private val desiredWidth: Int
    private val desiredHeight: Int

    private val starLottie: LottieDrawable


    var progress: Float = INITIAL_POSITION
        set(value) {
            field = value.limitToRange()
            trackDrawable.percentProgress = field
            trackDrawable.invalidateSelf()
            invalidate()
        }

    var thumbProgress: Float = INITIAL_POSITION
        set(value) {
            field = value.limitToRange()
            invalidate()
        }


    private val progressAnimProperty = object : FloatPropertyCompat<Float>("") {
        override fun setValue(p: Float, value: Float) {
            progress = value
        }

        override fun getValue(p: Float) = progress
    }

    private val thumbAnimProperty = object : FloatPropertyCompat<Float>("") {
        override fun setValue(p: Float, value: Float) {
            thumbProgress = value
        }

        override fun getValue(progress: Float) = thumbProgress
    }

    private val mThumbOffset: Int

    private val progressAnimation by lazy {
        SpringAnimation(progress, progressAnimProperty, 0f).apply {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = 0.55f
            minimumVisibleChange = 1f / trackDrawable.bounds.width()
            setMinValue(0f)
            setMaxValue(1f)
            addUpdateListener(thumbAnimationListener)
        }
    }

    private val thumbAnimation by lazy {
        SpringAnimation(thumbProgress, thumbAnimProperty, 0f).apply {
            spring.stiffness = 800f
            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            minimumVisibleChange = 1f / trackDrawable.bounds.width()
            setMinValue(0f)
            setMaxValue(1f)
        }
    }

    private val thumbAnimationListener = DynamicAnimation.OnAnimationUpdateListener { animation, value, velocity ->
        thumbAnimation.animateToFinalPosition(value)
    }

    fun setProgress(newProgress: Float, isAnimation: Boolean = false) {
        if (isAnimation) {
            if (throbAnimator.isRunning.not()) throbAnimator.start()
            progressAnimation.animateToFinalPosition(newProgress)
        } else {
            progress = newProgress
            thumbProgress = newProgress
        }
    }

    var colorStartA: Int
        get() = trackDrawable.colorStartA
        set(value) {
            trackDrawable.colorStartA = value
        }
    var colorEndA: Int
        get() = trackDrawable.colorEndA
        set(value) {
            trackDrawable.colorEndA = value
        }
    var colorStartB: Int
        get() = trackDrawable.colorStartB
        set(value) {
            trackDrawable.colorStartB = value
        }
    var colorEndB: Int
        get() = trackDrawable.colorEndB
        set(value) {
            trackDrawable.colorEndB = value
        }


    val trackDrawable: TrackDrawable = TrackDrawable()


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = resolveSizeAndState(desiredWidth, widthMeasureSpec, 0)
        val h = resolveSizeAndState(desiredHeight + paddingTop + paddingBottom, heightMeasureSpec, 0)
        setMeasuredDimension(w, h)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        this.trackDrawable.setBounds(
                0 + Math.max(paddingLeft, mThumbOffset),
                h / 2 - trackDrawable.intrinsicHeight / 2,
                w - Math.max(paddingRight, mThumbOffset),
                h / 2 + trackDrawable.intrinsicHeight / 2
        )
    }


    var thunderBitmapScale = 1f
    var throbAnimator = ValueAnimator.ofFloat(1f, 1.5f)

    init {
        this.setLayerType(LAYER_TYPE_SOFTWARE, null)
        val density = context.resources.displayMetrics.density

        desiredWidth = (56 * density * 4).toInt()
        desiredHeight =
                (density * 8 + context.resources.getDimension(R.dimen.slider_sticker_slider_handle_size)).roundToInt()
        mThumbOffset = desiredHeight / 2

        starLottie = LottieDrawable()

        this.trackDrawable.callback = this
        this.starLottie.callback = this

        trackDrawable.totalHeight = context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        trackDrawable.setTrackHeight(context.resources.getDimension(R.dimen.slider_sticker_slider_track_height))
        trackDrawable.invalidateSelf()




        starLottie.enableMergePathsForKitKatAndAbove(true)
        val result = LottieCompositionFactory.fromAssetSync(getContext().applicationContext, "stars_winner.json")
        starLottie.composition = result.value
        starLottie.speed = 1.2f
        starLottie.repeatCount = LottieDrawable.INFINITE
        starLottie.addAnimatorUpdateListener { invalidate() }
        starLottie.start()

        throbAnimator.repeatMode = ValueAnimator.REVERSE;
        throbAnimator.repeatCount = 1
        throbAnimator.interpolator = AccelerateDecelerateInterpolator()
        throbAnimator.addUpdateListener { animation ->
            thunderBitmapScale = animation.animatedValue as Float
            invalidate()
        }


        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.EmojiSlider)

            try {
                thumbProgress = progress

                colorStartA = context.getColorCompat(R.color.slider_gradient_start_A)
                colorEndA = context.getColorCompat(R.color.slider_gradient_end_A)

                colorStartB = context.getColorCompat(R.color.slider_gradient_start_B)
                colorEndB = context.getColorCompat(R.color.slider_gradient_end_B)

                invalidateAll()

            } finally {
                array.recycle()
            }
        }

    }

    fun invalidateAll() {
        trackDrawable.invalidateSelf()
        invalidate()
    }

    private fun Float.limitToRange() = Math.max(Math.min(this, 1f), 0f)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        trackDrawable.draw(canvas)
        drawStar(canvas)
        drawThumb(canvas)

    }

    private fun drawThumb(canvas: Canvas) {

        val thunderIconBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.lightning)
        val alpha: Bitmap = thunderIconBitmap.extractAlpha()

        val paint = Paint()
        paint.color = Color.parseColor("#FF9800")
        paint.maskFilter = BlurMaskFilter(35f, Blur.OUTER)

        val x = thumbProgress * trackDrawable.bounds.width() + trackDrawable.bounds.left - thunderIconBitmap.width / 2f
        val y = trackDrawable.bounds.top.toFloat() - trackDrawable.trackHeight / 2 + thunderIconBitmap.height / 2

        canvas.save()
        canvas.translate(x, y)
        canvas.scale(thunderBitmapScale, thunderBitmapScale, (thunderIconBitmap.width / 2.0).toFloat(), (thunderIconBitmap.height / 2.0).toFloat())
        canvas.drawBitmap(alpha, 0f, 0f, paint)
        canvas.drawBitmap(thunderIconBitmap, 0f, 0f, null)
        canvas.restore()
    }

    private fun drawStar(canvas: Canvas) {
        val thumbScale = 0.6f
        starLottie.scale = thumbScale
        val x = thumbProgress * trackDrawable.bounds.width() + trackDrawable.bounds.left - starLottie.intrinsicWidth / 2f
        val y = trackDrawable.bounds.top.toFloat() + trackDrawable.trackHeight / 2
        canvas.save()
        canvas.translate(x, y)
        starLottie.draw(canvas)
        canvas.restore()
    }


}

