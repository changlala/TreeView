package com.chang.treeview.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Node implements Serializable {


    private static final long serialVersionUID = 2L;

    //若发横一些意外情况 有可能是id值有了重复
    private int id ;
    private String name;
    private List<Node> children = new ArrayList<>();
    private int curLevel;

    public Node() {
        generateId();
    }

    Random r = new Random();

    /**
     * 生成一个唯一id 目前只能大概率保证唯一
     *
     * curtimemillien后三位 + 100以内随机数
     */
    private void generateId(){
        long t = System.currentTimeMillis();
        String time = String.valueOf(t);
        String res = time.substring(time.length()-3);
        res +=String.valueOf(r.nextInt(100));

        id = Integer.parseInt(res);
    }
    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getChildren() {
        return children;
    }



    public int getCurLevel() {
        return curLevel;
    }

    public void setCurLevel(int curLevel) {
        this.curLevel = curLevel;
    }
}
