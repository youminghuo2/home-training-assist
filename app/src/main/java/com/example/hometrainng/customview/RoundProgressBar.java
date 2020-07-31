package com.example.hometrainng.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.hometrainng.R;

/**
 * @Package com.example.hometrainng.customView
 * @Description 自定义RoundProgressView
 * @CreateDate: 2020/4/10 9:13
 */
public class RoundProgressBar extends View {
    private Paint paint;
    private Paint paint2;
    //圆环颜色
    private int roundColor;
    //进度颜色
    private int roundProgressColor;
    private int textColor;
    private float textSize;
    private float roundWidth;
    private int max;
    private int progress = 0;
    //是否显示中间进度条
    private boolean textIsDisplayable;
    //进度条实心or空心
    private int style;

    public static final int STROKE = 0;
    public static final int FILL = 1;


    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        //获取自定义属性
        roundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.parseColor("#14CE5B"));
//        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
//        textSize = mTypedArray.getColor(R.styleable.RoundProgressBar_textSize, 55);
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5);
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100);
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0);
        mTypedArray.recycle();


        paint2 = new Paint();
        TypedArray mTypedArray2 = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN);
        textSize = mTypedArray.getColor(R.styleable.RoundProgressBar_textSize, 55);
        mTypedArray2.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 大圆环绘制
         */
        int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        int center = getWidth() / 2;
        int radius = (int) (center - roundWidth / 2);
//        if (percent == 100) {
//            paint.setColor(Color.parseColor("#FF9D00"));
//        } else {
//
//        }
        paint.setColor(roundColor);
        //空心
        paint.setStyle(Paint.Style.STROKE);
        //设置圆环宽度
        paint.setStrokeWidth(roundWidth);
        //消除锯齿
        paint.setAntiAlias(true);
        canvas.drawCircle(center, center, radius, paint);

        /**
         * 画百分比
         */
        paint2.setStrokeWidth(0);
        paint2.setColor(textColor);
        paint2.setTextSize(textSize);
        paint2.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        float textWidth = paint2.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
//        if(textIsDisplayable && percent != 0 && style == STROKE){
        canvas.drawText("", center - textWidth / 2, center + textSize / 2, paint2); //画出进度百分比
//        }

        /**
         * 画圆弧，画圆环进度
         */
        paint.setStrokeWidth(roundWidth);
        if (percent == 100) {
            paint.setColor(Color.parseColor("#FF9D00"));
        } else {
            paint.setColor(Color.parseColor("#14CE5B"));
        }
        RectF oval = new RectF((float) center - radius, (float)center - radius, (float)center + radius, (float)center + radius);
        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, -90, 360F * progress / max, false, paint);
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                canvas.drawArc(oval, -90, 360F * progress / max, true, paint);
                break;
            }
        }
    }

    public synchronized int getMax() {
        return max;
    }

    public synchronized void setNax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    public synchronized int getProgress() {
        return progress;
    }

    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }

    }

    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }


}
