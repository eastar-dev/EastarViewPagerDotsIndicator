package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.eastandroid.smartc.viewpagerdotsindicator.R;

public class DotIndicator extends View {
    private int align;
    private int mCurrent;

    private int dotSpacing;
    private Drawable dot;
    private int dotCount;
    private int mW;
    private int startX;


    public DotIndicator(Context context) {
        this(context, null);
    }

    public DotIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        dotSpacing = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics()); // 5dp
        dotCount = 2;
        align = 0;//0:left,1:center,2:right

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.DotIndicator);
            dotSpacing = (int) a.getDimension(R.styleable.DotIndicator_dotSpacing, dotSpacing);
            dotCount = a.getInteger(R.styleable.DotIndicator_dotCount, dotCount);
            dot = a.getDrawable(R.styleable.DotIndicator_dot);
            align = a.getInteger(R.styleable.DotIndicator_align, align);
            a.recycle();
        }

        if (isInEditMode())
            dotCount = 5;

        updateUI();
    }

//    private int mCount;

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

    public void setDrawable(@DrawableRes int dot) {
        this.dot = ContextCompat.getDrawable(getContext(), dot);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mW = w;
        updateUI();
        invalidate();
    }

    public void updateUI() {
        int count = dotCount;

        int dots_width;
        if (dot instanceof StateListDrawable) {
            dot.setState(state_selected);
            int state_selected_width = dot.getIntrinsicWidth();
//            Log.e("t", "" + state_selected_width);

            dot.setState(state_normal);
            int state_normal_width = dot.getIntrinsicWidth();
//            Log.e("t", "" + state_normal_width);
            dots_width = state_selected_width + state_normal_width * (count - 1);
//            Log.e("t", "" + dots_width);
        } else {
            dots_width = dot.getIntrinsicWidth() * count;
        }
        final int space_width = (count - 1) * dotSpacing;
        final int width = dots_width + space_width;

        switch (align) {
            default:
            case 0://left
                startX = 0;
            case 1://center
                startX = mW / 2 - width / 2;
            case 2://right
                startX = mW - width;
        }
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

            dot.setBounds(left, 0, left + dot.getIntrinsicWidth(), dot.getIntrinsicHeight());
            dot.draw(canvas);
            left += dot.getIntrinsicWidth() + dotSpacing;
        }
    }

    public void setCurrentPosition(int position) {
        mCurrent = position;
        invalidate();
    }

    public void setupWithViewPager(ViewPager pager) {
        setCount(pager.getAdapter().getCount());
        pager.addOnPageChangeListener(onPageChangeListener);
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            setCurrentPosition(position % dotCount);
        }
    };
}
