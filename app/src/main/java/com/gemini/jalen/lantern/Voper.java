package com.gemini.jalen.lantern;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.viewpager.widget.ViewPager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class Voper extends ViewPager implements Runnable, LifecycleObserver {
    private Handler handler;
    //轮播频率,毫秒/次
    private int rate;
    //轮播事件,一次轮播所需时间
    private int time;
    //是否开启自动轮播
    private boolean auto;
    //是否开启无限轮播
    private boolean loop;
    //宽高比例,依靠固定计算为0的尺寸
    private float scale;
    //滑动方向
    private int orientation;
    private Vopter adapter;
    private PageTransformer transformer;
    private Boolean run;
    private boolean isLifecycled;

    public Voper(@NonNull Context context) {
        this(context, null);
    }

    public Voper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        handler = new Handler(Looper.getMainLooper());
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Voper);
        rate = a.getInt(R.styleable.Voper_voper_rate, 2000);
        time = a.getInt(R.styleable.Voper_voper_time, 1000);
        auto = a.getBoolean(R.styleable.Voper_voper_auto, false);
        loop = a.getBoolean(R.styleable.Voper_voper_loop, auto);
        scale = a.getFloat(R.styleable.Voper_voper_scale, 1.0F);
        orientation = a.getInt(R.styleable.Voper_android_orientation, 0);
        isLifecycled = a.getBoolean(R.styleable.Voper_lifecycle, true);
        a.recycle();

        try {
            Field interpolatorField = ViewPager.class.getDeclaredField("sInterpolator");
            interpolatorField.setAccessible(true);
            Interpolator interpolator = (Interpolator) interpolatorField.get(this);
            interpolatorField.setAccessible(false);
            Field scrollerField = ViewPager.class.getDeclaredField("mScroller");
            scrollerField.setAccessible(true);
            scrollerField.set(this, new Scroller(context, interpolator, true) {
                @Override
                public void startScroll(int startX, int startY, int dx, int dy, int duration) {
                    super.startScroll(startX, startY, dx, dy, time);
                }

                @Override
                public void startScroll(int startX, int startY, int dx, int dy) {
                    super.startScroll(startX, startY, dx, dy, time);
                }
            });
            scrollerField.setAccessible(false);
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), e.getMessage());
        }

        addOnPageChangeListener(adapter = new Vopter(loop, auto, this));
        setAdapter(adapter);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (height == 0 && mode == MeasureSpec.EXACTLY) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int) (width * scale), MeasureSpec.EXACTLY);
        } else {
            mode = MeasureSpec.getMode(heightMeasureSpec);
            if (width == 0 && mode == MeasureSpec.EXACTLY) {
                widthMeasureSpec = MeasureSpec.makeMeasureSpec((int) (height * scale), MeasureSpec.EXACTLY);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);
        if (transformer != null) {
            int x = getScrollX();
            int  count = getChildCount();
            for (int i = 0; i < count; i++) {
                View child = getChildAt(i);
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                if (params.isDecor) continue;
                float pos = (child.getLeft() - getPaddingLeft() - x) * 1.0F /
                        (getMeasuredWidth() - getPaddingLeft() - getPaddingRight());
                transformer.transformPage(child, pos);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(swap(ev));
        swap(ev);
        return intercepted;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN : {
                handler.removeCallbacks(this);
            }
            break;
            case MotionEvent.ACTION_CANCEL :
            case MotionEvent.ACTION_UP : {
                if (run != null && run) {
                    handler.postDelayed(this, rate);
                }
            }
        }
        return super.onTouchEvent(swap(ev));
    }

    private MotionEvent swap(MotionEvent ev) {
        if (orientation == 1) {
            int width = getWidth();
            int height = getHeight();
            ev.setLocation(ev.getY() / height * width, ev.getX() / width * height);
        }
        return ev;
    }

    @Override
    public int getCurrentItem() {
        return adapter.getPosition(super.getCurrentItem());
    }

    @Override
    public void run() {
        int position = adapter.getNextPosition();
        if (position == 1) {
            setCurrentItem(position, false);
        } else {
            setCurrentItem(position, true);
        }
        handler.postDelayed(this, rate);
    }

    public void setLifecycled(boolean lifecycled) {
        isLifecycled = lifecycled;
    }

    public boolean isLifecycled() {
        return isLifecycled;
    }

    public Voper setTransformer(PageTransformer transformer) {
        this.transformer = transformer;
        return this;
    }

    public ItemData getData(int index) {
        return adapter.getData(index);
    }

    public Voper setLoader(ItemLoader loader) {
        adapter.setLoader(loader);
        return this;
    }

    public Voper setData(List<?> data) {
        adapter.setData(data);
        return this;
    }

    public Voper setData(Object data) {
        adapter.setData(data);
        return this;
    }

    public Voper setData(Object[] data) {
        adapter.setData(Arrays.asList(data));
        return this;
    }

    public Voper clear() {
        adapter.clear();
        return this;
    }

    public Voper setLoop(boolean loop) {
        adapter.setLoop(loop);
        return this;
    }

    public Voper setAuto(boolean auto) {
        adapter.setAuto(auto);
        return this;
    }

    public void start(boolean update) {
        if (update) {
            setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        if (adapter.auto()) {
            run = true;
            handler.postDelayed(this, rate);
        }
    }

    public void start() {
        start(true);
    }

    public void stop() {
        if (run != null && run) {
            run = false;
            handler.removeCallbacks(this);
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        if (isLifecycled() && run != null && !run) {
            start(false);
        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (isLifecycled()) {
            stop();
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        if (isLifecycled()) {
            stop();
            handler = null;
            adapter.clearAll();
        }
    }
}
