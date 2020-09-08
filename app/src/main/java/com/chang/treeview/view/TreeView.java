package com.chang.treeview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chang.treeview.R;
import com.chang.treeview.Util;
import com.chang.treeview.model.ConfigFile;
import com.chang.treeview.model.Node;
import com.chang.treeview.model.Tree;
import com.chang.treeview.presenter.TreeViewContract;
import com.chang.treeview.presenter.TreeViewPresenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import androidx.customview.widget.ViewDragHelper;

import static com.chang.treeview.DensityUtils.dp2px;

public class TreeView extends ViewGroup implements TreeViewContract.View , ViewGroup.OnHierarchyChangeListener {

    private static final String TAG = "TreeView";
    private TreeViewContract.Presenter mPresenter;

    //layout代理
    private TreeViewLayoutManager mLayoutManager;

    private Tree mTree;

    private NodeView mHeadView;

    //TreeView的宽高
    private int mWidth = 1000;
    private int mHeight = 1000;
    //当前处于点击状态的NodeView
    private NodeView mCurClickNodeView;
    //子view样式生成器
    private ChildViewCustomer mChildViewCustomer;

    private Context mContext;
    private ViewDragHelper mDragHelper;

    private int curTop = 0;
    private int curleft = 0;

        /**
         * true 自动layout模式，使用layoutManager进行layout，结点不可拖动，添加删除子view需要通过按钮。
         *
         * false 互动模式，NodeVIew可拖拽，每个nodeview右边都会显示一个按钮用于拖拽创建子节点。
         */
        private boolean inAutoLayoutMode = true;

    //存储所有节点和其对应的PointVIew
    private ConcurrentHashMap<NodeView,PointView> mPointViewMap = new ConcurrentHashMap<>();
    private static int POINTVIEW_WIDTH = 70;

    private ArrayList<NodeView> mNodeViewList = new ArrayList();

    //互动模式下当前拖拽创建的nodeview
    private NodeView mNVcreatedByPoint;

    public boolean isInAutoLayoutMode() {
        return inAutoLayoutMode;
    }

    public void setInAutoLayoutMode(boolean inAutoLayoutMode) {
        this.inAutoLayoutMode = inAutoLayoutMode;
        if(inAutoLayoutMode){
            changePointViewVisible(false);
        }else{
            if(mPointViewMap.size() != 0)
                changePointViewVisible(true);
        }

        requestLayout();
        invalidate();
    }

    public ViewDragHelper getDragHelper() {
        return mDragHelper;
    }

    public TreeView(Context context) {
        super(context);
    }

    public TreeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initPresenter();
        //初始化连线paint
        initLinePaint();

        //给文件保存路径赋值
        String filePath = context.getFilesDir().getPath();
        ConfigFile.setFilePath(filePath);

        //draghelper
        mDragHelper = ViewDragHelper.create(this,new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull View child, int pointerId) {
                if(child instanceof NodeView){
                    NodeView nv = (NodeView)child;

                    //头节点不可移动
                    if(nv.getNodeValue().equals(mTree.getHead())){
                        return false;
                    }
                }else if(child instanceof PointView){

                }

                return true;
            }

            @Override
            public int getViewHorizontalDragRange(@NonNull View child) {
                return 1;
            }

            @Override
            public int getViewVerticalDragRange(@NonNull View child) {
                return 1;
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
                // TODO: 2020/9/4 需要限制拖拽范围
                Log.d(TAG, "clampViewPositionHorizontal: left "+left);
                return left;
            }

            @Override
            public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
                Log.d(TAG, "clampViewPositionVertical: top "+top);
                return top;
            }

            @Override
            public void onViewDragStateChanged(int state) {
                Log.d(TAG, "onViewDragStateChanged: state "+state);
                super.onViewDragStateChanged(state);
            }

            @Override
            public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
                if(releasedChild instanceof PointView){
                    //拖拽结束，移除pv
//                    Iterator<NodeView> it = mPointViewMap.keySet().iterator();
//                    while (it.hasNext()){
//                        NodeView nv = it.next();
//                        if(mPointViewMap.get(nv) == (PointView)releasedChild){
//                            removeView(releasedChild);
//                            mPointViewMap.remove(nv);
//                        }
//                    }
                    requestLayout();
                    mNVcreatedByPoint = null;
                }
//                Log.d(TAG, "onViewReleased: child t l r b"
//                        +((NodeView)releasedChild).getNodeValue().getName()
//                        +" "+releasedChild.getTop()+
//                        " "+ releasedChild.getLeft()+" "+
//                        releasedChild.getRight()+" "+
//                        releasedChild.getBottom());
            }

            @Override
            public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
                if(capturedChild instanceof PointView){
                    //拖拽point 创建NodeView
                    int l ,t;
                    l = capturedChild.getLeft();
                    t = capturedChild.getTop();
                    NodeView nv = findNodeViewByPointView((PointView)capturedChild);
                    //更新数据
                    Node n = new Node();
                    n.setName("add subNode by point");
                    nv.getNodeValue().getChildren().add(n);

                    //更新view
                    NodeView newNv = new NodeView(mContext);
                    newNv.setNodeValue(n);
                    newNv.addView(generateCustomView(n));
                    addView(newNv);
                    mNodeViewList.add(newNv);

                    mNVcreatedByPoint = newNv;

                }
//                Log.d(TAG, "onViewCaptured: "+ ((NodeView)capturedChild).getNodeValue().getName());
            }

            @Override
            public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
                if(changedView instanceof PointView){

                }
                /**
                 * 核心 每次drag结束后 都会调用重新measure layout刷新界面
                 */
                refreshView();
            }
        });

        //设置view树变动回调
        setOnHierarchyChangeListener(this);

        //nodeview添加完成后 添加pointview
        post(new Runnable() {
            @Override
            public void run() {
                clearAndFillPointViewHashMap();
                changePointViewVisible(false);
            }
        });

    }


    public TreeViewLayoutManager getmLayoutManager() {
        return mLayoutManager;
    }


    public void setmLayoutManager(TreeViewLayoutManager mLayoutManager) {
        this.mLayoutManager = mLayoutManager;
    }

    private void initPresenter(){
        mPresenter = new TreeViewPresenter(this);
    }

    public int getmWidth() {
        return mWidth;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public int getmHeight() {
        return mHeight;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
    }

    /**
     * 从保存文件读取树形图
     * @param fileName 若为null，加载一个只包含头结点的默认图
     */
    public void setConfigFile(String fileName){
        if(fileName == null){
            mTree = mPresenter.loadDefaultTree();
        }else{
            mTree = mPresenter.loadTreeFromFile(fileName);
        }

        if(mTree == null){
            showInfo("加载文件出错");
            return;
        }
        fillChildView();
    }

    /**
     * 直接读取Tree构造树形图
     * @param tree
     */
    public void setTree(Tree tree){
        mTree = tree;
        fillChildView();
        mPresenter.loadTreeFromCode(mTree);
    }

    public Tree getTree() {
        return mTree;
    }


    /**
     * 层次遍历tree 构造所有的NodeView并添加到TreeView
     */
    private void fillChildView(){
        if(mTree != null){
            Node head = mTree.getHead();
            List<Node> nodeList = new ArrayList<>();
            nodeList.add(head);
            for (int i = 0 ; i <nodeList.size() ; i++) {
                Node curNode = nodeList.get(i);

                NodeView nv = new NodeView(mContext);
                nv.setNodeValue(curNode);
                //生成内容子view并填充
                nv.addView(generateCustomView(curNode));
                addView(nv);
                mNodeViewList.add(nv);

                List<Node> children = curNode.getChildren();
                nodeList.addAll(children);
            }

            mHeadView = getNodeView(head);
        }
    }

    /**
     * 生成NodeView的内容布局
     *
     *  若未设置ChildViewCustomer 则生成一个TextView
     * @param curNode
     *
     * @return
     */
    private View generateCustomView(Node curNode){
        //mChildViewCustomer，直接调用
        if(mChildViewCustomer != null)
            return mChildViewCustomer.generateChildView(curNode);

        //暂定NodeView包裹着一个TextView
        TextView tv = new TextView(mContext);
        LayoutParams lp;
        if(curNode.getName().contains("n4")){
            //测试父View高度大于子树高度情况
            lp = new LayoutParams( LayoutParams.WRAP_CONTENT, 300);
        }else{
            lp = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        tv.setText(curNode.getName());
        tv.setBackgroundColor(getResources().getColor(R.color.colorAccent,null));
        tv.setLayoutParams(lp);

        return tv;
    }

    /**
     * 获得TreeView中Node对应的NodeView
     * @param n
     * @return
     */
    public NodeView getNodeView(Node n){
        for(NodeView nv : mNodeViewList){
            Node node = nv.getNodeValue();
            if(node.getId() == n.getId())
                return nv;
        }

        return null;
    }

    //画出每个节点的子树框 测试用
    private void drawSubTreeRect(Canvas canvas){

        Paint p = new Paint();
        p.setColor(getResources().getColor(R.color.red,null));
        p.setStrokeWidth(4);
        p.setStyle(Paint.Style.STROKE);
        for(NodeView nv: mNodeViewList){
            try{
                canvas.drawRect(nv.getSubTreeRect(),p);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int size = getChildCount();

        Log.d(TAG, "onMeasure: ");
        //通知每个child进行一次measure
        for (int i = 0; i < size; i++) {
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }

        if (inAutoLayoutMode) {
            //按照layoutManager自动布局
            if(mLayoutManager!= null && mTree != null){

                Rect treeRect = null;
                //默认布局到画布左上角
                treeRect = mLayoutManager.onLayout(mTree,0,0);

                setMeasuredDimension(treeRect.width(),treeRect.height());

                mWidth = treeRect.width();
                mHeight = treeRect.height();
            }

        }else{

            //可拖拽模式
            int t = Integer.MAX_VALUE,l = Integer.MAX_VALUE,r = Integer.MIN_VALUE,b = Integer.MIN_VALUE;
            //遍历所有nodeview，获得所有子view中l，t的最小值，r、b的最大值
            for(NodeView child : mNodeViewList){
                //scale改变前后 child的tlrb值是不会变的
                if(child.getTop() < t)
                    t = child.getTop();
                if(child.getLeft() < l)
                    l = child.getLeft();
                if(child.getRight() > r)
                    r = child.getRight();
                if(child.getBottom() > b)
                    b = child.getBottom();
            }

            Rect treeR = new Rect(l,t,r,b);

            setMeasuredDimension(treeR.width()+POINTVIEW_WIDTH/2,treeR.height());
            mWidth = treeR.width();
            mHeight = treeR.height();

        }


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        Log.d(TAG, "onLayout: changed,left,top,right,bottom"+l+t+r+b);
        Log.d(TAG, "onLayout: l t"+l+" "+t);
        if(mLayoutManager!= null && mTree != null && inAutoLayoutMode){
            //默认布局到画布左上角
            mLayoutManager.onLayout(mTree,0,0);
        }

        //互动模式
        if(!inAutoLayoutMode){

            //当前pointview正在被拖拽 更新mNVcreatedByPoint位置
            if(mDragHelper.getCapturedView() instanceof PointView && mDragHelper.getViewDragState() == ViewDragHelper.STATE_DRAGGING){
                //更新mNVcreatedByPoint位置
                View captureView = mDragHelper.getCapturedView();
                mNVcreatedByPoint.layout(captureView.getLeft(),captureView.getTop(),
                        captureView.getLeft()+mNVcreatedByPoint.getMeasuredWidth(),captureView.getTop()+mNVcreatedByPoint.getMeasuredHeight());
                return;
            }

            //layout pointview在nodeview右边中点
            Set nvSet = mPointViewMap.keySet();
            Iterator it = nvSet.iterator();
            while (it.hasNext()){
                NodeView nv = (NodeView) it.next();
                PointView pv = mPointViewMap.get(nv);


                int left,bottom,right,top;

                //pv大小不能比nv大
//                if(pv.getMeasuredHeight() > nv.getMeasuredHeight() ||
//                        pv.getMeasuredWidth() > nv.getMeasuredWidth()){
//                    try {
//                        throw new Exception();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
                left = nv.getRight() - pv.getMeasuredWidth()/2;
                right = nv.getRight()+ pv.getMeasuredWidth()/2;
                top = nv.getTop() +nv.getMeasuredHeight()/2-pv.getMeasuredHeight()/2;
                bottom = nv.getTop()+nv.getMeasuredHeight()/2 + pv.getMeasuredHeight()/2;

                //layout pointview，在NodeView的有边框中部
                pv.layout(left,top,right,bottom);

            }
        }

    }

    /**
     * 处理NodeView的触摸事件
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        Log.d(TAG, "onTouchEvent: "+event);

        if(event.getAction() == MotionEvent.ACTION_DOWN){
            //触摸到NodeView，设置点击状态
            NodeView clickedNodeView = getNodeViewUnderXY(x,y);
            if(clickedNodeView != null){
                setClickNode(clickedNodeView);
                Log.d(TAG, "点击了NodeView "+clickedNodeView.getNodeValue().getName());
                //直接调用TreeView.invalidate()是无法调用到全体NodeView的onDraw，好像是因为view缓存判断所有
                //NodeVIew没有发生变化，所以不进行invalidate
//                invalidate();

//                return false;
            }
        }

        if(!inAutoLayoutMode)
            mDragHelper.processTouchEvent(event);


        return true;
    }

    /**
     * 拦截所有事件
     *
     * NodeView的点击事件由自身处理
     *
     * @param ev
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    /**
     * 返回 x,y 点下面的NodeView
     *
     * @param x TreeView坐标系下的
     * @param y
     * @return
     */
    private NodeView getNodeViewUnderXY(int x, int y){

        for(NodeView nv: mNodeViewList){
            if(x >= nv.getLeft()
                    && x < nv.getRight()
                    && y >= nv.getTop()
                    && y < nv.getBottom()) {
                return nv;
            }
        }
        return null;
    }

    /**
     * 搜索pointViewHashMap，查找和pv关联的nodeview
     * @param pv
     * @return
     */
    private NodeView findNodeViewByPointView(PointView pv){
        Iterator<NodeView> it = mPointViewMap.keySet().iterator();
        while (it.hasNext()){
            NodeView nv = it.next();
            if(mPointViewMap.get(nv) == pv){
                return nv;
            }
        }
        return null;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        //绘制连接线
        if(mTree != null)
            drawNodeLine(canvas);

        //test
//        drawSubTreeRect(canvas);


        Log.d(TAG, "dispatchDraw: ");
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }


    /**
     * psresenter调用
     *
     * 用于显示Toast
     *
     * @param info
     */
    @Override
    public void showInfo(String info) {

        Util.showToast(mContext,info);

    }

    private Paint mLinePaint;

    //连接线用paint
    private void initLinePaint(){
        mLinePaint = new Paint();
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setColor(getResources().getColor(R.color.colorPrimary,null));
        mLinePaint.setStrokeWidth(10);
    }

    /**
     * 节点之间连线绘制
     *
     * @return
     */
    private void drawNodeLine(Canvas canvas){

        List<Path> nodeLinePathList = new ArrayList<>();
        List<NodeView> nodeViewList = new ArrayList<>();
        nodeViewList.add(mHeadView);
        for (int i = 0 ; i <nodeViewList.size() ; i++) {
            NodeView curView = nodeViewList.get(i);

            List<Node> children = curView.getNodeValue().getChildren();
            for (Node child : children) {

                NodeView childView = getNodeView(child);
                nodeViewList.add(childView);
                nodeLinePathList.add(drawChildNodeLine(curView,childView));
            }
        }

        for (Path path : nodeLinePathList) {
            canvas.drawPath(path,mLinePaint);
        }
    }

    /**
     * 绘制连线 目前用贝塞尔曲线绘制
     * @param from
     * @param to
     * @return
     */
    private Path drawChildNodeLine(NodeView from ,NodeView to){
        Path p = new Path();

        int top = from.getTop();
        int formY = top + from.getMeasuredHeight() / 2;
        int formX = from.getRight();

        int top1 = to.getTop();
        int toY = top1 + to.getMeasuredHeight() / 2;
        int toX = to.getLeft();

        p.reset();
        p.moveTo(formX, formY);
        p.quadTo(toX - dp2px(mContext, 15), toY, toX, toY);

        return p;
    }


    /**
     * psresenter添加完成后的回调
     */
    @Override
    public void refreshView() {
        requestLayout();
    }


    /**
     * 更新mPointViewHashMap
     *
     * NodeView添加完成后调用
     */
    private void clearAndFillPointViewHashMap(){

        //找到所有叶节点 存入数组
        List<NodeView> nodeViewList = new ArrayList<>();
        for(NodeView nv : mNodeViewList){
//            if(nv.getNodeValue().getChildren().size() == 0)
//                nodeViewList.add(nv);
            nodeViewList.add(nv);
        }

        removeAllPointView();
        mPointViewMap.clear();

        //更新hashmap
        for(NodeView nv : nodeViewList){
            PointView pv = new PointView(mContext);
            //设置宽高
            LayoutParams lp = new LayoutParams(POINTVIEW_WIDTH,POINTVIEW_WIDTH);
            pv.setLayoutParams(lp);
            pv.bringToFront();
            addView(pv);
            mPointViewMap.put(nv,pv);
        }
    }
    //移除mPointViewHashMap中保存的所有PointView
    private void removeAllPointView(){
        Set<NodeView> pvSet = mPointViewMap.keySet();
        Iterator<NodeView> it = pvSet.iterator();
        while (it.hasNext()){
            NodeView nv = it.next();
            PointView pv = mPointViewMap.get(nv);
            removeView(pv);
        }
    }

    private void changePointViewVisible(boolean isVisible){
        Set<NodeView> pvSet = mPointViewMap.keySet();
        Iterator<NodeView> it = pvSet.iterator();
        while (it.hasNext()){
            NodeView nv = it.next();
            PointView pv = mPointViewMap.get(nv);
            if (isVisible) {
                pv.setVisibility(View.VISIBLE);
            } else {
                pv.setVisibility(INVISIBLE);
            }

        }
    }

    /**
     * OnHirrachyChangeListener 接口覆写方法
     *
     * 当有子view添加时
     */

    @Override
    public void onChildViewAdded(View parent, View child) {
        if(child instanceof NodeView){
            NodeView nv = (NodeView)child;
            if(nv.getNodeValue().getChildren().size() == 0){
                //当前child是叶节点
                PointView pv = new PointView(mContext);
                //设置宽高
                LayoutParams lp = new LayoutParams(POINTVIEW_WIDTH,POINTVIEW_WIDTH);
                pv.setLayoutParams(lp);
                pv.bringToFront();
                if(isInAutoLayoutMode()){
                    pv.setVisibility(INVISIBLE);
                }else{
                    pv.setVisibility(VISIBLE);
                }
                addView(pv);
                mPointViewMap.put(nv,pv);
            }
        }
//            needUpdatePointView = true;
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        if(child instanceof NodeView){
            NodeView nv = (NodeView)child;
            if(mPointViewMap.containsKey(nv)){
                PointView pv = mPointViewMap.get(nv);
                removeView(pv);
                mPointViewMap.remove(nv);
            }
        }
    }

    /**
     * 清除mCurClickNodeView的点击状态
     */
    private void resetClickNode(){
        if(mCurClickNodeView != null){
            mCurClickNodeView.setClick(false);
            mCurClickNodeView.invalidate();
        }
    }

    /**
     * 设置当前选中的NodeView
     * @param clickedView
     */
    private void setClickNode(NodeView clickedView){
        resetClickNode();
        mCurClickNodeView = clickedView;
        mCurClickNodeView.setClick(true);
        mCurClickNodeView.invalidate();
    }

    /**
     * psresenter添加完成后的回调
     *
     * @param newNode
     */
    @Override
    public void addNodeView(Node newNode) {

        NodeView nv = new NodeView(mContext);
        nv.setNodeValue(newNode);
        nv.addView(generateCustomView(newNode));


        addView(nv);
        mNodeViewList.add(nv);

        refreshView();

    }

    /**psresenter添加完成后的回调
     *
     *
     */
    @Override
    public void deleteNodeView(List<Node> deleteNodeList) {
        for(Node n : deleteNodeList){
            NodeView nv = getNodeView(n);
            removeView(nv);
            mNodeViewList.remove(nv);
            nv = null;
        }
        mCurClickNodeView = null;

        refreshView();
    }


    /**
     * 外部调用
     * 为当前处于点击状态的节点mCurClickNodeView 添加子节点
     *
     * @param newNode
     */
    public void addSubNode(Node newNode){
        if(mCurClickNodeView != null && inAutoLayoutMode)
            mPresenter.addSubNode(mCurClickNodeView.getNodeValue(),newNode);
    }

    //添加兄弟节点
    public void addBroNode(Node broNode,Node newNode){
        mPresenter.addBroNode(broNode,newNode);
    }

    /**
     * 外部调用
     * 删除节点
     */
    public void deleteNode(){
        if(mCurClickNodeView != null){
            if(mCurClickNodeView == mHeadView){
                showInfo("头结点不能删除");
                return;
            }
            mPresenter.deleteNode(mCurClickNodeView.getNodeValue());
        }
    }

    //更新节点信息（属性）
    public void updateNodeInfo(Node oldN,Node newN){
        mPresenter.updateNodeInfo(oldN, newN);
    }

    /**
     * 外部调用 保存tree到文件
     * @param newFileName 若参数为null，则默认保存到读取的文件；若不为null则创建新文件进行保存
     */
    public void saveFile(String newFileName){
        mPresenter.saveToFile(newFileName);
    }

    /**
     * 针对不同的Node生成对应的子view
     */
    public interface ChildViewCustomer {
        View generateChildView(Node childNode);
    }

    public ChildViewCustomer getChildViewCustomer() {
        return mChildViewCustomer;
    }

    public void setChildViewCustomer(ChildViewCustomer mChildViewCustomer) {
        this.mChildViewCustomer = mChildViewCustomer;
    }
}
