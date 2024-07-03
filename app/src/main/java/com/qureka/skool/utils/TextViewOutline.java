package com.qureka.skool.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.qureka.skool.R;

public class TextViewOutline extends AppCompatTextView {

    private static final int DEFAULT_OUTLINE_SIZE = 5;
    private static final int DEFAULT_OUTLINE_COLOR = Color.TRANSPARENT;

    private int mOutlineSize;
    private int mOutlineColor;
    private int mTextColor;

    private float mShadowRadius;
    private float mShadowDx;
    private float mShadowDy;
    private int mShadowColor;

    public TextViewOutline(@NonNull Context context) {
        super(context);
    }

    public TextViewOutline(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setAttributes(attrs);
    }

    private void setAttributes(AttributeSet attr) {
        mOutlineSize = DEFAULT_OUTLINE_SIZE;
        mOutlineColor = getResources().getColor(R.color.color_a7002b);//DEFAULT_OUTLINE_COLOR;

        mTextColor = getCurrentTextColor();
        if (attr != null) {
            TypedArray a = getContext().obtainStyledAttributes(attr, R.styleable.TextViewOutline);
            if (a.hasValue(R.styleable.TextViewOutline_outlineSize)) {
                mOutlineSize = (int) a.getDimension(R.styleable.TextViewOutline_outlineSize, DEFAULT_OUTLINE_SIZE);
            }

            if (a.hasValue(R.styleable.TextViewOutline_outlineColor)) {
                mOutlineColor = a.getColor(R.styleable.TextViewOutline_outlineColor, DEFAULT_OUTLINE_COLOR);
            }

            if (a.hasValue(R.styleable.TextViewOutline_android_shadowRadius)
                    || a.hasValue(R.styleable.TextViewOutline_android_shadowDx)
                    || a.hasValue(R.styleable.TextViewOutline_android_shadowDy)
                    || a.hasValue(R.styleable.TextViewOutline_android_shadowColor)) {

                mShadowRadius = a.getFloat(R.styleable.TextViewOutline_android_shadowRadius, 0);
                mShadowDx = a.getFloat(R.styleable.TextViewOutline_android_shadowDx, 0);
                mShadowDy = a.getFloat(R.styleable.TextViewOutline_android_shadowDy, 0);
                mShadowColor = a.getColor(R.styleable.TextViewOutline_android_shadowColor, getResources().getColor(R.color.color_a7002b));
            }

            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setPaintToOutline();
    }

    private void setPaintToOutline() {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mOutlineSize);
        super.setTextColor(mOutlineColor);
        super.setShadowLayer(0, 0, 0, getResources().getColor(R.color.color_a7002b));
    }

    private void setPainToRegular() {
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        super.setTextColor(mTextColor);
        super.setShadowLayer(mShadowRadius, mShadowDx, mShadowDy, mShadowColor);
    }

    @Override
    public void setTextColor(int color) {
        super.setTextColor(color);
        mTextColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setPaintToOutline();
        super.onDraw(canvas);
        setPainToRegular();
        super.onDraw(canvas);
    }
}
