package com.chang.treeview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.chang.treeview.R;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

/**
 * NodeView上的可拖拽点 仅叶节点具有
 */
public class PointView extends View {

    private static final String TAG = "PointView";
    private int height;
    private int width;
    public PointView(Context context) {
        super(context);
        p.setStyle(Paint.Style.FILL);
        p.setColor(getResources().getColor(R.color.red,null));

    }

    public PointView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: ");
        return true;
    }

    Paint p = new Paint();
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(width/2,height/2,height/2,p);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        height = getHeight();
        width = getWidth();
    }
}
