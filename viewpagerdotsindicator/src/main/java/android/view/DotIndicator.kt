/*
 * Copyright 2018 copyright eastar Jeong
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("unused")

package android.view

import android.R
import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import kotlin.math.max
import kotlin.math.min


class DotIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    private var mCurrent = 0
    private var dotSpacing = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, context.resources.displayMetrics).toInt() // 5dp
    private var dotCount = 2
    private var gravity = Gravity.START or Gravity.CENTER_VERTICAL

    private var dot: Drawable? = null
    private var mW = 0
    private var mH = 0
    private var startX = 0
    private var centerY = 0
    private var suggestedWidth: Int = 0
    private var suggestedHeight: Int = 0

    init {
        if (attrs != null) {
            val a = getContext().obtainStyledAttributes(attrs, dev.eastar.viewpagerdotsindicator.R.styleable.DotIndicator)
            dotSpacing = a.getDimension(dev.eastar.viewpagerdotsindicator.R.styleable.DotIndicator_dotSpacing, dotSpacing.toFloat()).toInt()
            dotCount = a.getInteger(dev.eastar.viewpagerdotsindicator.R.styleable.DotIndicator_dotCount, dotCount)
            dot = a.getDrawable(dev.eastar.viewpagerdotsindicator.R.styleable.DotIndicator_dot)
            gravity = a.getInteger(dev.eastar.viewpagerdotsindicator.R.styleable.DotIndicator_gravity, gravity)
            a.recycle()
        }
        if (isInEditMode)
            dotCount = 5

        updateUI()
    }

    private fun updateUI() {
        if (dot !is StateListDrawable)
            return

        val dot = dot!!
        dot.state = state_selected
        val stateSelectedWidth = dot.intrinsicWidth
        val stateSelectedHeight = dot.intrinsicHeight

        dot.state = state_normal
        val stateNormalWidth = dot.intrinsicWidth
        val stateNormalHeight = dot.intrinsicHeight

        suggestedWidth = stateSelectedWidth + stateNormalWidth * (dotCount - 1) + dotSpacing * (dotCount - 1)
        suggestedHeight = max(stateSelectedHeight, stateNormalHeight)

        val outRect = Rect()
        Gravity.apply(gravity, width, height, Rect(0, 0, mW, mH), outRect)
        startX = outRect.left
        centerY = outRect.centerY()
    }


    override fun getSuggestedMinimumHeight(): Int = if (dot == null) minimumHeight else max(minimumHeight, suggestedHeight)
    override fun getSuggestedMinimumWidth(): Int = if (dot == null) minimumWidth else max(minimumWidth, suggestedWidth)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val desiredHeight = suggestedMinimumHeight + paddingTop + paddingBottom
        setMeasuredDimension(
            measureDimension(desiredWidth, widthMeasureSpec),
            measureDimension(desiredHeight, heightMeasureSpec)
        )
    }

    private fun measureDimension(desiredSize: Int, measureSpec: Int): Int {
        var result: Int
        val specMode = MeasureSpec.getMode(measureSpec)
        val specSize = MeasureSpec.getSize(measureSpec)
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize
        } else {
            result = desiredSize
            if (specMode == MeasureSpec.AT_MOST) {
                result = min(result, specSize)
            }
        }
        if (result < desiredSize) {
            Log.e("ChartView", "The view is too small, the content might get cut")
        }
        return result
    }

    fun setCount(count: Int) {
        dotCount = count
        updateUI()
        invalidate()
    }

    fun setDotSpacing(dotSpacing: Int) {
        this.dotSpacing = dotSpacing
        updateUI()
        invalidate()
    }

    fun setGravity(gravity: Int) {
        this.gravity = gravity
        updateUI()
        invalidate()
    }

    fun setCurrentPosition(position: Int) {
        mCurrent = position
        invalidate()
    }

    fun setupWithViewPager(pager: ViewPager) {
        if (dotCount <= 0)
            setCount(pager.adapter!!.count)

        pager.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                if (dotCount > 0) setCurrentPosition(position % dotCount)
            }
        })
    }

    fun setupWithViewPager(pager: ViewPager2) {
        if (dotCount <= 0)
            setCount(pager.adapter!!.itemCount)

        pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (dotCount > 0) setCurrentPosition(position % dotCount)
            }
        })
    }

    fun setDrawable(@DrawableRes dot: Int) {
        this.dot = ContextCompat.getDrawable(context, dot)
        updateUI()
        invalidate()
    }

    fun setDrawable(dot: Drawable?) {
        this.dot = dot
        updateUI()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mW = w
        mH = h
        updateUI()
        invalidate()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        var left = startX
        for (i in 0 until dotCount) {
            (dot as? StateListDrawable)?.let { dot ->
                if (i == mCurrent) dot.state = state_selected
                else dot.state = state_normal

                val w = dot.intrinsicWidth
                val h = dot.intrinsicHeight
                dot.setBounds(left, centerY - h / 2, left + w, centerY - h / 2 + h)
                dot.draw(canvas)
                left += dot.intrinsicWidth + dotSpacing
            }
        }
    }

    companion object {
        private val state_selected = intArrayOf(R.attr.state_selected)
        private val state_normal = intArrayOf(R.attr.state_empty)
    }
}