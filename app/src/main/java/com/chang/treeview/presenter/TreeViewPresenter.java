package com.chang.treeview.presenter;

import android.text.TextUtils;

import com.chang.treeview.model.ConfigFile;
import com.chang.treeview.model.Node;
import com.chang.treeview.model.Tree;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class TreeViewPresenter implements TreeViewContract.Presenter {

    private TreeViewContract.View mView;
    private Tree mTree;

    private Node mHead;

    private String mCurOpenedFileName ;

//    public static int LAYOUT_DEFAULT = 0;

    @Override
    public void setView(TreeViewContract.View view) {
        mView = view;
    }

    public TreeViewPresenter(TreeViewContract.View mView) {
        this.mView = mView;
    }

    //test 用 立马删
    @Override
    public void loadTreeFromCode(Tree tree){
        mTree = tree;
        mHead = mTree.getHead();
    }
    @Override
    public Tree loadDefaultTree() {

        Tree tree = new Tree();
        Node head = new Node();
        head.setCurLevel(0);
        head.setName("head");
        tree.setHead(head);

        mTree = tree;
        mHead = mTree.getHead();
        return tree;
    }

    @Override
    public Tree loadTreeFromFile(String fileName) {
        if(fileName == null ||TextUtils.isEmpty(fileName)){
            mView.showInfo("文件名有误");
            return null;
        }
        mCurOpenedFileName = fileName;
        Tree tree = null;
        try{
            tree = ConfigFile.getTreeFrom(fileName);
            mTree = tree;
            mHead = mTree.getHead();
        }catch (Exception e){
            if(e instanceof FileNotFoundException){
                mView.showInfo("未找到文件");
            }
            return null;
        }

        return tree;

    }

    /**
     *
     * 将修改后的tree保存到文件
     *
     * @param newFileName 新创建一个newFileName的文件来保存；若为null则保存在当前读取的file
     */
    @Override
    public void saveToFile(String newFileName) {
        if(newFileName == null){
            //写入到当前打开文件
            if(mCurOpenedFileName == null){
                mView.showInfo("文件保存出错");
                return;
            }

            if(ConfigFile.makeConfigFile(mTree,mCurOpenedFileName)){
                mView.showInfo("已保存到"+ConfigFile.FILE_PATH+mCurOpenedFileName);
            }else{
                mView.showInfo("保存文件出错");
            }

        }else{
            if(TextUtils.isEmpty(newFileName)){
                mView.showInfo("文件名有错");
            }else{
                //创建新文件并写入
                if(ConfigFile.makeConfigFile(mTree,newFileName)){
                    //更新mCurOpenedFileName
                    mCurOpenedFileName = newFileName;
                    mView.showInfo("已保存到"+ConfigFile.FILE_PATH+newFileName);
                }else{
                    mView.showInfo("保存文件出错");
                }
            }
        }
    }

    // 层次化遍历
    public void levelTraversalTree(Node head , NodeTraversalListener traversalListener){
        List<Node> nodeList = new ArrayList<>();
        nodeList.add(head);
        for (int i = 0 ; i <nodeList.size() ; i++) {
            Node curNode = nodeList.get(i);

            //dosomething
            if(traversalListener.onNodeTraversal(curNode))
                break;

            List<Node> children = curNode.getChildren();
            nodeList.addAll(children);
        }
    }

    @Override
    public void addSubNode(Node parentNode, Node newNode) {
        if(mTree != null){
            parentNode.getChildren().add(newNode);
            mView.addNodeView(newNode);
        }else{
            mView.showInfo("mTree or mView is null");
        }
    }

    @Override
    public void addBroNode(Node broNode, final Node newNode) {
        if(mTree != null){
            levelTraversalTree(mHead, new NodeTraversalListener() {
                @Override
                public boolean onNodeTraversal(Node curNode) {
                    //判断子节点数组中是否存在broNode
                    List<Node> childList = curNode.getChildren();
                    for (Node child : childList) {
                        if(child.getId() == curNode.getId()){
                            //找到父节点
                            Node parentNode = curNode;
                            parentNode.getChildren().add(newNode);
                            mView.addNodeView(newNode);
                            return true;
                        }
                    }
                    return false;
                }
            });
        }else{
            mView.showInfo("mTree or mView is null");
        }
    }

    @Override
    public void deleteNode(final Node deletNode) {

        final Boolean foundNode = false;

        //断开父节点引用
        levelTraversalTree(mHead, new NodeTraversalListener() {
            @Override
            public boolean onNodeTraversal(Node curNode) {
                //判断子节点数组中是否存在deletNode
                List<Node> childList = curNode.getChildren();
                for (Node child : childList) {
                    if(child.getId() == deletNode.getId()){
                        //父节点
                        Node parentNode = curNode;
                        int index = parentNode.getChildren().indexOf(deletNode);
                        if(index == -1){
                            mView.showInfo("delete error");
                        }else{
                            final List<Node> deleteNodeList = new ArrayList<>();
                            //删除子树中的所有节点
                            levelTraversalTree(deletNode, new NodeTraversalListener() {
                                @Override
                                public boolean onNodeTraversal(Node curNode) {
                                    deleteNodeList.add(curNode);
                                    curNode = null;
                                    return false;
                                }
                            });
                            //断开索引
                            parentNode.getChildren().remove(index);
                            //通知view更新ui
                            mView.deleteNodeView(deleteNodeList);

                        }
                        return true;
                    }
                }
                return false;
            }
        });



    }

    @Override
    public void updateNodeInfo(Node oldN, Node newN) {

    }

    interface NodeTraversalListener{
        /**
         *
         * @param curNode
         * @return true 立即跳出循环
         */
        boolean onNodeTraversal(Node curNode);
    }
}
