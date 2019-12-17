package com.gemini.jalen.lantern;

import android.view.View;

public interface ItemLoader<V extends View> {
    V createItem(int index);
    void bindItem(V view, ItemData data);
}
