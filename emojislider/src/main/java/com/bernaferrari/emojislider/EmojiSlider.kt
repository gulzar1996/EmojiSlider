package com.bernaferrari.emojislider

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.bernaferrari.emojislider.drawables.*
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

    private val debugPaint: Paint

    var emoji = "😍"
        set(value) {
            field = value
            updateThumb(field)
        }

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

    val progressAnimation by lazy {
        SpringAnimation(progress, progressAnimProperty, 0f).apply {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            minimumVisibleChange = 0.001f
            setMinValue(0f)
            setMaxValue(1f)
            addUpdateListener(thumbAnimationListener)
        }
    }

    val thumbAnimation by lazy {
        SpringAnimation(thumbProgress, thumbAnimProperty, 0f).apply {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_NO_BOUNCY
            setMinValue(0f)
            setMaxValue(1f)
        }
    }


    private val thumbAnimationListener = DynamicAnimation.OnAnimationUpdateListener { animation, value, velocity ->
        thumbAnimation.animateToFinalPosition(value)
    }

    fun setProgress(newProgress: Float, isAnimation: Boolean = false) {
        if (isAnimation) {
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


    lateinit var thumbDrawable: Drawable
    val trackDrawable: TrackDrawable = TrackDrawable()
    val averageDrawable: CircleDrawable = CircleDrawable(context)
    val resultDrawable: ResultDrawable = ResultDrawable(context)


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

    override fun scheduleDrawable(drawable: Drawable, runnable: Runnable, j: Long) = Unit
    override fun unscheduleDrawable(drawable: Drawable, runnable: Runnable) = Unit
    override fun invalidateDrawable(drawable: Drawable) = invalidate()


    //////////////////////////////////////////
    // Initialization
    //////////////////////////////////////////


    init {
        this.setLayerType(LAYER_TYPE_SOFTWARE, null)
        val density = context.resources.displayMetrics.density

        desiredWidth = (56 * density * 4).toInt()
        desiredHeight =
                (density * 8 + context.resources.getDimension(R.dimen.slider_sticker_slider_handle_size)).roundToInt()
        mThumbOffset = desiredHeight / 2

        starLottie = LottieDrawable()

        this.resultDrawable.callback = this
        this.averageDrawable.callback = this
        this.trackDrawable.callback = this
        this.starLottie.callback = this

        setResultHandleSize(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size))
        trackDrawable.totalHeight = context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        trackDrawable.setTrackHeight(context.resources.getDimension(R.dimen.slider_sticker_slider_track_height))
        trackDrawable.invalidateSelf()

        setResultDrawable(getBitmapFromVectorDrawable(context, R.drawable.thunder))

        debugPaint = Paint()
        debugPaint.color = Color.BLACK
        debugPaint.style = Paint.Style.STROKE

        starLottie.enableMergePathsForKitKatAndAbove(true)
        val result = LottieCompositionFactory.fromAssetSync(getContext().applicationContext, "stars_winner.json")
        starLottie.composition = result.value
        starLottie.speed = 1.8f
        starLottie.repeatCount = LottieDrawable.INFINITE
        starLottie.addAnimatorUpdateListener { invalidate() }
        starLottie.start()

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.EmojiSlider)

            try {
                progress = array.getProgress()
                thumbProgress = progress

                colorStartA = context.getColorCompat(R.color.slider_gradient_start_A)
                colorEndA = context.getColorCompat(R.color.slider_gradient_end_A)

                colorStartB = context.getColorCompat(R.color.slider_gradient_start_B)
                colorEndB = context.getColorCompat(R.color.slider_gradient_end_B)

                emoji = array.getEmoji()

                invalidateAll()

            } finally {
                array.recycle()
            }
        } else {
            colorStartA = context.getColorCompat(R.color.slider_gradient_start_A)
            colorEndA = context.getColorCompat(R.color.slider_gradient_end_A)
            emoji = emoji
        }

    }

    fun invalidateAll() {
        trackDrawable.invalidateSelf()
        thumbDrawable.invalidateSelf()
        invalidate()
    }

    fun setResultHandleSize(size: Int) {
        resultDrawable.sizeHandle = size.toFloat()
        resultDrawable.imageDrawable.invalidateSelf()
        resultDrawable.circleDrawable.invalidateSelf()
    }

    //////////////////////////////////////////
    // PRIVATE GET METHODS
    //////////////////////////////////////////

    private fun TypedArray.getProgress(): Float =
            this.getFloat(R.styleable.EmojiSlider_progress_value, progress).limitToRange()

    private fun TypedArray.getEmoji(): String =
            this.getString(R.styleable.EmojiSlider_emoji) ?: emoji


    //////////////////////////////////////////
    // Helper methods
    //////////////////////////////////////////

    private fun updateThumb(emoji: String) {
        thumbDrawable = textToDrawable(
                context = this.context,
                text = emoji,
                size = R.dimen.slider_sticker_slider_handle_size
        )
        thumbDrawable.callback = this
        invalidate()
    }

    /**
     * This will generate a drawable for [resultDrawable] based on a bitmap image.
     */
    fun setResultDrawable(bitmap: Bitmap) {
        resultDrawable.setDrawableFromBitmap(bitmap)
    }

    //////////////////////////////////////////
    // Extension functions
    //////////////////////////////////////////

    private fun Float.limitToRange() = Math.max(Math.min(this, 1f), 0f)


    //////////////////////////////////////////
    // Draw methods
    //////////////////////////////////////////

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        trackDrawable.draw(canvas)
        //if (shouldDisplayAverage) drawAverage(canvas)
        drawThumb(canvas)
        //drawProfilePicture(canvas)
        //if (shouldDisplayResultPicture) drawProfilePicture(canvas)
        drawStar(canvas)
    }

    private fun drawThumb(canvas: Canvas) {

        val widthPosition = thumbProgress * trackDrawable.bounds.width()
        val thumbScale = 0.7f

        canvas.save()
        canvas.translate(trackDrawable.bounds.left.toFloat(), trackDrawable.bounds.top.toFloat())
        canvas.scale(
                thumbScale,
                thumbScale,
                widthPosition,
                (trackDrawable.bounds.bottom - trackDrawable.bounds.top) / 2f
        )

        thumbDrawable.updateDrawableBounds(widthPosition.roundToInt())

        val paint = (thumbDrawable as TextDrawable).textPaint
        paint.color = Color.WHITE;
        paint.style = Paint.Style.FILL;
        paint.setShadowLayer(45f, 0f, 0f, Color.parseColor("#FF9800"))
        thumbDrawable.draw(canvas)

        canvas.restore()
    }

    private fun drawStar(canvas: Canvas) {

        val widthPosition = thumbProgress * trackDrawable.bounds.width()

        val thumbScale = 1f
        starLottie.scale = thumbScale
        val left = widthPosition - starLottie.intrinsicWidth / 7
        canvas.save()
        canvas.translate(left, trackDrawable.bounds.top.toFloat() - starLottie.intrinsicHeight / 9f)
        starLottie.draw(canvas)
        canvas.restore()
    }

    private fun drawProfilePicture(canvas: Canvas) {

        val widthPosition = progress * trackDrawable.bounds.width()
        val height: Float = trackDrawable.bounds.height() / 2f

        canvas.save()
        canvas.translate(trackDrawable.bounds.left.toFloat(), trackDrawable.bounds.top.toFloat())
        canvas.scale(1f, 1f, widthPosition, height)

        //resultDrawable.updateDrawableBounds(widthPosition.roundToInt())
        canvas.drawRect(resultDrawable.bounds, debugPaint)
        resultDrawable.draw(canvas)

        canvas.restore()
    }


    private fun Drawable.updateDrawableBounds(widthPosition: Int) {

        val customIntrinsicWidth = this.intrinsicWidth / 2
        val customIntrinsicHeight = this.intrinsicHeight / 2
        val heightPosition = trackDrawable.bounds.height() / 2

        this.setBounds(
                widthPosition - customIntrinsicWidth,
                heightPosition - customIntrinsicHeight,
                widthPosition + customIntrinsicWidth,
                heightPosition + customIntrinsicHeight
        )
    }

    fun getBitmapFromVectorDrawable(context: Context, @DrawableRes drawableId: Int): Bitmap {
        var drawable: Drawable = ContextCompat.getDrawable(context, drawableId)!!
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate()
        }
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
                drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

}

