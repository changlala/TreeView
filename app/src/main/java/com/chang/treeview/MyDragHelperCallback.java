package com.chang.treeview;

import android.util.Log;
import android.view.View;

import com.chang.treeview.view.CanvasLayout;
import com.chang.treeview.view.TreeViewWrapper;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

public class MyDragHelperCallback extends ViewDragHelper.Callback {

    private TreeViewWrapper mTreeViewWrapper;
    private CanvasLayout mCanvasLayout;
    private static final String TAG = "MyDragHelperCallback";
    public MyDragHelperCallback(TreeViewWrapper treeViewWrapper) {
        this.mTreeViewWrapper = treeViewWrapper;
//        this.mCanvasLayout = treeViewWrapper.getCanvasLayout();
    }


    @Override
    public int getViewHorizontalDragRange(@NonNull View child) {
        return 1;
    }

    @Override
    public int getViewVerticalDragRange(@NonNull View child) {
        return 1;
    }

    /**
     * 对水平方向上的拖拽进行限制
     *
     * 考虑缩放对右边界的影响，不需要考虑左边界，因为锚点在左上角。
     *
     * 如果后续需要将锚点改为手指中点，则还需要考虑左边界
     *
     * @param child
     * @param left
     * @param dx
     * @return 此次拖拽后更新的view top值
     */
    @Override
    public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {

        mCanvasLayout = mTreeViewWrapper.getCanvasLayout();
        //获取当前缩放倍数
//        float curScale =  mTreeViewWrapper.getOnScaleGestureHandler().getScale();
        float curScale =  mCanvasLayout.getScaleX();
        Log.d(TAG, "clampViewPositionHorizontal: 缩放倍数 left dx "+curScale+" "+left+" "+dx);

        if (dx < 0) {
            //手指左划

            //计算考虑缩放后的右边界
            int clampRight = (int)(mCanvasLayout.getMeasuredWidth() * curScale) + mCanvasLayout.getLeft();
            Log.d(TAG, "clampViewPositionHorizontal: clampRight "+clampRight);
            if (clampRight + dx <= mTreeViewWrapper.getScreenWidth()) {
                //缩放后的画布宽度
                int afterScaleWidth = (int)(mCanvasLayout.getMeasuredWidth() * curScale);
                Log.d(TAG, "clampViewPositionHorizontal: clampRight afterScaleWidth "+clampRight+" "+afterScaleWidth);

                int res = (afterScaleWidth - mTreeViewWrapper.getScreenWidth()) * -1;
                Log.d(TAG, "clampViewPositionHorizontal: dx小于零 return res "+res);
                return res;
            } else {
                Log.d(TAG, "clampViewPositionHorizontal: dx小于零 return left "+left);
                return left;
            }
        } else {
            if (mCanvasLayout.getLeft() + dx >= 0) {
                Log.d(TAG, "clampViewPositionHorizontal: dx大于零 return 0");
                return 0;
            } else {
                Log.d(TAG, "clampViewPositionHorizontal: dx大于零 return left "+left);
                return left;
            }
        }

    }

    @Override
    public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
        mCanvasLayout = mTreeViewWrapper.getCanvasLayout();
//        float curScale =  mTreeViewWrapper.getOnScaleGestureHandler().getScale();
        float curScale =  mCanvasLayout.getScaleX();

        Log.d(TAG, "clampViewPositionVertical: 缩放倍数 top dy "+curScale+" "+ top + " " + dy +
                "mCanvasLayout.getTop() " + mCanvasLayout.getTop());
        if (dy < 0) {
            //手指上划
            //计算考虑缩放后的下边界
            int clampBottom = (int)(mCanvasLayout.getMeasuredHeight() * curScale) + mCanvasLayout.getTop();
//            int clampBottom = (int)((mCanvasLayout.getMeasuredHeight() + mCanvasLayout.getTop() )* curScale);
            Log.d(TAG, "clampViewPositionVertical: clampBottom "+clampBottom);
            if (clampBottom + dy <= mTreeViewWrapper.getScreenHeight()) {
                //scale后的画布高度
                int afterScaleHeight = (int)(mCanvasLayout.getMeasuredHeight() * curScale);
                int res = (afterScaleHeight - mTreeViewWrapper.getScreenHeight()) * -1;
                Log.d(TAG, "clampViewPositionVertical: dy小于零 return res "+res);
                return res;
            } else {
                Log.d(TAG, "clampViewPositionVertical: dy大于零 return top "+top);
                return top;
            }
        } else {
            if (mCanvasLayout.getTop() + dy >= 0) {
                Log.d(TAG, "clampViewPositionVertical: dy大于零 return 0");
                return 0;
            } else {
                Log.d(TAG, "clampViewPositionVertical: dy大于零 return top "+top);
                return top;
            }
        }
    }

    @Override
    public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
        super.onViewPositionChanged(changedView, left, top, dx, dy);
        Log.d(TAG, "onViewPositionChanged: l t " + left + " " + top);
        mTreeViewWrapper.setCurLeft(left);
        mTreeViewWrapper.setCurTop(top);
    }

    @Override
    public boolean tryCaptureView(@NonNull View child, int pointerId) {
        return true;
    }

    @Override
    public void onViewDragStateChanged(int state) {
        Log.d(TAG, "onViewDragStateChanged: "+state);
    }
}
