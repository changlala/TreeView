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

import com.chang.treeview.MyDragHelperCallback;
import com.chang.treeview.MyScaleGestureHandler;
import com.nineoldandroids.view.ViewHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

/**
 * TreeView的父布局
 * 处理拖动缩放等事件
 */
public class TreeViewWrapper extends FrameLayout implements ViewGroup.OnHierarchyChangeListener{

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
        mContext = context;

        //缩放回调
        mOnScaleGestureHandler = new MyScaleGestureHandler(this);
        //缩放探测器
        mScaleGestureDetector = new ScaleGestureDetector(mContext,mOnScaleGestureHandler);
        //拖拽探测器
        mDragHelper = ViewDragHelper.create(this,new MyDragHelperCallback(this));

        setOnHierarchyChangeListener(this);
    }

    private int curLeft = 0;
    private int curTop = 0;


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //对子view的layout默认会放在左上角，传curLeft、curTop不起作用
//        super.onLayout(changed,curLeft,curTop,curLeft+getMeasuredWidth(), curTop+getMeasuredHeight());
        Log.d(TAG, "onLayout: changed,left,top,right,bottom" + left + top + right + bottom);

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
        boolean flag = mDragHelper.shouldInterceptTouchEvent(ev);
        Log.d(TAG, "onInterceptTouchEvent: "+ev.getAction()+" "+flag);

        // TODO: 2020/9/4 多指情况下如果触摸到了node的情形是否需要拦截？

        //当前是互动模式
        if(!mTreeView.isInAutoLayoutMode()){

            if(ev.getPointerCount() <= 1){
                //单指

                //第二个判断保证在MOVE幅度较大，触点可能在一些时刻脱离Nodeview 此时wrapper不应该拦截执行画布滑动
                //如果不加第二个判断，会出现滑动子view时MOVE滑动幅度较大时触点脱离NodeView isNodeUnder为false被拦截下来的情况
                if(mCanvasLayout.isNodeUnder((int)ev.getX(),(int)ev.getY())
                    ||mTreeView.getDragHelper().getViewDragState() == ViewDragHelper.STATE_DRAGGING){
                    return false;
                }
            }else if(ev.getPointerCount() == 2){
                //多点触摸的情况
                //对第一种情况的处理
                //取第二指的坐标
                float x = ev.getX(ev.getActionIndex());
                float y = ev.getY(ev.getActionIndex());
                //如果第二指在NodeVIew 默认拦截
                if(mCanvasLayout.isNodeUnder((int)x,(int)y)){
                    return true;
                }
            }
            //触摸位置在节点上 并且触摸事件不是第二指的情况下 不拦截
            //第二个条件用于在多指的情况下，如果第二指触碰到了NodeView会进行拦截。
            //例如放大手势中的第二指就算点到了NodeVIew，也会被拦截执行缩放而不是继续下发执行拖动
//            if(mCanvasLayout.isNodeUnder((int)ev.getX(),(int)ev.getY())){
//                if(ev.getAction() != MotionEvent.ACTION_POINTER_DOWN){
//                    return false;
//                }else{
//                    return true;
//                }
//            }


            //如果触摸位置不在节点但是当前节点正处于拖拽状态，也不拦截
//            if(mTreeView.getDragHelper().getViewDragState() == ViewDragHelper.STATE_DRAGGING){
//                return false;
//            }
        }
        return flag;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: ");
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        Log.d(TAG, "onChildViewAdded: parent child"+parent+" "+child);
        //取得子view实例
        mCanvasLayout = (CanvasLayout)child;
        mTreeView = (TreeView) mCanvasLayout.getChildAt(0);
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {

    }

    public CanvasLayout getCanvasLayout() {
        return mCanvasLayout;
    }

    public MyScaleGestureHandler getOnScaleGestureHandler() {
        return mOnScaleGestureHandler;
    }

    public int getScreenWidth() {
        return mScreenWidth;
    }

    public int getScreenHeight() {
        return mScreenHeight;
    }

    public void setCurLeft(int curLeft) {
        this.curLeft = curLeft;
    }

    public void setCurTop(int curTop) {
        this.curTop = curTop;
    }
}
