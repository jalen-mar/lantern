package com.gemini.jalen.lantern.indicator;

import android.view.View;

public interface Indicator {
    void selected();
    void unselected();
    interface Adapter {
        View getView();
    }
}
