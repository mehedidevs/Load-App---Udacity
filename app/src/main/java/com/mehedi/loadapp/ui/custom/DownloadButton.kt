package com.mehedi.loadapp.ui.custom

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import com.mehedi.loadapp.R
import com.mehedi.loadapp.ui.custom.state.ButtonState
import com.mehedi.loadapp.utils.dpToPx

class DownloadButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var widthSize = 0
    private var heightSize = 0
    private var buttonState: ButtonState = ButtonState.NORMAL
    private var normalButton = 0
    private var loadingButton = 0
    private var completedButton = 0
    private var progress = 0.0
    private val animationDuration = 3000L
    private val cornerRadius = 16f
    private val arcRadius = 28f
    private val buttonHeight = 56f
    
    private val paint = Paint().apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40.0f
        typeface = Typeface.create("", Typeface.NORMAL)
    }
    
    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            normalButton = getColor(R.styleable.LoadingButton_button_normal_color, 0)
            loadingButton = getColor(R.styleable.LoadingButton_button_loading_color, 0)
            completedButton = getColor(R.styleable.LoadingButton_button_completed_color, 0)
        }
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawButton(canvas)
        drawText(canvas)
    }
    
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val mWidth: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val mHeight: Int = resolveSizeAndState(
            MeasureSpec.getSize(mWidth),
            heightMeasureSpec,
            0
        )
        widthSize = mWidth
        heightSize = mHeight
        setMeasuredDimension(mWidth, mHeight)
    }
    
    override fun performClick(): Boolean {
        super.performClick()
        buttonState = ButtonState.LOADING
        if (buttonState == ButtonState.LOADING) {
            animation()
        }
        invalidate()
        return true
    }
    
    
    private fun drawButton(canvas: Canvas) {
        paint.color = normalButton
        val rect = RectF(0f, 0f, width.toFloat(), buttonHeight.dpToPx(this))
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
        
        if (buttonState == ButtonState.LOADING) {
            paint.color = loadingButton
            val progressWidth = (width * (progress / 100)).toFloat()
            val rectProgress = RectF(
                0f,
                0f,
                progressWidth,
                buttonHeight.dpToPx(this)
            )
            
            canvas.drawRoundRect(
                rectProgress,
                cornerRadius, cornerRadius,
                paint
            )
            
            val arcCenterX =
                width - (arcRadius + cornerRadius)
            val arcCenterY = buttonHeight.dpToPx(this) / 2f
            
            
            val arcRect = RectF(
                arcCenterX - arcRadius,
                arcCenterY - arcRadius,
                arcCenterX + arcRadius,
                arcCenterY + arcRadius
            )
            canvas.save()
            paint.color = completedButton
            canvas.drawArc(arcRect, 0f, (360 * (progress / 100)).toFloat(), true, paint)
            canvas.restore()
        }
    }
    
    
    private fun drawText(canvas: Canvas?) {
        paint.color = Color.WHITE
        val label = when (buttonState) {
            ButtonState.NORMAL -> context.getString(R.string.download)
            ButtonState.LOADING -> context.getString(R.string.loading)
            ButtonState.COMPLETED -> context.getString(R.string.download)
        }
        
        val textBounds = Rect()
        paint.getTextBounds(label, 0, label.length, textBounds)
        val textX = (width / 2f)
        val textY =
            (buttonHeight.dpToPx(this) / 2f) - (textBounds.bottom + textBounds.top) / 2f
        canvas?.drawText(label, textX, textY, paint)
    }
    
    private fun animation() {
        val buttonAnimator = ValueAnimator.ofFloat(0f, measuredWidth.toFloat())
        
        val updateListener = ValueAnimator.AnimatorUpdateListener { animated ->
            progress = (animated.animatedValue as Float).toDouble()
            invalidate()
        }
        
        buttonAnimator.duration = animationDuration
        buttonAnimator.addUpdateListener(updateListener)
        buttonAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                buttonState = ButtonState.NORMAL
            }
        })
        buttonAnimator.start()
    }
    
    
}