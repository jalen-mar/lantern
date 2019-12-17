package com.jalen.lantern.indicator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

public class SpotIndicator extends View implements Indicator {
    private int selectedColor;
    private int unselectedColor;
    private int radius;
    private int margin;
    private Paint paint = new Paint(ANTI_ALIAS_FLAG);

    public SpotIndicator(Context context, int selectedColor, int unselectedColor, int radius, int margin) {
        super(context);
        this.selectedColor = selectedColor;
        this.unselectedColor = unselectedColor;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(unselectedColor);
        this.radius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                radius, getResources().getDisplayMetrics());
        this.margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                margin, getResources().getDisplayMetrics());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int size = (radius + margin) * 2;
        super.onMeasure(MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(radius + margin, radius + margin, radius, getPaint());
    }

    @Override
    public void selected() {
        paint.setColor(selectedColor);
        invalidate();
    }

    @Override
    public void unselected() {
        paint.setColor(unselectedColor);
        invalidate();
    }

    private Paint getPaint(){
        if(paint.getColor() == 0)
            paint.setColor(unselectedColor);
        return paint;
    }
}
