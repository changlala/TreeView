package com.chang.treeview.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CanvasLayout extends FrameLayout{
    private static final String TAG = "CanvasLayout";
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

        final int childCount = treeView.getChildCount();
        for(int i = 0 ; i <childCount ; i++){
            View view = treeView.getChildAt(i);
            if(x >= view.getLeft()
                    && x < view.getRight()
                    && y >= view.getTop()
                    && y < view.getBottom()){
                return true;
            }
        }
        return false;
    }
}
