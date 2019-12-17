package com.gemini.jalen.lantern;

import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class Vopter extends PagerAdapter implements ViewPager.OnPageChangeListener {
    private ItemLoader loader;
    private List<Object> data;
    private View[] item;

    private boolean loop;
    private boolean auto;
    private ViewPager pager;

    private int position;
    private int count;

    public Vopter(boolean loop, boolean auto, ViewPager pager) {
        this.loop = loop;
        this.auto = auto;
        this.pager = pager;
        data = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean isViewFromObject(View view, Object obj) {
        return view == obj;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = item[position];
        ItemData data = getData(position);
        if (view == null) {
            view = loader.createItem(position);
            loader.bindItem(view, data);
            item[position] = view;
        } else if (data.isUpdate()){
            loader.bindItem(view, data);
            data.setUpdate(false);
        }
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public void notifyDataSetChanged() {
        count = data.size();
        if (loop()) {
            count += 2;
            position = 1;
        } else {
            position = 0;
        }
        item = new View[count];
        super.notifyDataSetChanged();
        pager.setCurrentItem(position, true);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        this.position = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (loop() && state != 2) {
            int count = data.size();
            if (position == 0) {
                pager.setCurrentItem(count, false);
            } else if (position == count + 1) {
                pager.setCurrentItem(1, false);
            }
        }
    }

    public ItemData getData(int index) {
        ItemData result;
        index = getPosition(index);
        Object obj = data.get(index);
        if (obj instanceof ItemData) {
            result = (ItemData) obj;
        } else {
            data.set(index, result = new ItemData(obj));
        }
        return result;
    }

    public void setLoader(ItemLoader loader) {
        this.loader = loader;
    }

    public void setData(List<?> data) {
        this.data.addAll(data);
    }

    public void setData(Object data) {
        this.data.add(data);
    }

    public void clear() {
        this.data.clear();
    }

    void clearAll() {
        this.data.clear();
        this.item = null;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public boolean loop() {
        return loop && data.size() > 1;
    }

    public boolean auto() {
        return auto && loop();
    }

    public int getPosition(int index) {
        if (loop()) {
            index = (index - 1) % data.size();
            if (index < 0)
                index += data.size();
        }
        return index;
    }

    int getNextPosition() {
        return position % (data.size() + 1) + 1;
    }
}
