package com.chang.treeview.view;

import android.graphics.Rect;

import com.chang.treeview.model.Tree;

public interface TreeViewLayoutManager {

    Rect onLayout(Tree tree,int l , int t);


}
