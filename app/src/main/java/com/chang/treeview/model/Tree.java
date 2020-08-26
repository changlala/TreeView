package com.chang.treeview.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Tree implements Serializable {

    private static final long serialVersionUID = 1L;

    private Node head;
    //树中节点总数（包括头结点）
    private int nodeCount;

    public Node getHead() {
        return head;
    }

    public void setHead(Node head) {
        this.head = head;
        calNodeCount();

    }

    //层次遍历统计节点数量
    private void calNodeCount(){
        List<Node> nodeList = new ArrayList();
        nodeList.add(head);
        
        for (int i = 0 ; i < nodeList.size() ;i++) {
            Node cur = nodeList.get(i);
            List<Node> children = cur.getChildren();
            if(children != null)
                nodeList.addAll(children);
            
        }
        
        nodeCount = nodeList.size();
    }


    public int getNodeCount() {
        return nodeCount;
    }


}
