package com.cjs.widgets.scrollverifyview;

import android.util.Log;

/**
 * 描述:简易日志工具类
 * <p>
 * 作者:陈俊森
 * 创建时间:2018年05月11日 11:00
 * 邮箱:chenjunsen@outlook.com
 *
 * @version 1.0
 */
public class L {
    private boolean isOpenLog = true;

    public L(boolean isOpenLog) {
        this.isOpenLog = isOpenLog;
    }

    public void d(String tag, String msg) {
        if (isOpenLog) {
            Log.d(tag, msg);
        }
    }
    public void e(String tag, String msg) {
        if (isOpenLog) {
            Log.e(tag, msg);
        }
    }
}
