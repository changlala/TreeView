package com.chang.treeview.presenter;

import com.chang.treeview.model.Node;
import com.chang.treeview.model.Tree;

import java.util.List;

public interface TreeViewContract {

    interface Presenter extends BasePresenter<View>{

        // 生成一个只包含一个头结点的新树形图
        Tree loadDefaultTree();

        //读取配置文件生成树形图
        Tree loadTreeFromFile(String filePath);

        //从Tree对象直接时生成一个树形图
        void loadTreeFromCode(Tree tree);

        //添加节点
        void addSubNode(Node parentNode,Node newNode);

        //添加兄弟节点
        void addBroNode(Node broNode,Node newNode);

        //删除节点
        void deleteNode(Node curNode);

        //更新节点信息（属性）
        void updateNodeInfo(Node oldN,Node newN);

        //保存操作后的tree对象，若参数为null，则默认保存到读取的文件；若不为null则创建新文件进行保存
        void saveToFile(String newFileName);



    }

    interface View extends BaseView{



        //Toast所需信息
        void showInfo(String erro);

        //节点添加后的回调
        void addNodeView(Node newNode);

        //节点删除后的回调
        void deleteNodeView(List<Node> deleteNodeList);

        //刷新控件
        void refreshView();

    }
}
