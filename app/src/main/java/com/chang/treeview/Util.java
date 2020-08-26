package com.chang.treeview;

import android.content.Context;
import android.widget.Toast;

public class Util {

    /**
     * 显示Toast的信息
     *
     * @param mContext
     * @param toastInfo
     */
    public static void showToast(Context mContext, String toastInfo) {
        Toast mToast = Toast.makeText(mContext, toastInfo, Toast.LENGTH_SHORT);
        mToast.show();
    }
}
