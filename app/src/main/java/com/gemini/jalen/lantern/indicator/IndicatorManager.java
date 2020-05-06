package com.gemini.jalen.lantern.indicator;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class IndicatorManager extends LinearLayout implements ViewPager.OnPageChangeListener {
    private DataSetObserver observer = new DataSetObserver() {
        @Override
        public void onChanged() {
            onInvalidated();
        }

        @Override
        public void onInvalidated() {
            redo(view.getAdapter());
        }
    };
    private ViewPager view;
    private int invalid;
    private Indicator indicator;
    private Indicator.Adapter adapter;

    public IndicatorManager(Context context) {
        this(context, null);
    }

    public IndicatorManager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void redo(PagerAdapter pagerAdapter){
        int count = pagerAdapter.getCount() - invalid;
        int childCount = getChildCount();
        boolean isAdded = Math.abs(count) > childCount;
        if(indicator != null) {
            indicator.unselected();
            indicator = null;
        }
        for(int i = 0; i < Math.abs(count - childCount); i++){
            if(isAdded){
                addView(adapter.getView());
            }else{
                removeViewAt(0);
            }
        }
        setCurrentItem(view.getCurrentItem());
    }

    public void setCurrentItem(int index) {
        if (index == -1) {
            index = view.getCurrentItem();
        }
        View view = getChildAt(index);
        if (indicator != null)
            indicator.unselected();
        indicator = (Indicator) view;
        if (indicator != null)
            indicator.selected();
    }

    public IndicatorManager setAdapter(Indicator.Adapter adapter){
        this.adapter = adapter;
        return this;
    }

    public void setPager(ViewPager view) {
        setPager(view, 0);
    }

    public void setPager(ViewPager view, int invalid) {
        PagerAdapter adapter = view.getAdapter();
        if (adapter == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        this.view = view;
        this.invalid = invalid;
        this.view.addOnPageChangeListener(this);
        adapter.registerDataSetObserver(observer);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(-1);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}
}
