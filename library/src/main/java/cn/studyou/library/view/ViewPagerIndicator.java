package cn.studyou.library.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.studyou.library.R;


/**
 * 基本功能：ViewPage指示器
 * 创建：王杰
 * 创建时间：16/3/13
 * 邮箱：w489657152@gmail.com
 */
public class ViewPagerIndicator extends LinearLayout {

    private Paint paint;
    private Path path;
    private int mTriangleWidth;
    private int mTriangleHeight;
    private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F;
    private final int DIMENSION_TRIANGLE_WIDTH_MAX = (int) (getScreenWidth() / 3 * RADIO_TRIANGLE_WIDTH);
    private int mInitTranslationsX;
    private int mTranslationsX;
    private int mTabVisibleCount;
    private static final int COUNT_DEFAULT_TAB = 4;
    private static final int COLOR_TEXT_NORMAL = 0xFF2C2C2C;
    private static final int COLOR_TEXT_HIGHLIGHT = 0xFF4CAF50;
    private ViewPager mViewPager;

    public PageChangeListener mListener;
    private List<String> mTitles;

    public ViewPagerIndicator(Context context) {
        this(context, null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTabVisibleCount = typedArray.getInt(R.styleable.ViewPagerIndicator_visible_tab_count, COUNT_DEFAULT_TAB);
        if (mTabVisibleCount < 0) {
            mTabVisibleCount = COUNT_DEFAULT_TAB;
        }
        typedArray.recycle();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(COLOR_TEXT_HIGHLIGHT);
        paint.setStyle(Paint.Style.FILL);
        paint.setPathEffect(new CornerPathEffect(3));
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslationsX + mTranslationsX, getHeight());
        canvas.drawPath(path, paint);
        canvas.restore();
        super.dispatchDraw(canvas);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int cCount = getChildCount();
        if (cCount == 0)
            return;
        for (int i = 0; i < cCount; i++) {
            View view = getChildAt(i);
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.weight = 0;
            layoutParams.width = getScreenWidth() / mTabVisibleCount;
            view.setLayoutParams(layoutParams);
        }
        setItemClickEvent();

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE_WIDTH);
        mTriangleWidth = Math.min(mTriangleWidth, DIMENSION_TRIANGLE_WIDTH_MAX);
        mInitTranslationsX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;
        initTriangle();
    }

    /**
     * 初始化三角形
     */
    private void initTriangle() {

        mTriangleHeight = mTriangleWidth / 2;
        path = new Path();
        path.moveTo(0, 0);
        path.lineTo(mTriangleWidth, 0);
        path.lineTo(mTriangleWidth / 2, -mTriangleHeight);
        path.close();
    }

    /**
     * 指示器滚动
     *
     * @param position
     * @param positionOffset
     */
    public void scroll(int position, float positionOffset) {
        int tabWidth = getWidth() / mTabVisibleCount;
        mTranslationsX = (int) (tabWidth * (positionOffset + position));
        //容器移动

        if (getChildCount() > mTabVisibleCount && positionOffset > 0
                && position >= mTabVisibleCount - 1) {
            this.scrollTo((int) ((position + 1 - mTabVisibleCount) * tabWidth + tabWidth * positionOffset)
                    , 0);
        }
        invalidate();

    }

    public int getScreenWidth() {
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    public void setTabItemTitles(List<String> titles) {

        if (titles != null && titles.size() > 0) {
            this.removeAllViews();
            mTitles = titles;
            for (String title : mTitles) {
                addView(generateTextView(title));
            }
            setItemClickEvent();
        }
    }

    public void setVisibleTabCount(int count) {
        mTabVisibleCount = count;
    }

    /**
     * 根据title创建Tab
     *
     * @param title
     * @return
     */
    private View generateTextView(String title) {
        TextView textView = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.width = getScreenWidth() / mTabVisibleCount;
        textView.setText(title);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textView.setTextColor(COLOR_TEXT_NORMAL);
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public void setViewPager(ViewPager viewPager, int pos) {
        mViewPager = viewPager;
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                scroll(position, positionOffset);
                if (mListener != null) {
                    mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (mListener != null) {
                    mListener.onPageSelected(position);
                }
                highLightTextView(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mListener != null) {
                    mListener.onPageScrollStateChanged(state);
                }
            }
        });
        mViewPager.setCurrentItem(pos);
        highLightTextView(pos);
    }

    public interface PageChangeListener {
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);

    }

    public void addOnPageChangeListener(PageChangeListener listener) {
        this.mListener = listener;
    }

    /**
     * 设置高亮文本
     *
     * @param pos
     */
    private void highLightTextView(int pos) {
        resetTextViewColor();
        View view = getChildAt(pos);
        if (view instanceof TextView) {
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }

    /**
     * 重置颜色
     */
    private void resetTextViewColor() {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
            }
        }
    }

    /**
     * 设置Tab点击事件
     */
    private void setItemClickEvent() {
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            final int j = i;
            View view = getChildAt(i);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}
