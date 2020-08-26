package com.chang.treeview;

import android.util.Log;
import android.view.ScaleGestureDetector;

import com.chang.treeview.view.CanvasLayout;

public class MyScaleGestureHandler implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = "MyScaleGestureHandler";

    private float preScale = 1f;
    private float scale = 1f;
    private boolean firstTime = true;

    private CanvasLayout mCanvasLayout;

    public CanvasLayout getmCanvasLayout() {
        return mCanvasLayout;
    }

    public void setmCanvasLayout(CanvasLayout mCanvasLayout) {
        this.mCanvasLayout = mCanvasLayout;
    }

    public float getScale() {
        return scale;
    }


    /**
     *
     * @param detector
     * @return 返回true仅会对previousSpan进行更新 ；false仅不对previousSpan更新 不影响scaleEnd()的调用
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float previousSpan = detector.getPreviousSpan();//scaleEnd()调用后更新
        float currentSpan = detector.getCurrentSpan();
        if (currentSpan < previousSpan) {
            // 缩小
            scale = preScale - (previousSpan - currentSpan) / 500;
        } else {
            // 放大
            scale = preScale + (currentSpan - previousSpan) / 500;
        }

        //限制缩放倍数
        if (scale > 3 || scale < 0.5) {

            if (firstTime)
                preScale = scale;
            Log.d(TAG, "onScale: return true scale" + " " + scale);
            Log.d(TAG, "onScale: prevSpan curSpan " + previousSpan + " " + currentSpan);
            firstTime = false;
            return true;
        }

        firstTime = true;

        Log.d(TAG, "onScale: scale" + scale);
        Log.d(TAG, "onScale: prevSpan curSpan " + previousSpan + " " + currentSpan);
        // 左上角不动
        mCanvasLayout.setPivotX(0);
        mCanvasLayout.setPivotY(0);
        mCanvasLayout.setScaleX(scale);
        mCanvasLayout.setScaleY(scale);


        Log.d(TAG, "onScale: scalFactor " + detector.getScaleFactor() + " wrapper left scalX " +
                mCanvasLayout.getLeft() + " " + mCanvasLayout.getScaleX());


        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        Log.d(TAG, "onScaleBegin: ");
        return mCanvasLayout != null;
    }

    /**
     * 是否调用onScaleEnd与onScale()返回值无关，只与MotionEvent的事件有关
     * @param detector
     */
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        preScale = scale;
        firstTime = true;
        Log.d(TAG, "onScaleEnd: ");
    }


}
