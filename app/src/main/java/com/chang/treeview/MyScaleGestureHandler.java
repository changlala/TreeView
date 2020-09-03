package com.chang.treeview;

import android.util.Log;
import android.view.ScaleGestureDetector;

import com.chang.treeview.view.CanvasLayout;
import com.chang.treeview.view.TreeViewWrapper;

public class MyScaleGestureHandler implements ScaleGestureDetector.OnScaleGestureListener {
    private static final String TAG = "MyScaleGestureHandler";

    private float preScale = 1f;
    private float scale = 1f;
    private boolean firstTime = true;

    private TreeViewWrapper mTreeViewWapper;
    private CanvasLayout mCanvasLayout;
    public float getScale() {
        return scale;
    }


    public MyScaleGestureHandler(TreeViewWrapper mTreeViewWapper) {
        this.mTreeViewWapper = mTreeViewWapper;
    }

    /**
     *
     * @param detector
     * @return 返回true仅会对previousSpan进行更新 ；false仅不对previousSpan更新 不影响scaleEnd()的调用
     */
    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float previousSpan = detector.getPreviousSpan();//只有返回true的时候才更新
        float currentSpan = detector.getCurrentSpan();

        float tempScale;

        /**
         * preScale在一个事件流结束时得到赋值，事件流进行中不会修改。
         * 除非缩放倍数超出或小于阈值
         */
        if (currentSpan < previousSpan) {
            // 缩小
            scale = preScale - (previousSpan - currentSpan) / 500;
        } else {
            // 放大
            scale = preScale + (currentSpan - previousSpan) / 500;
        }

        //限制缩放倍数
        if (scale > 3 || scale < 0.5) {

            //超出或小于缩放阈值，需要保存preScale
            if(firstTime){
                if(scale > 3){
                    preScale = 3f;
                }else if(scale <0.5){
                    preScale = 0.5f;
                }
            }
            Log.d(TAG, "onScale: return true prescale scale "+preScale + " " + scale);
            Log.d(TAG, "onScale: prevSpan curSpan " + previousSpan + " " + currentSpan);
            firstTime = false;
            return true;
        }

//        scale = tempScale;
        firstTime = true;

        Log.d(TAG, "onScale: scale" + scale);
        Log.d(TAG, "onScale: prevSpan curSpan " + previousSpan + " " + currentSpan);
        // 左上角不动
        mCanvasLayout.setPivotX(0  );
        mCanvasLayout.setPivotY(0);
        mCanvasLayout.setScaleX(scale);
        mCanvasLayout.setScaleY(scale);


        Log.d(TAG, "onScale: scalFactor " + detector.getScaleFactor() + " wrapper left scalX " +
                mCanvasLayout.getLeft() + " " + mCanvasLayout.getScaleX());


        //返回false不改变preScale
        return false;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        mCanvasLayout = mTreeViewWapper.getCanvasLayout();

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
