package com.cjs.widgets.scrollverifyview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.util.TypedValue;

/**
 * <p>
 * <h1>描述:图形工具类</h1>
 * 关于安卓矩阵的更多信息，可以查看这篇文章<a href="https://blog.csdn.net/cquwentao/article/details/51445269">android matrix 最全方法详解与进阶（完整篇）</a>
 * 作者:陈俊森
 * 创建时间:2018年05月10日 11:22
 * 邮箱:chenjunsen@outlook.com
 * </p>
 *
 * @version 1.0
 */
public class GraphicTools {
    /**
     * 获取基于原图的缩放图片位图，使用{@link Matrix#setScale(float, float)}(清除之前矩阵变换再执行缩放)该方法进行缩放
     *
     * @param bmp    未缩放前的原图
     * @param scaleX 水平缩放比例
     * @param scaleY 垂直缩放比例
     * @return 一个基于原图根据缩放比例得到的新图
     */
    public static Bitmap scaleSetBitmap(Bitmap bmp, float scaleX, float scaleY) {
        validBmp(bmp);
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    public static Bitmap scalePostBitmap(Bitmap bmp, float scaleX, float scaleY) {
        validBmp(bmp);
        Matrix matrix = new Matrix();
        matrix.postScale(scaleX, scaleY);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    public static Bitmap rotatePostBitmap(Bitmap bmp, float degree) {
        validBmp(bmp);
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        Matrix matrix = new Matrix();
//        matrix.postRotate(degree);
        matrix.postRotate(degree, bmpWidth / 2, bmpHeight / 2);
        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeight, matrix, true);
    }

    private static void validBmp(Bitmap bmp) {
        if (bmp == null) {
            throw new IllegalArgumentException("传入矩阵变换的位图不能为空");
        }
        /*else if (!bmp.isMutable()) {
            throw new IllegalArgumentException("传入矩阵变换的位图必须是可变的");
        }*/
    }

    /**
     * Drawable转Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (null == drawable) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Drawable getDrawable(Context context, @DrawableRes int drawableId) {
        Drawable d = null;
        if (Build.VERSION.SDK_INT >= 21) {
            d = context.getDrawable(drawableId);
        } else {
            d = context.getResources().getDrawable(drawableId);
        }
        return d;
    }

    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

}
