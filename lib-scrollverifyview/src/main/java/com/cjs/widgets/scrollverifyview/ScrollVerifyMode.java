package com.cjs.widgets.scrollverifyview;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 描述:ScrollVerifyView的滑动模式限定
 * <p>
 * 作者:陈俊森
 * 创建时间:2018年05月12日 14:36
 * 邮箱:chenjunsen@outlook.com
 *
 * @version 1.0
 */
@IntDef(value = {
        ScrollVerifyView.MODE_SCROLL_FREE,
        ScrollVerifyView.MODE_SCROLL_ONLY_HORIZONTAL,
        ScrollVerifyView.MODE_SCROLL_ONLY_VERTICAL,
        ScrollVerifyView.MODE_SCROLL_FREE_HORIZONTAL,
        ScrollVerifyView.MODE_SCROLL_FREE_VERTICAL}
)
@Retention(RetentionPolicy.SOURCE)
public @interface ScrollVerifyMode {
}
