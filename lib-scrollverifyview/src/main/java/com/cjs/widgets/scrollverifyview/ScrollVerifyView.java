package com.cjs.widgets.scrollverifyview;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 描述:图形滑块验证控件
 * <p>
 * 作者:陈俊森
 * 创建时间:2018年05月10日 11:21
 * 邮箱:chenjunsen@outlook.com
 *
 * @version 1.0
 */
@SuppressLint("AppCompatCustomView")
public class ScrollVerifyView extends ImageView {
    /**
     * 水平滑动模式，起始滑块在目标滑块左侧，并且从起始滑块到目标滑块的滑动轨迹只能是直线形式
     */
    public static final int MODE_SCROLL_ONLY_HORIZONTAL = 1;
    /**
     * 垂直滑动模式，起始滑块在目标滑块上面，并且从起始滑块到目标滑块的滑动轨迹只能是直线形式
     */
    public static final int MODE_SCROLL_ONLY_VERTICAL = 2;
    /**
     * 自由滑动模式，起始滑块与目标滑块的相对方向根据设置的起始滑块的基本位置随机产生，并且从起始滑块到目标滑块的滑动轨迹可以自由滑动
     */
    public static final int MODE_SCROLL_FREE = 3;
    /**
     * 水平滑动模式，起始滑块在目标滑块左侧，并且从起始滑块到目标滑块的滑动轨迹可以自由滑动
     */
    public static final int MODE_SCROLL_FREE_HORIZONTAL = 4;
    /**
     * 垂直滑动模式，起始滑块在目标滑块上面，并且从起始滑块到目标滑块的滑动轨迹可以自由滑动
     */
    public static final int MODE_SCROLL_FREE_VERTICAL = 5;
    /**
     * 日志打印管理器
     */
    private L l;
    /**
     * 是否开启日志
     */
    private boolean isOpenLog = true;
    /**
     * 当前控件日志标签
     */
    private static final String TAG = "ScrollVerifyView";
    /**
     * 该值不是真正的最大可滑动距离，而是指代采用默认的最大值。默认值在构造时期是获取不到的，需要在视图渲染完毕后才能获取到。
     */
    public static final int DEFAULT_MAX_HORIZONTAL_SCROLL_DISTANCE = -9999;
    /**
     * 整个控件绘制的画笔
     */
    private Paint mViewPaint;
    /**
     * 是否开启随机旋转
     */
    private boolean isOpenRandomRotate;
    /**
     * 外部初始设置的旋转角度
     */
    private float mDefaultRotateDegree;
    /**
     * 旋转角度
     */
    private float mRotateDegree;
    /**
     * 是否随机位置生成滑块
     */
    private boolean isOpenRandomBlockLocation;
    /**
     * 目标滑块初始横坐标(左上角的那个像素点)
     */
    private float mTargetBlockX;
    /**
     * 目标滑块初始纵坐标(左上角的那个像素点)
     */
    private float mTargetBlockY;
    /**
     * 起始可移动滑块的坐上角顶点横坐标
     */
    private float mStartBlockX;
    /**
     * 起始可移动滑块的坐上角顶点纵坐标
     */
    private float mStartBlockY;
    /**
     * 当{@link #mBlockWidth}的值为0的时候，取控件宽度除以该值得到滑块宽度
     */
    private int mBlockWidthDivide;
    /**
     * 当{@link #mBlockHeight}的值为0的时候，取控件的高度除以该值得到滑块高度
     */
    private int mBlockHeightDivide;
    /**
     * 滑块宽度
     */
    private int mBlockWidth;
    /**
     * 滑块高度
     */
    private int mBlockHeight;
    /**
     * 控件的背景图bitmap
     */
    private Bitmap mBackBitmap;
    /**
     * 为了美化效果，给滑块设置的在滑动控件内部的内边距
     */
    private int mViewPadding;
    /**
     * 滑块移动到目标位置的水平距离的最大可能值(即两个滑块中心点的x坐标的差值的最大可能值，当垂直模式时是两个中心点y坐标的差值)
     */
    private int mMaxHorizontalScrollDistance;
    /**
     * 滑块移动到目标位置的真实距离
     */
    private int mRealScrollDistance;
    /**
     * 判断是否需要重置绘图
     */
    private boolean isReset = true;
    /**
     * 滑块是否可以触摸
     */
    private boolean isBlockTouchable;
    /**
     * 移动到目标滑块的容差范围(像素)
     */
    private int mValidOffset;
    /**
     * 是否开启避免随机生成的滑块之间距离过近的功能
     */
    private boolean isAvoidGenerateTooClose;
    /**
     * 滑动模式
     */
    @ScrollVerifyMode
    private int mScrollMode;
    /**
     * 滑动验证监听器
     */
    private ScrollVerifyListener mScrollVerifyListener;
    /**
     * 可移动滑块的形状图片
     */
    private Drawable mMovableBlockDrawable;
    /**
     * 目标不可移动滑块的形状图片
     */
    private Drawable mTargetBlockDrawable;
    /**
     * 从可移动滑块形状图片中获取的位图资源
     */
    private Bitmap mMovableSrcBitmap;
    /**
     * 控件首次创建的时候的起始滑块的横坐标，重置时候用
     */
    private float mOriStartBlockX;
    /**
     * 控件首次创建的时候的起始滑块的纵坐标，重置时候用
     */
    private float mOriStartBlockY;

    public ScrollVerifyView(Context context) {
        super(context);
        initView(context, null, 0, 0);
    }

    public ScrollVerifyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs, 0, 0);
    }

    public ScrollVerifyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public ScrollVerifyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * 设置滑动验证监听器
     *
     * @param scrollVerifyListener
     */
    public void setScrollVerifyListener(ScrollVerifyListener scrollVerifyListener) {
        mScrollVerifyListener = scrollVerifyListener;
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        l = new L(isOpenLog);
        if (attrs != null) {
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ScrollVerifyView);
            isOpenRandomRotate = array.getBoolean(R.styleable.ScrollVerifyView_openRandomRotate, false);
            isOpenRandomBlockLocation = array.getBoolean(R.styleable.ScrollVerifyView_openRandomBlockLocation, true);
            mBlockHeight = array.getDimensionPixelOffset(R.styleable.ScrollVerifyView_blockHeight, 0);
            mBlockWidth = array.getDimensionPixelOffset(R.styleable.ScrollVerifyView_blockWidth, 0);
            mBlockHeightDivide = array.getInt(R.styleable.ScrollVerifyView_blockHeightDivide, 4);
            mBlockWidthDivide = array.getInt(R.styleable.ScrollVerifyView_blockWidthDivide, 6);
            mViewPadding = array.getDimensionPixelOffset(R.styleable.ScrollVerifyView_viewPadding, GraphicTools.dp2px(getContext(), 5));
            mMovableBlockDrawable = array.getDrawable(R.styleable.ScrollVerifyView_movableBlockDrawable);
            mTargetBlockDrawable = array.getDrawable(R.styleable.ScrollVerifyView_targetBlockDrawable);
            mMaxHorizontalScrollDistance = array.getDimensionPixelOffset(R.styleable.ScrollVerifyView_maxScrollDistance, DEFAULT_MAX_HORIZONTAL_SCROLL_DISTANCE);
            isBlockTouchable = array.getBoolean(R.styleable.ScrollVerifyView_blockTouchable, true);
            mValidOffset = array.getDimensionPixelOffset(R.styleable.ScrollVerifyView_validOffset, GraphicTools.dp2px(getContext(), 1));
            mDefaultRotateDegree = array.getFloat(R.styleable.ScrollVerifyView_rotateDegree, 0f);
            isAvoidGenerateTooClose = array.getBoolean(R.styleable.ScrollVerifyView_avoidGenerateTooClose, true);
            mScrollMode = array.getInt(R.styleable.ScrollVerifyView_scrollMode, MODE_SCROLL_FREE);
            array.recycle();
        }
        mMovableBlockDrawable = mMovableBlockDrawable == null ? GraphicTools.getDrawable(getContext(), R.drawable.svv_default_puzzle_shade) : mMovableBlockDrawable;
        mTargetBlockDrawable = mTargetBlockDrawable == null ? GraphicTools.getDrawable(getContext(), R.drawable.svv_default_puzzle_show) : mTargetBlockDrawable;
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);
        mViewPaint.setDither(true);
        mRotateDegree = isOpenRandomRotate ? (float) (Math.random() * 3 * 90) : mDefaultRotateDegree;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        l.d(TAG, "--------------------->onMeasure<---------------------");
        l.d(TAG, "width:" + getWidth());
        l.d(TAG, "height:" + getHeight());
        l.d(TAG, "measuredWidth:" + getMeasuredWidth());
        l.d(TAG, "measuredHeight:" + getMeasuredHeight());
        mBackBitmap = getBaseBitmap();
        if (mBackBitmap == null) {
            return;
        }
        int baseBmpWidth = mBackBitmap.getWidth();
        int baseBmpHeight = mBackBitmap.getHeight();
        l.d(TAG, "baseBmpWidth:" + baseBmpWidth);
        l.d(TAG, "baseBmpHeight:" + baseBmpHeight);
        int blockDrawableWidth = mMovableBlockDrawable.getIntrinsicWidth();
        int blockDrawableHeight = mMovableBlockDrawable.getIntrinsicHeight();
        if (blockDrawableHeight > 0 && blockDrawableWidth > 0 && mBlockWidth==0 && mBlockHeight==0) {
            mBlockWidth=blockDrawableWidth;
            mBlockHeight=blockDrawableHeight;
        }else{
            mBlockWidth = (mBlockWidth == 0 ? mBackBitmap.getWidth() / mBlockWidthDivide : mBlockWidth);
            mBlockHeight = (mBlockHeight == 0 ? mBackBitmap.getHeight() / mBlockHeightDivide : mBlockHeight);
        }

        //通过日志可以看出，首次，getWidth和getHeight是获取不到值的，所以取getMeasuredWidth和getMeasuredHeight
        l.d(TAG, "maxHorizontalScrollDistance before:" + mMaxHorizontalScrollDistance);
        //可用最大滑动值的计算方式是获得两个滑块的中心点的x坐标的差值的最大值，就是最大宽度减去左右padding再减去两个滑块的半宽
        int availableDistance = getMeasuredWidth() - mViewPadding * 2 - mBlockWidth;
        mMaxHorizontalScrollDistance = mMaxHorizontalScrollDistance == DEFAULT_MAX_HORIZONTAL_SCROLL_DISTANCE ? availableDistance : mMaxHorizontalScrollDistance;
        l.d(TAG, "maxHorizontalScrollDistance after:" + mMaxHorizontalScrollDistance);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        l.d(TAG, "--------------------->onDraw<---------------------");
        if (isReset) {
            mBackBitmap = getBaseBitmap();
            if (mBackBitmap == null) {
                return;
            }
            initSlideBlockLocation();
            mMovableSrcBitmap = Bitmap.createBitmap(mBackBitmap, (int) mTargetBlockX, (int) mTargetBlockY, mBlockWidth, mBlockHeight);
            isReset = false;
        }
        //这两步有顺序要求，先画target再画start,不然target会盖在start上面
        canvas.drawBitmap(getTargetBitmap(), mTargetBlockX, mTargetBlockY, mViewPaint);
        canvas.drawBitmap(getMovableBitmap(mMovableSrcBitmap), mStartBlockX, mStartBlockY, mViewPaint);
    }

    private void initSlideBlockLocation() {
        int totalWidth = mBackBitmap.getWidth();
        int totalHeight = mBackBitmap.getHeight();
        int validScrollDistance = totalWidth - mBlockWidth - mViewPadding * 2;
        mRealScrollDistance = Math.min(mMaxHorizontalScrollDistance, validScrollDistance);
        if (totalWidth < (mBlockWidth * 2 + mViewPadding * 2) || totalHeight < (mBlockHeight + mViewPadding * 2)) {
            l.e(TAG, "滑块尺寸超过控件大小");
            Toast.makeText(getContext(), "滑块尺寸超过控件大小", Toast.LENGTH_SHORT).show();
            return;
        } else {
            switch (mScrollMode) {
                case MODE_SCROLL_FREE:
                    applyFreeMode(totalWidth, totalHeight);
                    break;
                case MODE_SCROLL_ONLY_HORIZONTAL:
                    applyHorizontalMode(totalWidth, totalHeight);
                    break;
                case MODE_SCROLL_ONLY_VERTICAL:
                    applyVerticalMode(totalWidth, totalHeight);
                    break;
                case MODE_SCROLL_FREE_HORIZONTAL:
                    applyHorizontalMode(totalWidth, totalHeight);
                    break;
                case MODE_SCROLL_FREE_VERTICAL:
                    applyVerticalMode(totalWidth, totalHeight);
                    break;
            }
            l.d(TAG, "totalWidth:" + totalWidth + "  totalHeight:" + totalHeight);
            l.d(TAG, "blockWidth:" + mBlockWidth + "  blockHeight:" + mBlockHeight);
            l.d(TAG, "startX:" + mStartBlockX + "  startY:" + mStartBlockY);
            l.d(TAG, "targetX:" + mTargetBlockX + "  targetY:" + mTargetBlockY);
            mOriStartBlockX = mStartBlockX;
            mOriStartBlockY = mStartBlockY;

            float dx = mStartBlockX - mTargetBlockX;
            float dy = mStartBlockY - mTargetBlockY;
            mRealScrollDistance = (int) Math.sqrt(dx * dx + dy * dy);
        }
    }

    /**
     * 设置水平滑动模式
     *
     * @param totalHeight
     * @param totalWidth
     */
    private void applyHorizontalMode(int totalWidth, int totalHeight) {
        l.e(TAG, "水平滑动模式启用");
        int validStartXRangeStart = mViewPadding;
        int validStartXRangeEnd = totalWidth - mBlockWidth * 2 - mViewPadding;
        int validStartYRangeStart = mViewPadding;
        int validStartYRangeEnd = totalHeight - mBlockHeight - mViewPadding;
        if (isOpenRandomBlockLocation) {
            l.e(TAG, "已开启随机滑块生成功能");
            mStartBlockX = (float) (Math.random() * validStartXRangeEnd);
            mStartBlockY = (float) (Math.random() * validStartYRangeEnd);
        } else {
            mStartBlockX = Math.min(mStartBlockX, validStartXRangeEnd);
            mStartBlockY = Math.min(mStartBlockY, validStartYRangeEnd);
        }
        if (isAvoidGenerateTooClose) {
            l.e(TAG, "已开启避免滑块生成过近的功能");
            int suitableDistance = (totalWidth - mBlockWidth * 2 - mViewPadding * 2) / 5;
            if (totalWidth - (mStartBlockX + mBlockWidth) <= mBlockWidth + mViewPadding + suitableDistance) {
                mStartBlockX = totalWidth - (mBlockWidth + mViewPadding + suitableDistance) - mBlockWidth;
            }
        }
        if (mStartBlockX < validStartXRangeStart) {
            mStartBlockX = validStartXRangeStart;
        }
        if (mStartBlockY < validStartYRangeStart) {
            mStartBlockY = validStartYRangeStart;
        }
        //横轴方向确定目标滑块没有超出范围
        mTargetBlockX = mStartBlockX + mRealScrollDistance + mBlockWidth;
        int validTargetRangeEnd = totalWidth - mBlockWidth - mViewPadding;
        mTargetBlockX = Math.min(mTargetBlockX, validTargetRangeEnd);
        //纵轴方向无需修改
        mTargetBlockY = mStartBlockY;
    }

    /**
     * 设置垂直滑动模式
     */
    private void applyVerticalMode(int totalWidth, int totalHeight) {
        l.e(TAG, "垂直滑动模式启用");
        int validStartXRangeStart = mViewPadding;
        int validStartXRangeEnd = totalWidth - mBlockWidth - mViewPadding;
        int validStartYRangeStart = mViewPadding;
        int validStartYRangeEnd = totalHeight - mBlockHeight * 2 - mViewPadding;
        if (isOpenRandomBlockLocation) {
            l.e(TAG, "已开启随机滑块生成功能");
            mStartBlockX = (float) (Math.random() * validStartXRangeEnd);
            mStartBlockY = (float) (Math.random() * validStartYRangeEnd);
        } else {
            mStartBlockX = Math.min(mStartBlockX, validStartXRangeEnd);
            mStartBlockY = Math.min(mStartBlockY, validStartYRangeEnd);
        }
        if (isAvoidGenerateTooClose) {
            l.e(TAG, "已开启避免滑块生成过近的功能");
            int suitableDistance = (totalHeight - mBlockHeight * 2 - mViewPadding * 2) / 5;
            if (totalHeight - (mStartBlockY + mBlockHeight) <= mBlockHeight + mViewPadding + suitableDistance) {
                mStartBlockY = totalHeight - (mBlockHeight + mViewPadding + suitableDistance) - mBlockHeight;
            }
        }
        if (mStartBlockX < validStartXRangeStart) {
            mStartBlockX = validStartXRangeStart;
        }
        if (mStartBlockY < validStartYRangeStart) {
            mStartBlockY = validStartYRangeStart;
        }
        //横轴方向确定目标滑块没有超出范围
        mTargetBlockY = mStartBlockY + mRealScrollDistance + mBlockHeight;
        int validTargetRangeEnd = totalHeight - mBlockHeight - mViewPadding;
        mTargetBlockY = Math.min(mTargetBlockY, validTargetRangeEnd);
        //纵轴方向无需修改
        mTargetBlockX = mStartBlockX;
    }

    /**
     * 设置自由滑动模式
     */
    private void applyFreeMode(int totalWidth, int totalHeight) {
        l.e(TAG, "自由滑动模式启用");
        int validStartXRangeStart = mViewPadding;
        int validStartXRangeEnd = totalWidth - mBlockWidth - mViewPadding;
        int validStartYRangeStart = mViewPadding;
        int validStartYRangeEnd = totalHeight - mBlockHeight - mViewPadding;
        if (isOpenRandomBlockLocation) {
            l.e(TAG, "已开启随机滑块生成功能");
            mStartBlockX = (float) (Math.random() * validStartXRangeEnd);
            mStartBlockY = (float) (Math.random() * validStartYRangeEnd);
        } else {
            mStartBlockX = Math.min(mStartBlockX, validStartXRangeEnd);
            mStartBlockY = Math.min(mStartBlockY, validStartYRangeEnd);
        }
        if (mStartBlockX < validStartXRangeStart) {
            mStartBlockX = validStartXRangeStart;
        }
        if (mStartBlockY < validStartYRangeStart) {
            mStartBlockY = validStartYRangeStart;
        }

        //看人品的随机算法，其他模式都是一次生成，这个可能需要多次，不过即使多次影响几乎看不到
        boolean isNeedGenerate = true;
        int times = 0;
        while (isNeedGenerate) {
            l.d(TAG, "generateTimes:" + (times++));
            //先保证目标滑块的初始位置是在父容器内部
            mTargetBlockX = (float) (Math.random() * validStartXRangeEnd);
            mTargetBlockY = (float) (Math.random() * validStartYRangeEnd);

            RectF startBlock = new RectF(mStartBlockX, mStartBlockY, mStartBlockX + mBlockWidth, mStartBlockY + mBlockHeight);
            RectF targetBlock = new RectF(mTargetBlockX, mTargetBlockY, mTargetBlockX + mBlockWidth, mTargetBlockY + mBlockHeight);

            if (startBlock.intersect(targetBlock)) {//起始模块和目标模块相交
                isNeedGenerate = true;
            } else {
                if (isAvoidGenerateTooClose) {
                    float x1 = startBlock.centerX();
                    float y1 = startBlock.centerY();
                    float x2 = targetBlock.centerX();
                    float y2 = targetBlock.centerY();
                    float dx = Math.abs(x2 - x1);
                    float dy = Math.abs(y2 - y1);
                    int suitableDx = mBackBitmap.getWidth() / 5;
                    int suitableDy = mBackBitmap.getHeight() / 5;
                    l.d(TAG, "dx:" + dx);
                    l.d(TAG, "dy:" + dy);
                    isNeedGenerate = !(dx >= suitableDx || dy >= suitableDy);
                } else {
                    isNeedGenerate = false;
                }
            }
        }
    }

    /**
     * 获取实际显示的图片
     *
     * @return
     */
    private Bitmap getBaseBitmap() {
        Bitmap b = GraphicTools.drawable2Bitmap(getDrawable());
        if (b == null) {
            return null;
        }
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        // 如果图片的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的图片的宽高，一定要大于我们view的宽高；所以我们这里取大值；
        //该方法会在onMeasure中调用，此时getWidth获取值是0，所以使用getMeasuredWidth
        scaleX = getMeasuredWidth() * 1.0f / b.getWidth();
        scaleY = getMeasuredHeight() * 1.0f / b.getHeight();
        return GraphicTools.scaleSetBitmap(b, scaleX, scaleY);
    }

    /**
     * 获取可移动滑块的bitmap
     *
     * @param srcBitmap 基于控件背景图和滑块同样大小的bitmap
     * @return
     */
    private Bitmap getMovableBitmap(Bitmap srcBitmap) {
        Bitmap b = GraphicTools.drawable2Bitmap(mMovableBlockDrawable);
        b = GraphicTools.scalePostBitmap(b, ((float) mBlockWidth) / b.getWidth(), ((float) mBlockHeight) / b.getHeight());
        if (isOpenRandomRotate) {
            b = GraphicTools.rotatePostBitmap(b, mRotateDegree);
            b = GraphicTools.scalePostBitmap(b, ((float) mBlockWidth) / b.getWidth(), ((float) mBlockHeight) / b.getHeight());
        }
        Bitmap resultBmp = Bitmap.createBitmap(mBlockWidth, mBlockHeight, Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        Canvas canvas = new Canvas(resultBmp);
        canvas.drawBitmap(b, new Rect(0, 0, mBlockWidth, mBlockHeight), new Rect(0, 0, mBlockWidth, mBlockHeight),
                paint);
        // 选择交集去上层图片
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        canvas.drawBitmap(srcBitmap, new Rect(0, 0, mBlockWidth, mBlockHeight), new Rect(0, 0, mBlockWidth, mBlockHeight), paint);
        return resultBmp;
    }

    /**
     * 获取目标滑块bmp
     *
     * @return
     */
    private Bitmap getTargetBitmap() {
        Bitmap b = GraphicTools.drawable2Bitmap(mTargetBlockDrawable);
        b = GraphicTools.scalePostBitmap(b, ((float) mBlockWidth) / b.getWidth(), ((float) mBlockHeight) / b.getHeight());
        if (isOpenRandomRotate) {
            b = GraphicTools.rotatePostBitmap(b, mRotateDegree);
            b = GraphicTools.scalePostBitmap(b, ((float) mBlockWidth) / b.getWidth(), ((float) mBlockHeight) / b.getHeight());
        }
        return b;
    }

    private float lastTouchX;
    private float lastTouchY;
    private float firstTouchX;
    private float firstTouchY;
    private boolean isMove;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isMove = false;
                l.e(TAG, "action down");
                if (isBlockTouchable) {
                    lastTouchX = touchX;
                    lastTouchY = touchY;
                    firstTouchX = touchX;
                    firstTouchY = touchY;
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                l.e(TAG, "action up");
                if (isMove) {
                    if (verify()) {
                        if (mScrollVerifyListener != null) {
                            mScrollVerifyListener.onVerifyFinished(true);
                        }
                        l.d(TAG, "verify success");
                    } else {
                        if (mScrollVerifyListener != null) {
                            mScrollVerifyListener.onVerifyFinished(false);
                        }
                        l.d(TAG, "verify failed");
                    }
                    l.d(TAG, "verify finished");

                    firstTouchX = touchX;
                    firstTouchY = touchY;
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                isMove = isValidDrag(touchX, touchY, lastTouchX, lastTouchY, 1)
                        || isValidDrag(touchX, touchY, firstTouchX, firstTouchY, 1);
                if (isMove && isTouchInsideMovableBlock(touchX, touchY)) {
                    l.e(TAG, "action move");
                    switch (mScrollMode) {
                        case MODE_SCROLL_FREE:
                            mStartBlockX += touchX - lastTouchX;
                            mStartBlockY += touchY - lastTouchY;
                            break;
                        case MODE_SCROLL_ONLY_HORIZONTAL:
                            mStartBlockX += touchX - lastTouchX;
                            break;
                        case MODE_SCROLL_ONLY_VERTICAL:
                            mStartBlockY += touchY - lastTouchY;
                            break;
                        case MODE_SCROLL_FREE_HORIZONTAL:
                            mStartBlockX += touchX - lastTouchX;
                            mStartBlockY += touchY - lastTouchY;
                            break;
                        case MODE_SCROLL_FREE_VERTICAL:
                            mStartBlockX += touchX - lastTouchX;
                            mStartBlockY += touchY - lastTouchY;
                            break;
                    }
                    makeBlockInsideParent();
                    invalidate();
                    lastTouchX = touchX;
                    lastTouchY = touchY;
                } else {
                    l.e(TAG, "虽然是move操作，但位移不满足条件，视为单击");
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 确保滑块在指定范围里面滑动
     */
    private void makeBlockInsideParent() {
        int xStart = mViewPadding;
        int xEnd = mBackBitmap.getWidth() - mViewPadding - mBlockWidth;
        mStartBlockX = mStartBlockX > xEnd ? xEnd : mStartBlockX;
        mStartBlockX = mStartBlockX < xStart ? xStart : mStartBlockX;
        int yStart = mViewPadding;
        int yEnd = mBackBitmap.getHeight() - mViewPadding - mBlockHeight;
        mStartBlockY = mStartBlockY > yEnd ? yEnd : mStartBlockY;
        mStartBlockY = mStartBlockY < yStart ? yStart : mStartBlockY;
    }

    /**
     * 手指触摸区域是否在可移动的滑块内部
     *
     * @param touchX
     * @param touchY
     * @return
     */
    private boolean isTouchInsideMovableBlock(float touchX, float touchY) {
        RectF rectF = new RectF(mStartBlockX, mStartBlockY, mStartBlockX + mBlockWidth, mStartBlockY + mBlockHeight);
//        return touchX >= mStartBlockX && touchX <= mStartBlockX + mBlockWidth && touchY >= mStartBlockY && touchY <= mStartBlockY + mBlockHeight;
        return rectF.contains(touchX, touchY);
    }

    /**
     * 校验是否到达目标位置
     *
     * @return 如果起始滑块和目标滑块的中心点直线距离小于等于 {@link #mValidOffset}就视为成功，反之失败
     */
    private boolean verify() {
        float centerStartX = (mStartBlockX + mBlockWidth) / 2;
        float centerStartY = (mStartBlockY + mBlockHeight) / 2;
        float centerTargetX = (mTargetBlockX + mBlockWidth) / 2;
        float centerTargetY = (mTargetBlockY + mBlockHeight) / 2;
        float dx = centerStartX - centerTargetX;
        float dy = centerStartY - centerTargetY;
        return Math.sqrt(dx * dx + dy * dy) <= mValidOffset;
    }

    /**
     * 是否是有效的拖拽
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param offset
     * @return
     */
    private boolean isValidDrag(float x1, float y1, float x2, float y2, int offset) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy) >= offset;
    }

    /**
     * 描述:滑动验证控件监听器
     * <p>
     * <br>作者: 陈俊森
     * <br>创建时间: 2018/5/11 0011 17:24
     * <br>邮箱: chenjunsen@outlook.com
     *
     * @version 1.0
     */
    public interface ScrollVerifyListener {
        /**
         * 滑动验证结束
         *
         * @param isSuccess true-验证成功  false-验证失败
         */
        void onVerifyFinished(boolean isSuccess);
    }

    /**
     * 重置
     */
    public void reset() {
        isReset = true;
        mRotateDegree = isOpenRandomRotate ? (float) (Math.random() * 3 * 90) : mDefaultRotateDegree;
        mStartBlockX = mOriStartBlockX;
        mStartBlockY = mOriStartBlockY;
        invalidate();
    }

    @ScrollVerifyMode
    public int getScrollMode() {
        return mScrollMode;
    }

    public void setScrollMode(@ScrollVerifyMode int scrollMode) {
        mScrollMode = scrollMode;
    }

    public boolean isOpenLog() {
        return isOpenLog;
    }

    public void setOpenLog(boolean openLog) {
        isOpenLog = openLog;
    }

    public float getRotateDegree() {
        return mRotateDegree;
    }

    /**
     * 设置滑块的旋转角度
     *
     * @param rotateDegree
     */
    public void setRotateDegree(float rotateDegree) {
        mRotateDegree = rotateDegree;
        reset();
    }

    /**
     * 是否开启了随机滑块生成功能
     *
     * @return
     */
    public boolean isOpenRandomBlockLocation() {
        return isOpenRandomBlockLocation;
    }

    /**
     * 设置是否生成随机滑块的功能
     *
     * @param openRandomBlockLocation true-开启  false-关闭
     */
    public void setOpenRandomBlockLocation(boolean openRandomBlockLocation) {
        isOpenRandomBlockLocation = openRandomBlockLocation;
    }

    /**
     * 获取根据控件宽度生成滑块宽度时的比例系数，如5就代表生成滑块宽度是控件宽度的5分之1
     *
     * @return
     */
    public int getBlockWidthDivide() {
        return mBlockWidthDivide;
    }

    /**
     * 设置根据控件宽度生成滑块宽度时的比例系数。该方法只有在没有设置滑块宽度(没有调用{@link #setBlockWidth(int)})，或者设置滑块宽度为0时有效。
     * 设置该值后，如5就代表生成滑块宽度是控件宽度的5分之1，就会以此生成滑块宽度。
     *
     * @param blockWidthDivide 比例系数，大于零
     */
    public void setBlockWidthDivide(int blockWidthDivide) {
        if (blockWidthDivide <= 0) {
            throw new IllegalArgumentException("blockWidth系数要大于0");
        }
        mBlockWidthDivide = blockWidthDivide;
        requestLayout();
    }

    public int getBlockHeightDivide() {
        return mBlockHeightDivide;
    }

    /**
     * 设置根据控件高度生成滑块高度时的比例系数。该方法只有在没有设置滑块宽度(没有调用{@link #setBlockHeight(int)})，或者设置滑块高度为0时有效。
     * 设置该值后，如5就代表生成滑块高度是控件高度的5分之1，就会以此生成滑块高度。
     *
     * @param blockHeightDivide 比例系数，大于零
     */
    public void setBlockHeightDivide(int blockHeightDivide) {
        if (blockHeightDivide <= 0) {
            throw new IllegalArgumentException("blockHeight系数要大于0");
        }
        mBlockHeightDivide = blockHeightDivide;
        requestLayout();
    }

    /**
     * 获取滑块宽度
     *
     * @return
     */
    public int getBlockWidth() {
        return mBlockWidth;
    }

    /**
     * 设置滑块宽度
     *
     * @param blockWidth
     */
    public void setBlockWidth(int blockWidth) {
        if (blockWidth <= 0) {
            throw new IllegalArgumentException("滑块宽度必须大于零");
        }
        mBlockWidth = blockWidth;
        requestLayout();
    }

    /**
     * 获取滑块高度
     *
     * @return
     */
    public int getBlockHeight() {
        return mBlockHeight;
    }

    /**
     * 设置滑块高度
     *
     * @param blockHeight
     */
    public void setBlockHeight(int blockHeight) {
        if (blockHeight <= 0) {
            throw new IllegalArgumentException("滑块高度必须大于零");
        }
        mBlockHeight = blockHeight;
        requestLayout();
    }

    /**
     * 获取理论的滑块横坐标方向或者纵坐标方向的最大可移动距离，该距离可能不是实际的滑动距离，真实近距离跟
     * 滑块的生成位置有关系，如果需要真实距离，可以调用{@link #getRealScrollDistance()}
     *
     * @return
     */
    public int getMaxHorizontalScrollDistance() {
        return mMaxHorizontalScrollDistance;
    }

    /**
     * 设置理论的滑块横坐标方向或者纵坐标方向的最大可移动距离
     *
     * @param maxHorizontalScrollDistance 起始滑块的中心点到目标滑块的中心点的直线距离,该值只是一个理论范围值，
     *                                    用于帮助生成目标滑块的位置。要想获取最终的移动距离，请调用{@link #getRealScrollDistance()}
     */
    public void setMaxHorizontalScrollDistance(int maxHorizontalScrollDistance) {
        mMaxHorizontalScrollDistance = maxHorizontalScrollDistance;
        requestLayout();
    }

    /**
     * 获取目标滑块到起始滑块的初始中心点之间的真实距离
     *
     * @return
     */
    public int getRealScrollDistance() {
        return mRealScrollDistance;
    }

    /**
     * 起始滑块是否可以手指拖拽
     *
     * @return
     */
    public boolean isBlockTouchable() {
        return isBlockTouchable;
    }

    /**
     * 设置起始滑块是否可以手指拖拽
     *
     * @param blockTouchable
     */
    public void setBlockTouchable(boolean blockTouchable) {
        isBlockTouchable = blockTouchable;
    }

    /**
     * 获取拖拽验证的偏差值
     *
     * @return
     */
    public int getValidOffset() {
        return mValidOffset;
    }

    /**
     * 设置拖拽验证的偏差值
     *
     * @param validOffset 像素
     */
    public void setValidOffset(int validOffset) {
        mValidOffset = validOffset;
    }

    /**
     * 是否已经开启了避免随机生成滑块过近的功能
     *
     * @return
     */
    public boolean isAvoidGenerateTooClose() {
        return isAvoidGenerateTooClose;
    }

    /**
     * 设置是否开启避免随机生成滑块过近的功能
     *
     * @param avoidGenerateTooClose
     */
    public void setAvoidGenerateTooClose(boolean avoidGenerateTooClose) {
        isAvoidGenerateTooClose = avoidGenerateTooClose;
    }

    /**
     * 获取起始滑块的形状图片drawable
     *
     * @return
     */
    public Drawable getMovableBlockDrawable() {
        return mMovableBlockDrawable;
    }

    /**
     * 设置起始滑块的形状图片drawable
     *
     * @param movableBlockDrawable
     */
    public void setMovableBlockDrawable(Drawable movableBlockDrawable) {
        mMovableBlockDrawable = movableBlockDrawable;
        invalidate();
    }

    /**
     * 获取目标滑块的形状图片的drawable
     *
     * @return
     */
    public Drawable getTargetBlockDrawable() {
        return mTargetBlockDrawable;
    }

    /**
     * 目标滑块的形状图片的drawable
     *
     * @param targetBlockDrawable
     */
    public void setTargetBlockDrawable(Drawable targetBlockDrawable) {
        mTargetBlockDrawable = targetBlockDrawable;
        invalidate();
    }
}
