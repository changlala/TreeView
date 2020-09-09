# TreeView

v1.0 2020年8月26日10:26:20

当前功能
- 画布的拖拽（考虑了边界、缩放适配的问题，解决了父view拖拽和子view触摸事件的兼容问题）
- 手势缩放（考虑了同一事件流中缩小到最小又放到最大的特殊情况）
- 向右生长的自动布局（核心，后续遍历） LayoutManager
- 添加、删除节点（对外提供的接口）
- 当前点击的子view，全局变量；节点view的点击框绘制
- 保存到文件，对象输出流 ; 从文件中读取
- 整体是树形结构，无环
- 代码结构 MVP （主要体现在presenter和view的互相调用上,①presenter处理完将更新后的Tree交给view来构建对应控件呈现，若有错误信息也一并交给view toast出来；②数据的处理和呈现完全分离，presenter只需要将更新后的tree交给view，view通过tree生成对应的NodeView来进行呈现即可，如果处理逻辑需要修改完全不需要改动view部分）
- 提供了外部设置子view布局的接口 ChildViewCustomer

v2.0 2020年9月9日10点05分

新增特性：
- 新增互动模式，点击按钮可以在自动layout模式和互动模式下来回切换。在互动模式下可以对节点进行自由拖动，拖拽节点尾部圆点可以创建子节点。
- 新增PointView作为互动模式下拖拽创建子节点的按钮。

### 数据结构
数据结构比较简单，由于是树形包括节点类Node，以及存储头节点的Tree。

### 控件说明
项目的控件结构可分为四层，下面从底层向上说明：
- NodeView，继承自ViewGroup，包含一个Node类型的变量。作为树形图中的节点。通过对其添加子布局来完成节点样式的定制。
- TreeView，继承自ViewGroup，包含一个Tree类型变量。作为所有NodeView和PointView的父布局对他们进行管理，子view所有的触摸事件（拖拽缩放点击），对节点的自动布局，互动模式的处理，基础的增删事件都会在TreeView中统一处理。
- CanvasLayout，继承自FrameLayout，作为一张逻辑上的画布承载着TreeView，默认布局在左上角。
- TreeViewWrapper，继承自ViewGroup，大小为显示屏尺寸，作为CanvasLayout的直接父控件管理着画布的拖拽缩放并控制着触摸事件的下发。

### 外部调用接口说明

#### 设置TreeView数据源
```java
   //读取保存的树文件 文件默认保存在getFilesDir/
   mTreeView.setConfigFile("temp1.ch");

   //读取Tree对象直接生成
   mTreeView.setTree(mTree);

```

#### 子节点的增删
```java
    mTreeView.addSubNode(node);

    mTreeView.deleteNode();
```

#### 设置节点内容布局
```java
       TreeView.ChildViewCustomer mChildViewCustomer = new TreeView.ChildViewCustomer() {
            @Override
            public View generateChildView(Node childNode) {

                //
                return null;
            }
        };

        mTreeView.setChildViewCustomer(mChildViewCustomer);
```

#### 设置LayoutManager

```java
        //向右生长的布局方式
        RightTreeLayoutManager treeLayoutManager = new RightTreeLayoutManager(mTreeView);
        mTreeView.setmLayoutManager(treeLayoutManager);
```

#### 模式的切换 自动layout模式———互动模式
```java
        //true为自动layout false为互动模式
        mTreeView.setInAutoLayoutMode();

```

#### 保存到文件
```java
        mTreeView.saveFile("temp1.ch");
```