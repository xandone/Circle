package com.example.xandone.circle;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * author: xandone
 * created on: 2017/7/27 11:31
 */

public class XCircle extends View {
    private Context mContext;
    private int mRadius;
    private int mX, mY;//圆心
    private int mDefaultSize;

    private Paint mPaint;
    private Paint mPaintBg;
    private Paint mTextPaint;
    private int mColorLight;
    private int mColorDeep;
    private int mColorBg;
    private int mTextSize;
    private int mTextHeight;

    private RectF mRectF;

    private ValueAnimator mValueAnimator;
    private int mValue;
    private int mEndAngle;
    private int mCount;

    private SweepGradient mSweepGradient;
    private int[] colors = new int[2];
    private Matrix mMatrix = new Matrix();

    private int mCircleWidth;
    private int mOffsetSize;


    public static final int TEXT_OFFSET_SIZE = 20;

    public static final int DEFUALT_CIRCLE_WIDTH = 40;
    public static final int DEFUALT_COLOR_LIGHT = Color.GRAY;
    public static final int DEFUALT_COLOR_DEEP = Color.GRAY;
    public static final int DEFUALT_COLOR_BG = Color.WHITE;
    public static final int DEFUALT_TEXT_SIZE = 30;
    public static final int DURATION = 300;

    public XCircle(Context context) {
        this(context, null);
    }

    public XCircle(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public XCircle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.XCircleView, defStyleAttr, 0);
        mTextSize = a.getDimensionPixelSize(R.styleable.XCircleView_xcv_text_size, DEFUALT_TEXT_SIZE);
        mColorLight = a.getColor(R.styleable.XCircleView_xcv_color_light, DEFUALT_COLOR_LIGHT);
        mColorDeep = a.getColor(R.styleable.XCircleView_xcv_color_deep, DEFUALT_COLOR_DEEP);
        mColorBg = a.getColor(R.styleable.XCircleView_xcv_color_bg, DEFUALT_COLOR_BG);
        mCircleWidth = a.getDimensionPixelSize(R.styleable.XCircleView_xcv_width_size, DEFUALT_CIRCLE_WIDTH);
        a.recycle();
        this.mContext = context;
        init();
    }

    public void init() {
        mDefaultSize = Utils.dp2px(mContext, 100);
        mOffsetSize = mCircleWidth;
        colors[0] = mColorDeep;
        colors[1] = mColorLight;

        mPaintBg = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintBg.setColor(mColorBg);
        mPaintBg.setStyle(Paint.Style.STROKE);
        mPaintBg.setStrokeWidth(mCircleWidth);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);
//        mPaint.setStrokeCap(Paint.Cap.ROUND);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setStrokeWidth(4);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = mTextPaint.getFontMetrics();
        mTextHeight = (int) Math.ceil(fm.descent - fm.ascent);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBgCircle(canvas);
        drawBgArc(canvas);
        drawBgText(canvas);
    }

    /**
     * 绘制圆环背景
     *
     * @param canvas
     */
    public void drawBgCircle(Canvas canvas) {
        canvas.drawCircle(mX, mY, mRadius, mPaintBg);
    }

    public void drawBgArc(Canvas canvas) {
        mMatrix.setRotate(mEndAngle, mX, mY);
        mSweepGradient.setLocalMatrix(mMatrix);
        canvas.drawArc(mRectF, -90, mEndAngle, false, mPaint);
    }

    public void drawBgText(Canvas canvas) {
        mTextPaint.setTextSize(mTextSize * 2);
        canvas.drawText("" + mCount, mX, mY, mTextPaint);
        mTextPaint.setTextSize(mTextSize);
        canvas.drawText("我的排名", mX, mY + mTextHeight + TEXT_OFFSET_SIZE, mTextPaint);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = measureDimension(widthMeasureSpec);
        int h = measureDimension(heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    public int measureDimension(int measureDir) {
        int size;
        int specMode = MeasureSpec.getMode(measureDir);
        int specSize = MeasureSpec.getSize(measureDir);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                size = Math.min(mDefaultSize, specSize);
                break;
            case MeasureSpec.EXACTLY:
                size = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                size = mDefaultSize;
                break;
            default:
                size = mDefaultSize;
                break;
        }
        return size;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mX = w / 2;
        mY = h / 2;
        mRadius = w < h ? w / 2 - mOffsetSize : h / 2 - mOffsetSize;
        if (mRadius < 0) {
            mRadius = 0;
        }

        mRectF = new RectF(mOffsetSize, mOffsetSize, w - mOffsetSize, h - mOffsetSize);

        mSweepGradient = new SweepGradient(mX, mY, colors, null);
        mPaint.setShader(mSweepGradient);

    }

    public void startAnim(final int end) {

        mValueAnimator = ValueAnimator.ofInt(0, end);
        mValueAnimator.setInterpolator(new LinearInterpolator());
        mValueAnimator.setDuration(DURATION);
        mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mValue = (int) animation.getAnimatedValue();
                mEndAngle = mValue;
                mCount = mValue;
                invalidate();
            }
        });
        mValueAnimator.start();
    }

    public void stopAnim() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnim();
    }
}
