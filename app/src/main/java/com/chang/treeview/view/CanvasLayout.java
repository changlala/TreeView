package com.chang.treeview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CanvasLayout extends FrameLayout implements ViewGroup.OnHierarchyChangeListener {
    private static final String TAG = "CanvasLayout";
    private TreeView mTreeView;
    public CanvasLayout(@NonNull Context context) {
        super(context);
    }

    public CanvasLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: changed,left,top,right,bottom"+left+top+right+bottom);
    }

    /**
     *
     * 判断wrapperlayout坐标系下的x，y是否存在Node
     *
     * 需要考虑缩放
     * @param x parent(treewrapper)坐标系下
     * @param y
     * @return
     */
    public boolean isNodeUnder(int x , int y){

        x -= getLeft();
        y -= getTop();

        TreeView treeView = (TreeView) getChildAt(0);
        int treeViewOffsetX = treeView.getLeft();
        int treeViewOffsetY = treeView.getTop();

        x -= treeViewOffsetX;
        y -= treeViewOffsetY;

        Log.d(TAG, "isNodeUnder: before scale x y "+x+" "+y);
        //考虑缩放 由于实际上子view的ltrb并没有变 所以需要换算一下触摸点的坐标
        x /= getScaleX();
        y /= getScaleX();

        final int childCount = treeView.getChildCount();
        for(int i = 0 ; i <childCount ; i++){
            View view = treeView.getChildAt(i);
            if(i == 3) {
                Log.d(TAG, "isNodeUnder:afterscale x y scale" + x + " " + y+" "+getScaleX());
                Log.d(TAG, "isNodeUnder: n4 t ,l,r,b " + view.getTop() + " " + view.getLeft() +
                        " " + view.getRight() + " " + view.getBottom());
            }
            if(x >= view.getLeft()
                    && x < view.getRight()
                    && y >= view.getTop()
                    && y < view.getBottom()){
                Log.d(TAG, "isNodeUnder: return true");
                return true;
            }
        }
        Log.d(TAG, "isNodeUnder: return false");
        return false;
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        mTreeView = (TreeView)child;
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {

    }
}
