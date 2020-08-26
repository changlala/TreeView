package com.chang.treeview.presenter;

import com.chang.treeview.model.Node;

public interface NodeViewContract {

    interface Presenter extends BasePresenter<View>{

        //添加兄弟节点
        Boolean addNode(Node broNode,Node newNode);

        //添加子节点
        Boolean addSubNode(Node parentNode,Node newNode);

        //删除节点
        void deleteNode(Node curNode);
    }

    interface View extends BaseView{

    }
}
