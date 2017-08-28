package com.weishi.wisdommarket.Util.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;


import com.weishi.wisdommarket.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("AppCompatCustomView")
public class VerticalSwitchTextView extends TextView implements View.OnClickListener {
    private static final int DEFAULT_SWITCH_DURATION = 500;
    private static final int DEFAULT_IDLE_DURATION = 2000;
    public static final int TEXT_ALIGN_CENTER = 0;
    public static final int TEXT_ALIGN_LEFT = 1;
    public static final int TEXT_ALIGN_RIGHT = 2;
    private Context mContext;

    private List<String> lists;//��ѭ����ʾ���ı�����
    private List<String> ellipsisLists;
    private int contentSize;
    private String outStr;//��ǰ�������ı�����
    private String inStr;//��ǰ������ı�����
    private float textBaseY;//�ı���ʾ��baseline
    private int currentIndex = 0;//��ǰ��ʾ���ڼ����ı�
    private String ellipsis;
    private float ellipsisLen = 0;

    private int switchDuaration = DEFAULT_SWITCH_DURATION;//�л�ʱ��
    private int idleDuaration = DEFAULT_IDLE_DURATION;//���ʱ��
    private int switchOrientation = 0;
    private int alignment = TEXT_ALIGN_CENTER;

    /**
     * �ı�������X����
     */
    private float inTextCenterX;
    private float outTextCenterX;
    private float currentAnimatedValue = 0.0f;
    private ValueAnimator animator;

    private TextUtils.TruncateAt mEllipsize;

    private int verticalOffset = 0;
    private int mWidth;
    private int mHeight;
    private int paddingLeft = 0;
    private int paddingBottom = 0;
    private int paddingTop = 0;
    private int paddingRight = 0;

    private Paint mPaint;

    //�ص��ӿڣ�����֪ͨ�����߿ؼ���ǰ��״̬
    public VerticalSwitchTextViewCbInterface cbInterface;

    public VerticalSwitchTextView(Context context) {
        this(context, null);
    }

    public VerticalSwitchTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalSwitchTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.VerticalSwitchTextView);
        try {
            switchDuaration = array.getInt(R.styleable.VerticalSwitchTextView_switchDuaration, DEFAULT_SWITCH_DURATION);
            idleDuaration = array.getInt(R.styleable.VerticalSwitchTextView_idleDuaration, DEFAULT_IDLE_DURATION);
            switchOrientation = array.getInt(R.styleable.VerticalSwitchTextView_switchOrientation, 0);
            alignment = array.getInt(R.styleable.VerticalSwitchTextView_alignment, TEXT_ALIGN_CENTER);
        } finally {
            array.recycle();
        }
        init();
    }

    private void init() {
        setOnClickListener(this);
        mPaint = getPaint();
        mPaint.setTextAlign(Paint.Align.CENTER);
        ellipsis = getContext().getString(R.string.ellipsis);
        ellipsisLen = mPaint.measureText(ellipsis);
        mEllipsize = getEllipsize();

        animator = ValueAnimator.ofFloat(0f, 1f).setDuration(switchDuaration);
        animator.setStartDelay(idleDuaration);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                currentAnimatedValue = (float) animation.getAnimatedValue();
                if (currentAnimatedValue < 1.0f) {
                    invalidate();
                }
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                currentIndex = (++currentIndex) % contentSize;
                if (cbInterface != null) {
                    cbInterface.showNext(currentIndex);
                }
                outStr = lists.get(currentIndex);
                inStr = lists.get((currentIndex + 1) % contentSize);

                animator.setStartDelay(idleDuaration);
                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * ����ѭ����ʾ���ı�����
     *
     * @param content ����list
     */
    public void setTextContent(List<String> content) {
        lists = content;
//        lists.clear();
//        lists = new ArrayList<>();
//        lists.add("1�ʵ����ɷ��ɷ��ɵ�ˮ���ˮ���ʷ�ٷ�ˮ���ˮ���");
//        lists.add("2�ʵ����ɷ��ɷ��ɵ�ˮ���ˮ���ʷ�ٷ�ˮ���ˮ���");
//        lists.add("3�ʵ����ɷ�");
//        lists.add("3�ʵ����ɷ�1111222333");
        if (lists == null || lists.size() == 0) {
            return;
        }
        contentSize = lists.size();

        if (contentSize > 0) {
            animator.start();
        }
    }

    private void generateEllipsisText() {
        if (ellipsisLists != null) {//��ֹ�ظ�����
            return;
        }
        ellipsisLists = new ArrayList<>();
        if (lists != null && lists.size() != 0) {
            for (String item : lists) {
                int avail = mWidth - paddingLeft - paddingRight;
                float remaining = avail - ellipsisLen;
                if (avail <= 0) {
                    ellipsisLists.add("");
                } else {
                    float itemWidth = mPaint.measureText(item, 0, item.length());
                    if (itemWidth < avail) {
                        ellipsisLists.add(item);
                    } else if (remaining <= 0) {
                        ellipsisLists.add(ellipsis);
                    } else {
                        int len = item.length();
                        float[] widths = new float[len];
                        mPaint.getTextWidths(item, 0, item.length(), widths);
                        if (mEllipsize == TextUtils.TruncateAt.END) {
                            float blockWidth = 0f;
                            for (int i = 0; i < len; i++) {
                                blockWidth += widths[i];
                                if (blockWidth > remaining) {
                                    ellipsisLists.add(item.substring(0, i) + ellipsis);
                                    break;
                                }
                            }
                        } else if (mEllipsize == TextUtils.TruncateAt.START) {
                            float blockWidth = 0f;
                            for (int i = len - 1; i >= 0; i--) {
                                blockWidth += widths[i];
                                if (blockWidth > remaining) {
                                    ellipsisLists.add(ellipsis + item.substring(i, len - 1));
                                    break;
                                }
                            }
                        } else if (mEllipsize == TextUtils.TruncateAt.MIDDLE) {
                            float blockWidth = 0f;
                            for (int i = 0, j = len - 1; i < j; i++, j--) {
                                blockWidth += (widths[i] + widths[j]);
                                if (blockWidth > remaining) {
                                    if (blockWidth - widths[j] < remaining) {
                                        ellipsisLists.add(item.substring(0, i + 1) + ellipsis + item.substring(j, len - 1));
                                    } else {
                                        ellipsisLists.add(item.substring(0, i) + ellipsis + item.substring(j, len - 1));
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        lists = ellipsisLists;
    }

    /**
     * ��Ҫ��������TextView�ĸ߶�
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);

        Rect bounds = new Rect();
        if (contentSize <= 0) {
            return;
        }
        String text = lists.get(0);
        mPaint.getTextBounds(text, 0, text.length(), bounds);
        int textHeight = bounds.height();

        paddingLeft = getPaddingLeft();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();
        paddingTop = getPaddingTop();

        if (mEllipsize != null) {
            generateEllipsisText();
        }

        outStr = lists.get(0);
        if (contentSize > 1) {
            inStr = lists.get(1);
        } else {
            inStr = lists.get(0);
        }

        mHeight = textHeight + paddingBottom + paddingTop;

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //�������ָ߶�
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        //�������ֵ�baseline
        textBaseY = mHeight - (mHeight - fontHeight) / 2 - fontMetrics.bottom;

        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (contentSize <= 0) {
            return;
        }
        //������Ƶ���������λ��
        switch (alignment) {
            case TEXT_ALIGN_CENTER:
                inTextCenterX = outTextCenterX = (mWidth - paddingLeft - paddingRight) / 2 + paddingLeft;
                break;
            case TEXT_ALIGN_LEFT:
                inTextCenterX = paddingLeft + mPaint.measureText(inStr) / 2;
                outTextCenterX = paddingLeft + mPaint.measureText(outStr) / 2;
                break;
            case TEXT_ALIGN_RIGHT:
                inTextCenterX = mWidth - paddingRight - mPaint.measureText(inStr) / 2;
                outTextCenterX = mWidth - paddingRight - mPaint.measureText(outStr) / 2;
                break;
        }

        //ֱ��ʹ��mHeight�����ı����ƣ�����Ϊtext��baseline�����ⲻ�ܾ�����ʾ
        verticalOffset = Math.round(2 * textBaseY * (0.5f - currentAnimatedValue));
//        L.d("verticalOffset is " + verticalOffset);
        if (switchOrientation == 0) {//���Ϲ����л�
            if (verticalOffset > 0) {
                canvas.drawText(outStr, outTextCenterX, verticalOffset, mPaint);
            } else {
                canvas.drawText(inStr, inTextCenterX, 2 * textBaseY + verticalOffset, mPaint);
            }
        } else {
            if (verticalOffset > 0) {//���¹����л�
                canvas.drawText(outStr, outTextCenterX, 2 * textBaseY - verticalOffset, mPaint);
            } else {
                canvas.drawText(inStr, inTextCenterX, -verticalOffset, mPaint);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (contentSize > currentIndex) {
            if (cbInterface != null) {
                cbInterface.onItemClick(currentIndex);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mContext = null;
        if (animator != null) {
            animator.cancel();
        }
    }

    //�ص��ӿڣ�����֪ͨ�����߿ؼ���ǰ��״̬,index��ʾ��ʼ��ʾ��һ���ı�����
    public interface VerticalSwitchTextViewCbInterface {
        void showNext(int index);

        void onItemClick(int index);
    }

    public void setCbInterface(VerticalSwitchTextViewCbInterface cb) {
        cbInterface = cb;
    }
}
