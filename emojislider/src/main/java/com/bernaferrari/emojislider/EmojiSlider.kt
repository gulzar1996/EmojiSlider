package com.bernaferrari.emojislider

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.FloatPropertyCompat
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.bernaferrari.emojislider.drawables.CircleDrawable
import com.bernaferrari.emojislider.drawables.ResultDrawable
import com.bernaferrari.emojislider.drawables.TextDrawable
import com.bernaferrari.emojislider.drawables.TrackDrawable
import com.cpiz.android.bubbleview.BubbleStyle
import com.cpiz.android.bubbleview.BubbleTextView
import com.facebook.rebound.*
import kotlin.math.roundToInt

class EmojiSlider @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private companion object {
        const val INITIAL_POSITION = 0.25f
        const val INITIAL_AVERAGE_POSITION = 0.5f

        const val INITIAL_AUTO_DISMISS_TIMER = 2500

        const val TENSION_SMALL = 3.0
        const val TENSION_BIG = 40.0

        const val FRICTION_SMALL = 5.0
        const val FRICTION_BIG = 7.0
    }

    private val desiredWidth: Int
    private val desiredHeight: Int


    /**
     * The main characteristic from the [EmojiSlider]. There is no restriction, as long as it is
     * a text. It actually can be anything - even a text with multiple chars, the convert text
     * to drawable process works flawless.
     */
    var emoji = "😍"
        set(value) {
            field = value
            updateThumb(field)
        }


    /**
     * Initial position of progress in range form `0.0` to `1.0`.
     */
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

    val STIFFNESS = 100f
    private val mThumbOffset: Int

    val progressAnimation by lazy {
        SpringAnimation(progress, progressAnimProperty, 0f).apply {
            spring.stiffness = STIFFNESS
            spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
            minimumVisibleChange = 0.001f
            setMinValue(0f)
            setMaxValue(1f)
            addUpdateListener(thumbAnimationListener)
        }
    }

    val thumbAnimation by lazy {
        SpringAnimation(thumbProgress, thumbAnimProperty, 0f).apply {
            spring.stiffness = SpringForce.STIFFNESS_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
            minimumVisibleChange = 0.001f
            setMinValue(0f)
            setMaxValue(1f)
        }
    }


    private val thumbAnimationListener = DynamicAnimation.OnAnimationUpdateListener { animation, value, velocity ->
        thumbAnimation.animateToFinalPosition(value)
    }


    fun setProgress(newProgress: Float, isAnimation: Boolean = false) {
        if (isAnimation)
            progressAnimation.animateToFinalPosition(newProgress)
        else {
            progress = newProgress
            thumbProgress = newProgress
        }
    }



    /*
        A - B
     */

    //Color A
    /**
     * The track progress color for the left side of the slider - default is purple.
     */
    var colorStartA: Int
        get() = trackDrawable.colorStartA
        set(value) {
            trackDrawable.colorStartA = value
        }

    /**
     * The track progress color for the right side of the slider - default is red.
     */
    var colorEndA: Int
        get() = trackDrawable.colorEndA
        set(value) {
            trackDrawable.colorEndA = value
        }


    //COLOR B
    /**
     * The track progress color for the left side of the slider - default is purple.
     */
    var colorStartB: Int
        get() = trackDrawable.colorStartB
        set(value) {
            trackDrawable.colorStartB = value
        }

    /**
     * The track progress color for the right side of the slider - default is red.
     */
    var colorEndB: Int
        get() = trackDrawable.colorEndB
        set(value) {
            trackDrawable.colorEndB = value
        }

    //////////////////////////////////////////
    // Drawables
    //////////////////////////////////////////

    /**
     * Drawable which will contain the emoji already converted into a drawable.
     */
    lateinit var thumbDrawable: Drawable

    /**
     * Drawable which will contain the track: both the background with help from [colorTrack]
     * and the progress by mixing together [colorStartA] and [colorEndA]
     */
    val trackDrawable: TrackDrawable = TrackDrawable()

    /**
     * Drawable which displays the average's small round circle with a small ring around.
     */
    val averageDrawable: CircleDrawable = CircleDrawable(context)

    /**
     * Drawable which displays the result's big round circle with a bitmap (if any) or a big circle.
     */
    val resultDrawable: ResultDrawable = ResultDrawable(context)

    //////////////////////////////////////////
    // Public callbacks
    //////////////////////////////////////////


    //////////////////////////////////////////
    // Measure methods
    //////////////////////////////////////////

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

    //////////////////////////////////////////
    // Select methods
    //////////////////////////////////////////

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



        this.resultDrawable.callback = this
        this.averageDrawable.callback = this
        this.trackDrawable.callback = this

        setResultHandleSize(context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_handle_size))
        trackDrawable.totalHeight =
                context.resources.getDimensionPixelSize(R.dimen.slider_sticker_slider_height)
        trackDrawable.setTrackHeight(context.resources.getDimension(R.dimen.slider_sticker_slider_track_height))
        trackDrawable.invalidateSelf()

        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.EmojiSlider)

            try {
                progress = array.getProgress()
                thumbProgress = progress

                colorStartA = array.getProgressGradientStartA()
                colorEndA = array.getProgressGradientEndA()

                colorStartB = array.getProgressGradientStartB()
                colorEndB = array.getProgressGradientEndB()

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

    /**
     * Invalidate all drawables with a hammer. There are so many things happening on screen, this solves
     * any invalidate problem brutally.
     */
    fun invalidateAll() {
        trackDrawable.invalidateSelf()
        thumbDrawable.invalidateSelf()
        invalidate()
    }

    /**
     * Sets the [resultDrawable]'s size
     *
     * @param size is the diameter in pixels
     */
    fun setResultHandleSize(size: Int) {
        resultDrawable.sizeHandle = size.toFloat()
        resultDrawable.imageDrawable.invalidateSelf()
        resultDrawable.circleDrawable.invalidateSelf()
    }

    //////////////////////////////////////////
    // PRIVATE GET METHODS
    //////////////////////////////////////////

    private fun TypedArray.getProgressGradientStartA(): Int {
        return context.getColorCompat(R.color.slider_gradient_start_A)
    }

    private fun TypedArray.getProgressGradientStartB(): Int {
        return context.getColorCompat(R.color.slider_gradient_start_B)
    }

    private fun TypedArray.getProgressGradientEndA(): Int {
        return context.getColorCompat(R.color.slider_gradient_end_A)
    }

    private fun TypedArray.getProgressGradientEndB(): Int {
        return context.getColorCompat(R.color.slider_gradient_end_B)
    }

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
        //if (shouldDisplayResultPicture) drawProfilePicture(canvas)
    }

    private fun drawThumb(canvas: Canvas) {

        val widthPosition = thumbProgress * trackDrawable.bounds.width()
        val thumbScale = 1f

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

    private fun drawProfilePicture(canvas: Canvas) {

        val widthPosition = progress * trackDrawable.bounds.width()
        val height: Float = trackDrawable.bounds.height() / 2f

        canvas.save()
        canvas.translate(trackDrawable.bounds.left.toFloat(), trackDrawable.bounds.top.toFloat())
        canvas.scale(1f, 1f, widthPosition, height)

        resultDrawable.updateDrawableBounds(widthPosition.roundToInt())
        resultDrawable.draw(canvas)

        canvas.restore()
    }

    private fun drawAverage(canvas: Canvas) {
//        averageDrawable.outerColor = getCorrectColor(
//                colorStartA,
//                colorEndA,
//                averageProgressValue
//        )

        // this will invalidate it in case the averageValue changes, so it updates the position
        averageDrawable.invalidateSelf()

//        val scale = mAverageSpring.currentValue.toFloat()

        val widthPosition = progress * trackDrawable.bounds.width()
        val heightPosition = (trackDrawable.bounds.height() / 2).toFloat()

        canvas.save()
        canvas.translate(trackDrawable.bounds.left.toFloat(), trackDrawable.bounds.top.toFloat())
//        canvas.scale(scale, scale, widthPosition, heightPosition)

        averageDrawable.updateDrawableBounds(widthPosition.roundToInt())
        averageDrawable.draw(canvas)

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

}

