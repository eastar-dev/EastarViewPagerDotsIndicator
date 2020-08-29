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


package android.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import dev.eastar.viewpagerdotsindicator.R;

public class DotIndicator extends View {
    private int gravity;
    private int mCurrent;

    private int dotSpacing;
    private Drawable dot;
    private int dotCount;
    private int mW;
    private int mH;
    private int startX;
    private int centerY;

    public DotIndicator(Context context) {
        this(context, null);
    }

    @TargetApi(Build.VERSION_CODES.CUPCAKE)
    public DotIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        dotSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics()); // 5dp
        dotCount = 2;
        gravity = 0;//android.view.Gravity

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DotIndicator);
            dotSpacing = (int) a.getDimension(R.styleable.DotIndicator_dotSpacing, dotSpacing);
            dotCount = a.getInteger(R.styleable.DotIndicator_dotCount, dotCount);
            dot = a.getDrawable(R.styleable.DotIndicator_dot);
            gravity = a.getInteger(R.styleable.DotIndicator_gravity, gravity);
            a.recycle();
        }

        if (isInEditMode())
            dotCount = 5;

        updateUI();
    }

    public void setCount(int count) {
        dotCount = count;
        updateUI();
        invalidate();
    }

    public void setDotSpacing(int dotSpacing) {
        this.dotSpacing = dotSpacing;
        updateUI();
        invalidate();
    }

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setCurrentPosition(int position) {
        mCurrent = position;
        invalidate();
    }

    public void setupWithViewPager(ViewPager pager) {
        if (dotCount <= 0)
            setCount(pager.getAdapter().getCount());
        pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (dotCount > 0)
                    setCurrentPosition(position % dotCount);
            }
        });
    }

    public void setupWithViewPager(ViewPager2 pager) {
        if (dotCount <= 0)
            setCount(pager.getAdapter().getItemCount());
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                if (dotCount > 0)
                    setCurrentPosition(position % dotCount);
            }
        });
    }

    public void setDrawable(@DrawableRes int dot) {
        this.dot = ContextCompat.getDrawable(getContext(), dot);
        updateUI();
        invalidate();
    }

    public void setDrawable(Drawable dot) {
        this.dot = dot;
        updateUI();
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        mH = h;
        updateUI();
        invalidate();
    }

    public void updateUI() {
        int count = dotCount;

        int dots_width;
        int height = mH / 2;
        if (dot instanceof StateListDrawable) {
            dot.setState(state_selected);
            int state_selected_width = dot.getIntrinsicWidth();
            int state_selected_height = dot.getIntrinsicHeight();
//            Log.e("t", "" + state_selected_width);

            dot.setState(state_normal);
            int state_normal_width = dot.getIntrinsicWidth();
            int state_normal_height = dot.getIntrinsicHeight();
//            Log.e("t", "" + state_normal_width);
            dots_width = state_selected_width + state_normal_width * (count - 1);

            height = Math.max(state_selected_height, state_normal_height);
//            Log.e("t", "" + dots_width);
        } else {
            dots_width = dot.getIntrinsicWidth() * count;
            height = dot.getIntrinsicHeight();
        }

        final int space_width = (count - 1) * dotSpacing;
        final int width = dots_width + space_width;
        final Rect outRect = new Rect();
        Gravity.apply(gravity, width, height, new Rect(0, 0, mW, mH), outRect);

        startX = outRect.left;
        centerY = outRect.centerY();
    }

    private static final int[] state_selected = new int[]{android.R.attr.state_selected};
    private static final int[] state_normal = new int[]{android.R.attr.state_empty};

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int left = startX;
        for (int i = 0; i < dotCount; i++) {
            if (dot instanceof StateListDrawable) {
                if (i == mCurrent)
                    dot.setState(state_selected);
                else
                    dot.setState(state_normal);
            }
            int w = dot.getIntrinsicWidth();
            int h = dot.getIntrinsicHeight();

            dot.setBounds(left, centerY - h / 2, left + w, centerY - h / 2 + h);
            dot.draw(canvas);
            left += dot.getIntrinsicWidth() + dotSpacing;
        }
    }

}
