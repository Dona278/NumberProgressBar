package com.dona278.numberprogressbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

import static com.dona278.numberprogressbar.NumberProgressBar.ProgressTextVisibility.Invisible;
import static com.dona278.numberprogressbar.NumberProgressBar.ProgressTextVisibility.Visible;

/**
 * Created by daimajia on 14-4-30.
 * Refactored by Dona278 since 16-12-15
 */
public class NumberProgressBar extends View {
    /**
     * Max progress can be reached.
     */
    private int mMaxProgress = 100;

    /**
     * Current progress, can not exceed the max progress.
     */
    private int mCurrentProgress = 0;

    /**
     * The progress area bar color.
     */
    private int mReachedBarColor;

    /**
     * The bar unreached area color.
     */
    private int mUnreachedBarColor;

    /**
     * The progress text color.
     */
    private int mProgressTextColor;

    /**
     * The progress text size.
     */
    private float mProgressTextSize;

    /**
     * The height of the reached area.
     */
    private float mReachedBarHeight;

    /**
     * The height of the unreached area.
     */
    private float mUnreachedBarHeight;

    /**
     * The suffix of the number.
     */
    private String mSuffix = "%";

    /**
     * The prefix.
     */
    private String mPrefix = "";

    /**
     * Default values.
     */
    private final int DEFAULT_TEXT_COLOR = Color.rgb(66, 145, 241);
    private final int DEFAULT_REACHED_COLOR = Color.rgb(66, 145, 241);
    private final int DEFAULT_UNREACHED_COLOR = Color.rgb(204, 204, 204);
    private final float DEFAULT_PROGRESS_TEXT_OFFSET = dp2px(3.0f);
    private final float DEFAULT_TEXT_SIZE = sp2px(10);
    private final float DEFAULT_REACHED_BAR_HEIGHT = dp2px(1.5f);
    private final float DEFAULT_UNREACHED_BAR_HEIGHT = dp2px(1.0f);

    /**
     * For save and restore instance of progressbar.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_PROGRESS_TEXT_COLOR = "text_color";
    private static final String INSTANCE_PROGRESS_TEXT_SIZE = "text_size";
    private static final String INSTANCE_REACHED_BAR_HEIGHT = "reached_bar_height";
    private static final String INSTANCE_REACHED_BAR_COLOR = "reached_bar_color";
    private static final String INSTANCE_UNREACHED_BAR_HEIGHT = "unreached_bar_height";
    private static final String INSTANCE_UNREACHED_BAR_COLOR = "unreached_bar_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_TEXT_VISIBILITY = "text_visibility";

    private static final int PROGRESS_TEXT_VISIBLE = 0;

    /**
     * The drawn text start.
     */
    private float mDrawTextStart;

    /**
     * The drawn text end.
     */
    private float mDrawTextEnd;

    /**
     * The text that to be drawn in onDraw().
     */
    private String mCurrentDrawText;

    /**
     * The Paint of the reached area.
     */
    private Paint mReachedBarPaint;
    /**
     * The Paint of the unreached area.
     */
    private Paint mUnreachedBarPaint;
    /**
     * The Paint of the progress text.
     */
    private Paint mTextPaint;

    /**
     * Unreached bar area to draw rect.
     */
    private RectF mUnreachedRectF = new RectF(0, 0, 0, 0);
    /**
     * Reached bar area rect.
     */
    private RectF mReachedRectF = new RectF(0, 0, 0, 0);

    /**
     * The progress text offset.
     */
    private float mOffset;

    /**
     * Determine if need to draw unreached area.
     */
    private boolean mDrawUnreachedBar = true;

    private boolean mDrawReachedBar = true;

    private boolean mProgressTextVisibility = true;

    /**
     * If provided it will be shown instead of the progress percentage.
     */
    private String mCustomValue = "";

    /**
     * Listener
     */
    private OnProgressBarListener mListener;

    public enum ProgressTextVisibility {
        Visible, Invisible
    }

    public NumberProgressBar(Context context) {
        this(context, null);
    }

    public NumberProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initializePainters();

        // Load styled attributes.
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberProgressBar,
            defStyleAttr, 0);

        int textVisible = attributes.getInt(R.styleable.NumberProgressBar_numberProgressBarTextOffset, PROGRESS_TEXT_VISIBLE);
        if (textVisible != PROGRESS_TEXT_VISIBLE) {
            mProgressTextVisibility = false;
        }

        mOffset = attributes.getDimension(R.styleable.NumberProgressBar_numberProgressBarTextOffset, DEFAULT_PROGRESS_TEXT_OFFSET);

        setReachedBarColor(attributes.getColor(R.styleable.NumberProgressBar_numberProgressBarReachedColor, DEFAULT_REACHED_COLOR));
        setUnreachedBarColor(attributes.getColor(R.styleable.NumberProgressBar_numberProgressBarUnreachedColor, DEFAULT_UNREACHED_COLOR));
        setProgressTextColor(attributes.getColor(R.styleable.NumberProgressBar_numberProgressBarTextColor, DEFAULT_TEXT_COLOR));
        setProgressTextSize(attributes.getDimension(R.styleable.NumberProgressBar_numberProgressBarTextSize, DEFAULT_TEXT_SIZE));

        setReachedBarHeight(attributes.getDimension(R.styleable.NumberProgressBar_numberProgressBarReachedBarHeight, DEFAULT_REACHED_BAR_HEIGHT));
        setUnreachedBarHeight(attributes.getDimension(R.styleable.NumberProgressBar_numberProgressBarUnreachedBarHeight, DEFAULT_UNREACHED_BAR_HEIGHT));
        setCustomValue(attributes.getString(R.styleable.NumberProgressBar_numberProgressBarCustomValue));

        setSuffix(attributes.getString(R.styleable.NumberProgressBar_numberProgressBarSuffix));
        setPrefix(attributes.getString(R.styleable.NumberProgressBar_numberProgressBarPrefix));

        setProgress(attributes.getInt(R.styleable.NumberProgressBar_numberProgressBarCurrent, 0));
        setMax(attributes.getInt(R.styleable.NumberProgressBar_numberProgressBarMax, 100));

        attributes.recycle();
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mProgressTextSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) mProgressTextSize, Math.max((int) mReachedBarHeight, (int) mUnreachedBarHeight));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }

    private int measure(int measureSpec, boolean isWidth) {
        int result;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = Math.max(result, size);
                } else {
                    result = Math.min(result, size);
                }
            }
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mProgressTextVisibility) {
            calculateDrawRectF();
        } else {
            calculateDrawRectFWithoutProgressText();
        }

        if (mDrawReachedBar) {
            canvas.drawRect(mReachedRectF, mReachedBarPaint);
        }

        if (mDrawUnreachedBar) {
            canvas.drawRect(mUnreachedRectF, mUnreachedBarPaint);
        }

        if (mProgressTextVisibility) {
            canvas.drawText(mCurrentDrawText, mDrawTextStart, mDrawTextEnd, mTextPaint);
        }
    }

    private void initializePainters() {
        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private void calculateDrawRectFWithoutProgressText() {
        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
        mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (mMaxProgress * 1.0f) * mCurrentProgress + getPaddingLeft();
        mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;

        mUnreachedRectF.left = mReachedRectF.right;
        mUnreachedRectF.right = getWidth() - getPaddingRight();
        mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
        mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
    }

    private void calculateDrawRectF() {
        if (!mCustomValue.equals(""))
            mCurrentDrawText = mCustomValue;
        else {
            mCurrentDrawText = String.format(Locale.ROOT, "%d", mCurrentProgress * 100 / mMaxProgress);
        }

        mCurrentDrawText = mPrefix + mCurrentDrawText + mSuffix;
        final float drawTextWidth = mTextPaint.measureText(mCurrentDrawText);

        if (mCurrentProgress == 0) {
            mDrawReachedBar = false;
            mDrawTextStart = getPaddingLeft();
        } else {
            mDrawReachedBar = true;
            mReachedRectF.left = getPaddingLeft();
            mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
            mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (mMaxProgress * 1.0f) * mCurrentProgress - mOffset + getPaddingLeft();
            mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;
            mDrawTextStart = (mReachedRectF.right + mOffset);
        }

        mDrawTextEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

        if ((mDrawTextStart + drawTextWidth) >= getWidth() - getPaddingRight()) {
            mDrawTextStart = getWidth() - getPaddingRight() - drawTextWidth;
            mReachedRectF.right = mDrawTextStart - mOffset;
        }

        float unreachedBarStart = mDrawTextStart + drawTextWidth + mOffset;
        if (unreachedBarStart >= getWidth() - getPaddingRight()) {
            mDrawUnreachedBar = false;
        } else {
            mDrawUnreachedBar = true;
            mUnreachedRectF.left = unreachedBarStart;
            mUnreachedRectF.right = getWidth() - getPaddingRight();
            mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
            mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
        }
    }

    /**
     * Get progress text color.
     *
     * @return progress text color.
     */
    public int getProgressTextColor() {
        return mProgressTextColor;
    }

    /**
     * Get progress text size.
     *
     * @return progress text size.
     */
    public float getProgressTextSize() {
        return mProgressTextSize;
    }

    public int getUnreachedBarColor() {
        return mUnreachedBarColor;
    }

    public int getReachedBarColor() {
        return mReachedBarColor;
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    public int getMax() {
        return mMaxProgress;
    }

    public float getReachedBarHeight() {
        return mReachedBarHeight;
    }

    public float getUnreachedBarHeight() {
        return mUnreachedBarHeight;
    }

    public void setCustomValue(String customValue) {
        if (customValue == null) {
            customValue = "";
        }

        this.mCustomValue = customValue;
        invalidate();
    }

    public void setProgressTextSize(float textSize) {
        this.mProgressTextSize = textSize;
        mTextPaint.setTextSize(mProgressTextSize);
        invalidate();
    }

    public void setProgressTextColor(int textColor) {
        this.mProgressTextColor = textColor;
        mTextPaint.setColor(mProgressTextColor);
        invalidate();
    }

    public void setUnreachedBarColor(int barColor) {
        this.mUnreachedBarColor = barColor;
        mUnreachedBarPaint.setColor(mUnreachedBarColor);
        invalidate();
    }

    public void setReachedBarColor(int progressColor) {
        this.mReachedBarColor = progressColor;
        mReachedBarPaint.setColor(mReachedBarColor);
        invalidate();
    }

    public void setReachedBarHeight(float height) {
        mReachedBarHeight = height;
    }

    public void setUnreachedBarHeight(float height) {
        mUnreachedBarHeight = height;
    }

    public void setMax(int maxProgress) {
        if (maxProgress > 0) {
            this.mMaxProgress = maxProgress;
            invalidate();
        }
    }

    public void setSuffix(String suffix) {
        if (suffix == null) {
            mSuffix = "";
        } else {
            mSuffix = suffix;
        }
    }

    public String getSuffix() {
        return mSuffix;
    }

    public void setPrefix(String prefix) {
        if (prefix == null)
            mPrefix = "";
        else {
            mPrefix = prefix;
        }
    }

    public String getPrefix() {
        return mPrefix;
    }

    public void incrementProgressBy(int by) {
        if (by > 0) {
            setProgress(mCurrentProgress + by);
        }

        if (mListener != null) {
            mListener.onProgressChange(mCurrentProgress, mMaxProgress);
        }
    }

    public void setProgress(int progress) {
        if (progress <= mMaxProgress && progress >= 0) {
            this.mCurrentProgress = progress;
            invalidate();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_PROGRESS_TEXT_COLOR, mProgressTextColor);
        bundle.putFloat(INSTANCE_PROGRESS_TEXT_SIZE, mProgressTextSize);
        bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT, mReachedBarHeight);
        bundle.putFloat(INSTANCE_UNREACHED_BAR_HEIGHT, mUnreachedBarHeight);
        bundle.putInt(INSTANCE_REACHED_BAR_COLOR, mReachedBarColor);
        bundle.putInt(INSTANCE_UNREACHED_BAR_COLOR, mUnreachedBarColor);
        bundle.putInt(INSTANCE_MAX, mMaxProgress);
        bundle.putInt(INSTANCE_PROGRESS, mCurrentProgress);
        bundle.putString(INSTANCE_SUFFIX, mSuffix);
        bundle.putString(INSTANCE_PREFIX, mPrefix);
        bundle.putBoolean(INSTANCE_TEXT_VISIBILITY, mProgressTextVisibility);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            initializePainters();
            final Bundle bundle = (Bundle) state;
            setProgressTextColor(bundle.getInt(INSTANCE_PROGRESS_TEXT_COLOR));
            setProgressTextSize(bundle.getFloat(INSTANCE_PROGRESS_TEXT_SIZE));
            setReachedBarHeight(bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT));
            setUnreachedBarHeight(bundle.getFloat(INSTANCE_UNREACHED_BAR_HEIGHT));
            setReachedBarColor(bundle.getInt(INSTANCE_REACHED_BAR_COLOR));
            setUnreachedBarColor(bundle.getInt(INSTANCE_UNREACHED_BAR_COLOR));
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            setPrefix(bundle.getString(INSTANCE_PREFIX));
            setSuffix(bundle.getString(INSTANCE_SUFFIX));
            setProgressTextVisibility(bundle.getBoolean(INSTANCE_TEXT_VISIBILITY) ? Visible : Invisible);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public float dp2px(float dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(float sp) {
        final float scale = getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public void setProgressTextVisibility(ProgressTextVisibility visibility) {
        mProgressTextVisibility = visibility == Visible;
        invalidate();
    }

    public boolean getProgressTextVisibility() {
        return mProgressTextVisibility;
    }

    public void setOnProgressBarListener(OnProgressBarListener listener) {
        mListener = listener;
    }
}
