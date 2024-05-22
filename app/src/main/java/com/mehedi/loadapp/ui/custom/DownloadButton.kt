package com.mehedi.loadapp.ui.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.mehedi.loadapp.R
import com.mehedi.loadapp.ui.custom.state.ButtonState
import kotlin.properties.Delegates

class DownloadButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var widthSize = 0
    private var heightSize = 0
    private var progressValue = 0.0F
    private val paint = Paint().apply {}
    private val textPaint = Paint().apply {
        textSize = context.resources.getDimension(R.dimen.default_text_size)
        textAlign = Paint.Align.CENTER
    }
    private val circlePaint = Paint().apply {}
    private var loadingButtonText: String? = null
    private var completeButtonText: String? = null
    private var loadingButtonColor: Int = 0
    private var completeButtonColor: Int = 0
    private var arcColor: Int = 0
    private var text: String = ""
    private var valueAnimator = ValueAnimator()
    private val animationDuration = 3000L
    private val animationCompletedDuration = 300L
    private val circleRadius = 40F
    
    var buttonState: ButtonState by Delegates.observable(
        ButtonState.Completed
    ) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> {}
            ButtonState.Loading -> {
                text = loadingButtonText!!
                valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
                    duration = animationDuration
                    repeatCount = ValueAnimator.INFINITE
                    addUpdateListener {
                        progressValue = it.animatedValue as Float
                        invalidate()
                    }
                    start()
                }
            }
            
            ButtonState.Completed -> {
                valueAnimator.cancel()
                valueAnimator = ValueAnimator.ofFloat(progressValue, 1F).apply {
                    duration = animationCompletedDuration
                    addUpdateListener {
                        progressValue = it.animatedValue as Float
                        invalidate()
                    }
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            text = completeButtonText!!
                            progressValue = 0.0F
                            invalidate()
                        }
                    })
                    start()
                }
            }
        }
    }
    
    init {
        progressValue = 0.0F
        context.theme.obtainStyledAttributes(attrs, R.styleable.DownloadButton, 0, 0).apply {
            loadingButtonText = getString(R.styleable.DownloadButton_loadingButtonText)
            completeButtonText = getString(R.styleable.DownloadButton_completeButtonText)
            loadingButtonColor = getColor(R.styleable.DownloadButton_loadingButtonColor, 0)
            completeButtonColor = getColor(R.styleable.DownloadButton_completeButtonColor, 0)
            arcColor = getColor(R.styleable.DownloadButton_arcColor, 0)
        }
        text = completeButtonText!!
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.let {
            canvas.drawColor(completeButtonColor)
            drawRectangle(canvas)
            drawText(canvas)
            drawArc(canvas)
        }
    }
    
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val measuredWidth: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val measuredHeight: Int = resolveSizeAndState(
            MeasureSpec.getSize(measuredWidth), heightMeasureSpec, 0
        )
        widthSize = measuredWidth
        heightSize = measuredHeight
        setMeasuredDimension(measuredWidth, measuredHeight)
    }
    
    private fun drawRectangle(canvas: Canvas) {
        val rectDraw = RectF(
            0F, 0F, widthSize.toFloat() * progressValue, heightSize.toFloat()
        )
        
        paint.color = loadingButtonColor
        canvas.drawRect(rectDraw, paint)
    }
    
    private fun drawText(canvas: Canvas) {
        textPaint.color = Color.WHITE
        canvas.drawText(
            text, widthSize.toFloat() / 2, heightSize.toFloat() / 2 + 10, textPaint
        )
    }
    
    private fun drawArc(canvas: Canvas) {
        circlePaint.color = arcColor
        val xPosition = 3 * widthSize.toFloat() / 4
        val yPosition = heightSize.toFloat() / 2
        canvas.drawArc(
            xPosition,
            yPosition - circleRadius,
            circleRadius * 2 + xPosition,
            circleRadius + yPosition,
            0F,
            360F * progressValue,
            true,
            circlePaint
        )
    }
    
}