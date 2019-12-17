package com.jalen.lantern.transformer;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class CardTransformer implements ViewPager.PageTransformer {
    private static final float SCALE = 0.7F;

    @Override
    public void transformPage(View page, float position) {
        float scale = Math.max(SCALE, 1 - Math.abs(position));
        page.setAlpha(scale);
        page.setScaleX(scale);
        page.setScaleY(scale);
    }
}
