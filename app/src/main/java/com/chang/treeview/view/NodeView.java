package com.chang.treeview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.chang.treeview.R;
import com.chang.treeview.model.Node;
import com.chang.treeview.presenter.NodeViewContract;
import com.chang.treeview.presenter.NodeViewPresenter;

public class NodeView extends LinearLayout implements NodeViewContract.View  {

    private static final String TAG = "NodeView";
    private Node nodeValue;

    private boolean isClick = false;

    public static int ClickRectWidth = 9;

    //记录该节点子树的高度（所有的子节点高度总和） rect是相对于父布局treeview的rect
    private Rect subTreeRect;

    private NodeViewPresenter mPresenter;

    private Context mContext;

    //可拖拽点控件
//    private PointView mPointView;

    public NodeView(Context context) {
        super(context);
        mContext = context;
        //开启后才会走onDraw
        setWillNotDraw(false);
        setOrientation(HORIZONTAL);
        initClickRectPaint();
        //设置padding，值为点击框的宽度 将来点击框会占满padding
        setPadding(ClickRectWidth,ClickRectWidth,ClickRectWidth,ClickRectWidth);

    }

    public NodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NodeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public NodeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
    public Rect getSubTreeRect() {
        return subTreeRect;
    }

    public void setSubTreeRect(Rect subTreeRect) {
        this.subTreeRect = subTreeRect;
    }
    public Node getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(Node nodeValue) {
        this.nodeValue = nodeValue;
    }

    private void initPresenter(){
        mPresenter = new NodeViewPresenter();
        mPresenter.setView(this);
    }

    public Boolean addNode(Node broNode,Node newNode){

        Boolean res = false;
        res = mPresenter.addNode(broNode, newNode);

        return res;
    }

    public Boolean addSubNode(Node parentNode,Node newNode){
        Boolean res = false;
        res = mPresenter.addSubNode(parentNode, newNode);

        return res;
    }

//    private void initPointView(){
//        //允许子view超出布局绘制
//        setClipChildren(false);
//
//        //配置pointVIew属性
//        int pointviewSideLength = 20;
//        mPointView = new PointView(mContext);
//        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(pointviewSideLength,pointviewSideLength);
//        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);//true
//        lp.addRule(RelativeLayout.CENTER_VERTICAL);
//        lp.setMargins(0,0,-pointviewSideLength/2,0);
//        mPointView.setLayoutParams(lp);
//
//
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: ");
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        Log.d(TAG, "onLayout: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d(TAG, "onDraw: ");

    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        //点击焦点
        if(isClick )
            //绘制焦点框
            drawClickRect(canvas);
    }

    private Paint clickRectPaint;
    private void initClickRectPaint(){
        clickRectPaint = new Paint();
        clickRectPaint.setStrokeWidth(ClickRectWidth);
        clickRectPaint.setColor(getResources().getColor(R.color.red,null));
        clickRectPaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 绘制点击框
     *
     * 点击框将在NodeVIew内部绘制，类似于padding的效果。
     * @param canvas
     */
    private void drawClickRect(Canvas canvas){
        if(subTreeRect != null){
            Log.d(TAG, "drawClickRect: ");
            //框绘制在NodeVIew内部
            int padding = (int)clickRectPaint.getStrokeWidth()/2;
//            int padding = 0;
            Log.d(TAG, "drawClickRect: padding"+padding);
            canvas.drawRect(padding,padding,getMeasuredWidth() - padding,getMeasuredHeight() - padding,clickRectPaint);
        }
    }


    public boolean isClick() {
        return isClick;
    }

    public void setClick(boolean click) {
        isClick = click;
    }
}
