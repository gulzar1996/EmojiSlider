package com.bernaferrari.emojislider.drawables

import android.graphics.*
import android.graphics.Shader.TileMode
import android.util.Log

/**
 * This contains the track, track background, progress and respective colors.
 * Most of the other components and drawables are dependent on [TrackDrawable]'s dimensions.
 */
class TrackDrawable : GenericDrawableCallback() {

    internal val trackColor = Paint(1)
    private val progressGradientA = Paint(1)
    private val progressGradientB = Paint(1)

    private val barRect = RectF()
    private var gradientRect = Rect()
    var percentProgress = 0.90f

    //TODO remove setters from here as it is being used during onDraw
    internal var colorStartA: Int = 0
    internal var colorEndA: Int = 0

    internal var colorStartB: Int = 0
    internal var colorEndB: Int = 0

    private var radius: Float = 0f
    internal var totalHeight: Int = 0
    internal var trackHeight: Float = 0f

    fun setTrackHeight(size: Float) {
        radius = size / 2
        trackHeight = size
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {

        canvas.save()
        canvas.translate(bounds.left.toFloat(), bounds.top.toFloat())

        //First Bar
        barRect.set(
                0f,
                bounds.height() / 2f - trackHeight / 2,
                percentProgress * bounds.width(),
                bounds.height() / 2f + trackHeight / 2
        )
        barRect.round(gradientRect)
        updateShader(gradientRect, progressGradientA, colorStartA, colorEndA)
        canvas.drawRoundRect(barRect, radius, radius, progressGradientA)
        canvas.restore()

        //Second bar
        barRect.set(
                percentProgress * bounds.width(),
                bounds.height() / 2f - trackHeight / 2,
                bounds.width().toFloat(),
                bounds.height() / 2f + trackHeight / 2
        )
        barRect.round(gradientRect)
        updateShader(gradientRect, progressGradientB, colorStartB, colorEndB)
        canvas.drawRoundRect(barRect, radius, radius, progressGradientB)


    }

    override fun onBoundsChange(rect: Rect) {
        Log.d("TD", "onBoundsChange: ${rect}")
    }

    override fun getIntrinsicHeight(): Int = totalHeight

    private fun updateShader(rect: Rect, progressGradient: Paint, colorS: Int, colorE: Int) {
        progressGradient.shader = LinearGradient(
                0.0f,
                rect.exactCenterY(),
                rect.width().toFloat(),
                rect.exactCenterY(),
                colorS,
                colorE,
                TileMode.CLAMP
        )
    }

    override fun setAlpha(alpha: Int) {
        this.progressGradientA.alpha = alpha
        this.trackColor.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        this.progressGradientA.colorFilter = colorFilter
        this.trackColor.colorFilter = colorFilter
    }
}
