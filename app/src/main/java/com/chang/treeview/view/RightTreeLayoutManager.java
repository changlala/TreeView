package com.chang.treeview.view;

import android.graphics.Rect;
import android.util.Log;
import android.util.Pair;

import com.chang.treeview.model.Node;
import com.chang.treeview.model.Tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * layout适配器 子树生长方向向右
 */
public class RightTreeLayoutManager implements TreeViewLayoutManager {

    private static final String TAG = "RightTreeLayoutManager";

    //子树间在竖直上的间隔
//    private int dyTree = 20;
    //节点间在竖直上的间隔
    private int dyNode = 50;
    //节点间在水平上的间隔
    private int dx = 90;

    private TreeView mTreeView;

    //后续遍历顺序存储，Integer保存该节点子树尺寸
    private List<Pair<Node , Rect>> childList = new ArrayList<>();


    //存储当前layout时子view右上角的坐标,子viewlayout从右上角开始
    private int curX ;
    private int curY ;

    public RightTreeLayoutManager(TreeView mTreeView) {

        this.mTreeView = mTreeView;

    }

    /**
     * 后序遍历 layout子view
     * @param n
     */
    private void postOrderLayout(Node n){
        List<Node> children = n.getChildren();

        if(children.size() != 0){
            //除叶子结点以外的其他节点

            Rect subTreeRect = new Rect();

            //起始位置 r t永远是右上角
            int r = curX;
            int t = curY;

            int subTreeHeight = 0;
            int subTreeWidth = 0;

            NodeView maxWidthNodeView = null;

            //对所有子节点 记录总高度以及最宽的Width
            for (Node child : children) {
                postOrderLayout(child);
                NodeView childView = mTreeView.getNodeView(child);

                subTreeHeight+=childView.getSubTreeRect().height();
                int maxWidth = Math.max(subTreeWidth,childView.getSubTreeRect().width());
                if(maxWidth > subTreeWidth){
                    subTreeWidth = maxWidth;
                    maxWidthNodeView = childView;
                }
            }
            Log.d(TAG, "postOrderLayout: 头结点为"+n.getName()+" 的subtree 的宽高为"+subTreeWidth+" "+subTreeHeight);
            NodeView parentView = mTreeView.getNodeView(n);

            //计算parentView位置
            int parentR = r - subTreeWidth- dx;;
            int parentT;

            if(subTreeHeight >= parentView.getMeasuredHeight()){
                //子树高度大于父节点高度 父节点高度定到子树高度一半
                parentT = t + subTreeHeight/2 - parentView.getMeasuredHeight()/2;
            }else{
                //父节点高度大于子树 父节点高度与子树高度齐平
                //需修正curY
                curY += parentView.getMeasuredHeight() - subTreeHeight + dyNode;
                //更改子树高度 为父节点高度
                subTreeHeight = parentView.getMeasuredHeight()+dyNode;
                parentT = t;

            }

            parentView.layout(parentR - parentView.getMeasuredWidth(), parentT,
                    parentR,parentT+parentView.getMeasuredHeight());

            //构造子树rect （包括头结点）
            subTreeRect.left = r-subTreeWidth - dx - parentView.getMeasuredWidth();
            subTreeRect.right = r;
            subTreeRect.top = t;
            subTreeRect.bottom = t+subTreeHeight;

            parentView.setSubTreeRect(subTreeRect);


            //correct subtree  仅修正x 将parentView的子节点们左对齐
            for(Node child:children){
                if(child.getId() != maxWidthNodeView.getNodeValue().getId()){
                    NodeView nView = mTreeView.getNodeView(child);
                    int dx = maxWidthNodeView.getSubTreeRect().width() - nView.getSubTreeRect().width();
                    moveSubTree(nView,-dx,0);
                }
            }

        }else{
            //所有的叶节点

            NodeView nv = mTreeView.getNodeView(n);
            int h = nv.getMeasuredHeight();
            int w = nv.getMeasuredWidth();

            int l = curX - w;
            int t = curY;

            nv.layout(l,t,l+w,t+h);
            //更新下一个兄弟view起始y
            curY += h + dyNode;

            Rect nodeRect = new Rect(l,t,l+w,t+h+dyNode);
            nv.setSubTreeRect(nodeRect);
        }
    }



    @Override
    public Rect onLayout(Tree tree,int l , int t) {
        Node head = mTreeView.getTree().getHead();

        //layout起点 画布的右上角
        curX = mTreeView.getmWidth() ;
        curY = 0 ;

        //后序layout完后整体在右上角
        postOrderLayout(head);

        //整体移到l,t的位置
        NodeView headView = mTreeView.getNodeView(head);
        moveSubTree(headView,headView.getSubTreeRect().left * -1 + l,t);

        return headView.getSubTreeRect();
    }


//    private void setGap( ){
//        Tree tree = mTreeView.getTree();
//        Node head = tree.getHead();
//        NodeView headView = mTreeView.getNodeView(head);
//
//        Rect headRect = headView.getSubTreeRect();
//
//    }

    /**
     * 将headView对应子树移动 dx dy
     *
     * @param headView
     * @param dx
     * @param dy
     */
    private void moveSubTree(NodeView headView,int dx ,int dy){

        List<NodeView> nodeViewList = new ArrayList<>();
        nodeViewList.add(headView);
        for (int i = 0 ; i <nodeViewList.size() ; i++) {
            NodeView curView = nodeViewList.get(i);

            curView.layout(curView.getLeft()+dx , curView.getTop()+dy,
                                    curView.getRight()+dx,curView.getBottom()+dy);

            Rect rect = curView.getSubTreeRect();
            rect.left += dx;
            rect.right += dx;
            rect.top += dy;
            rect.bottom += dy;

            List<Node> children = curView.getNodeValue().getChildren();
            for (Node child : children) {
                nodeViewList.add(mTreeView.getNodeView(child));
            }
        }
    }


}
