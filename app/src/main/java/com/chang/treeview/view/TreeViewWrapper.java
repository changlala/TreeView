package com.chang.treeview.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.chang.treeview.MyScaleGestureHandler;
import com.nineoldandroids.view.ViewHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

/**
 * TreeView的父布局
 * 处理拖动缩放等事件
 */
public class TreeViewWrapper extends FrameLayout {

    private static final String TAG = "TreeViewWrapper";

    //像一个画布一样，包含一个treeView。是TreeViewWrapper的直接子布局
    private CanvasLayout mCanvasLayout;
    //实际TreeView
    private TreeView mTreeView;

    private ScaleGestureDetector mScaleGestureDetector;
    private MyScaleGestureHandler mOnScaleGestureHandler;

    private ViewDragHelper mDragHelper;
    private Context mContext;

    //屏幕尺寸与控件尺寸相同
    private int mScreenWidth;
    private int mScreenHeight;

    public TreeViewWrapper(@NonNull Context context) {
        super(context);
    }

    public TreeViewWrapper(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

//        mTreeView = (TreeView) getChildAt(0).;


        mContext = context;

        //缩放回调
        mOnScaleGestureHandler = new MyScaleGestureHandler();
        //缩放探测器
        mScaleGestureDetector = new ScaleGestureDetector(mContext,mOnScaleGestureHandler);
        //拖拽探测器
        mDragHelper = ViewDragHelper.create(this, new ViewDragHelper.Callback() {

//            private CanvasLayout mCanvasLayout ;
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                // TODO: 2020/7/30 对mCanvasLayout解耦
                //mCanvasLayout = (CanvasLayout)child;
                return true;
            }



            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                return mDragHelper.getTouchSlop();
            }

            @Override
            public int getViewVerticalDragRange(@NonNull View child) {
                return mDragHelper.getTouchSlop();
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

                //获取当前缩放倍数
                float curScale =  mOnScaleGestureHandler.getScale();

                if (dx < 0) {
                    //手指左划

                    //计算考虑缩放后的右边界
                    int clampRight = (int)(mCanvasLayout.getMeasuredWidth() * curScale) + mCanvasLayout.getLeft();
                    Log.d(TAG, "clampViewPositionHorizontal: clampRight "+clampRight);
                    if (clampRight + dx <= mScreenWidth) {
                        //缩放后的画布宽度
                        int afterScaleWidth = (int)(mCanvasLayout.getMeasuredWidth() * curScale);
                        Log.d(TAG, "clampViewPositionHorizontal: clampRight afterScaleWidth "+clampRight+" "+afterScaleWidth);
                        return (afterScaleWidth - mScreenWidth) * -1;
                    } else {
                        return left;
                    }
                } else {
                    if (mCanvasLayout.getLeft() + dx >= 0) {
                        return 0;
                    } else {
                        return left;
                    }
                }

            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {

                Log.d(TAG, "clampViewPositionVertical: top dy " + top + " " + dy +
                        "mCanvasLayout.getTop() " + mCanvasLayout.getTop());
                float curScale =  mOnScaleGestureHandler.getScale();
                if (dy < 0) {
                    //手指上划

                    //计算考虑缩放后的下边界
                    int clampBottom = (int)(mCanvasLayout.getMeasuredHeight() * curScale) + mCanvasLayout.getTop();
                    if (clampBottom + dy <= mScreenHeight) {
                        //scale后的画布高度
                        int afterScaleHeight = (int)(mCanvasLayout.getMeasuredHeight() * curScale);
                        return (afterScaleHeight - mScreenHeight) * -1;
                    } else {
                        return top;
                    }
                } else {
                    if (mCanvasLayout.getTop() + dy >= 0) {
                        return 0;
                    } else {
                        return top;
                    }
                }
            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                super.onViewPositionChanged(changedView, left, top, dx, dy);
                Log.d(TAG, "onViewPositionChanged: l t " + left + " " + top);
                curLeft = left;
                curTop = top;
            }
        });
    }

    private int curLeft = 0;
    private int curTop = 0;


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //对子view的layout默认会放在左上角，传curLeft、curTop不起作用
//        super.onLayout(changed,curLeft,curTop,curLeft+getMeasuredWidth(), curTop+getMeasuredHeight());
        Log.d(TAG, "onLayout: changed,left,top,right,bottom" + left + top + right + bottom);

        mCanvasLayout = (CanvasLayout) getChildAt(0);
        mOnScaleGestureHandler.setmCanvasLayout(mCanvasLayout);
        mScreenWidth = getMeasuredWidth();
        mScreenHeight = getMeasuredHeight();
        //保留拖拽后的位置
        mCanvasLayout.layout(curLeft, curTop,
                curLeft + mCanvasLayout.getMeasuredWidth(), curTop + mCanvasLayout.getMeasuredHeight());
        Log.d(TAG, "onLayout: changed curleft curTop " + changed + " " + curLeft);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        mScaleGestureDetector.onTouchEvent(event);
        mDragHelper.processTouchEvent(event);

        Log.d(TAG, "onTouchEvent: event" + event.toString());

        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        boolean flag = mDragHelper.shouldInterceptTouchEvent(ev);

        if (mCanvasLayout.isNodeUnder(x, y) && ev.getAction() != MotionEvent.ACTION_DOWN) {
            flag = true;
        }
        Log.d(TAG, "onInterceptTouchEvent: 是否拦截" + flag);
        return flag;

//        return false;
    }

}
