package com.chang.treeview.presenter;

import com.chang.treeview.model.Node;

public class NodeViewPresenter implements NodeViewContract.Presenter {

    private NodeViewContract.View mView;
    private Node mNode;

    @Override
    public Boolean addNode(Node broNode, Node newNode) {
        return null;
    }

    @Override
    public Boolean addSubNode(Node parentNode, Node newNode) {
        return null;
    }

    @Override
    public void deleteNode(Node curNode) {

    }

    @Override
    public void setView(NodeViewContract.View view) {
        mView = view;
    }
}
