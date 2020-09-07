package com.chang.treeview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chang.treeview.model.Node;
import com.chang.treeview.model.Tree;
import com.chang.treeview.view.CanvasLayout;
import com.chang.treeview.view.RightTreeLayoutManager;
import com.chang.treeview.view.TreeView;

import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TreeView mTreeView;
    private Tree mTree;

    private CanvasLayout mCanvasLayout;
    private Button addsubBtn;
    private Button deleteBtn;
    private Button saveBtn;
    private Button changeAutoLayoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        savedInstanceState.get


        mTreeView = findViewById(R.id.treeview);
        //初始化TreeView
        generateTree();
        File df = new File(getFilesDir()+"/"+"temp1.ch");
        if(df.exists())
            //读取保存的树文件
            mTreeView.setConfigFile("temp1.ch");
        else
            mTreeView.setTree(mTree);


        //设置layout方式
        RightTreeLayoutManager treeLayoutManager = new RightTreeLayoutManager(mTreeView);
        mTreeView.setmLayoutManager(treeLayoutManager);
        //设置子view的样式
        TreeView.ChildViewCustomer mChildViewCustomer = new TreeView.ChildViewCustomer() {
            @Override
            public View generateChildView(Node childNode) {

                return null;
            }
        };
//        mTreeView.setGenerateChildViewListener(mChildViewCustomer);

        addsubBtn = findViewById(R.id.addSubNode);
        deleteBtn = findViewById(R.id.deleteNode);
        saveBtn = findViewById(R.id.saveNode);
        changeAutoLayoutBtn = findViewById(R.id.changeLayoutMode);

        //点击添加子节点
        addsubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Node node = new Node();
                node.setName("add subNode");
                mTreeView.addSubNode(node);
            }
        });

        //点击删除节点及其子节点
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTreeView.deleteNode();
            }
        });

        //点击保存树形图到文件
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTreeView.saveFile("temp1.ch");
            }
        });

        changeAutoLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean flag = mTreeView.isInAutoLayoutMode();
                mTreeView.setInAutoLayoutMode(!flag);
                mTreeView.requestLayout();
            }
        });

    }

    private void generateTree(){
        Tree tree = new Tree();
        Node n5 = new Node();
        Node n1 = new Node();
        Node n2 = new Node();
        Node n3 = new Node();
        Node n4 = new Node();
        Node n6= new Node();
        Node n7 = new Node();
        Node n8 = new Node();
        Node n9 = new Node();
        Node n10 = new Node();



        n1.setName("n1n1n1n1n1n1n1n1n1n1n1n1n1n1");
        n2.setName("n2n2n2n2n2n2n2n2n2n2n2");
        n3.setName("n3n3n3n3n3n3n3");
        n4.setName("n4n4n4n4");
        n5.setName("n5");
        n6.setName("n666");
        n7.setName("n7777777");
        n8.setName("n888888888888888888888");
        n9.setName("n9999999999");
        n10.setName("n101010");
//
        n7.getChildren().add(n9);
        n8.getChildren().add(n10);
        n5.getChildren().add(n7);
        n5.getChildren().add(n8);
        n2.getChildren().add(n5);
        n4.getChildren().add(n6);
        n1.getChildren().add(n2);
        n1.getChildren().add(n3);
        n1.getChildren().add(n4);

        tree.setHead(n1);
        mTree = tree;
    }

    private Random random = new Random();

//    private Node getRandomSubTree(){
//        int childCount = random.nextInt(10);
//        Node head = new Node();
//
//        for(int i = 0 ; i<childCount ; i++){
//            Node child = new Node();
//            child.setName();
//        }
//    }
}