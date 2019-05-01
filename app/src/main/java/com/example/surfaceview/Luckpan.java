package com.example.surfaceview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Luckpan extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Thread thread;//用于绘制的线程
    private int mRadius;
    //绘制盘快的画笔
    private Paint mArcPaint;
    //绘制文本的画笔
    private Paint mTextPaint;
    private Boolean isRunning;//线程的控制开关
    //    盘快的奖项
    private String[] mStrs = new String[]{"单反相机", "iPad", "恭喜发财", "IPHONE", "服装一套", "恭喜发财"};//String数组
    //奖项的图片
    private int[] mImgs = new int[]{R.drawable.danfan, R.drawable.ipad, R.drawable.f040, R.drawable.iphone, R.drawable.meizi, R.drawable.f015};
    //与图片对应的bitmap数组
    private Bitmap[] bitmaps;
    //转盘背景
    private Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg2);
    //文本大小
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, getResources().getDisplayMetrics());
    //奖项的颜色
    private int[] mColors = new int[]{0xFFFFC300, 0XFFF17E01, 0XFFFFC300, 0XFFF17E01, 0xFFFFC300, 0XFFF17E01};
    private int mItemCount = 6;
    //盘快的范围
    private RectF mRange = new RectF();
    //盘快的直径
    //滚动速度
    private double mSpeed = 0;
    private volatile float mStartAngle = 0;

    //转盘的中心位置
    private int mCenter;
    private int mPadding;
    //判断是否点击停止
    private boolean isShouldEnd;

    public Luckpan(Context context) {
        this(context, null);
    }

    public Luckpan(Context context, AttributeSet attrs) {
        //构造方法
        super(context, attrs);
        //变量初始化
        mHolder = getHolder();
        mHolder.addCallback(this);
        //可获得焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredHeight(), getMeasuredWidth());
        //半径
        mPadding = getPaddingLeft();
        //直径
        mRadius = width - mPadding * 2;
        //中心点
        mCenter = width / 2;

        setMeasuredDimension(width, width);


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //初始化绘制盘快的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);

        //初始化绘制文本的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);
        //初始化盘快绘制的范围
        mRange = new RectF(mPadding, mPadding, mPadding + mRadius, mPadding + mRadius);
        //初始化图片
        bitmaps = new Bitmap[mItemCount];
        for (int i = 0; i < mItemCount; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), mImgs[i]);
        }

        isRunning = true;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isRunning) {
                    long start = System.currentTimeMillis();
                    draw();
                    long end = System.currentTimeMillis();
                    if (end - start < 50) {
                        try {
                            Thread.sleep(50 - (end - start));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


        });
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }

    //绘制方法（important）
    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            if (mCanvas != null) {
                //绘制背景
                drawBg();
                //绘制盘快
                float tmpAngle = mStartAngle;
                float sweepAngle = 360 / mItemCount;

                for (int i = 0; i < mItemCount; i++) {
                    mArcPaint.setColor(mColors[i]);
                    mCanvas.drawArc(mRange, tmpAngle, sweepAngle, true, mArcPaint);
                    //绘制文本
                    drawText(tmpAngle, sweepAngle, mStrs[i]);
                    //绘制Icon
                    drawIcon(tmpAngle, bitmaps[i]);
                    tmpAngle += sweepAngle;
                }
                mStartAngle += mSpeed;
                //判断是否点击了停止按钮
                if (isShouldEnd) {
                    mSpeed -= 1;

                }
                if (mSpeed <= 0) {
                    mSpeed = 0;
                    isShouldEnd = false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas);
            }

        }
    }

    //    点击启动旋转
    public void LuckyStart(int index) {
        mSpeed = 50;
        //作弊身法
        //计算每一项的角度
        float angle = 360 / mItemCount;
        //计算每一项中奖当前范围
        //1对应角度150~210
        //0 210~270
        float form = 270 - (index + 1) * angle;
        float end = form + angle;
        //设置停止需要旋转的距离
        float targetForm = 4 * 360 + form;
        float targetEnd = 4 * 360 + end;
        //开始和结束速度
        float v1 = (float) ((-1 + Math.sqrt(1 + 8 * targetForm)) / 2);
        float v2 = (float) ((-1 + Math.sqrt(1 + 8 * targetEnd)) / 2);
        mSpeed = v1 + Math.random() * (v2 - v1);


        isShouldEnd = false;
    }

    public void LuckyStartWin() {
        mSpeed = 50;
        isShouldEnd = false;
    }

    public void LuckyEnd() {
        mStartAngle = 0;
        isShouldEnd = true;
    }

    public void LuckyEndWin() {

        isShouldEnd = true;
    }

    //判断转盘是否在旋转
    public boolean isStart() {
        return mSpeed != 0;
    }

    public boolean isShouldEnd() {
        return isShouldEnd;
    }

    //绘制Icon
    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        //适配屏幕，设置图片的宽度是直径的1/8
        int imgWidth = mRadius / 8;

        float angle = (float) ((tmpAngle + 360 / mItemCount / 2) * Math.PI / 180);
        //图片中心点坐标
        int x = (int) (mCenter + mRadius / 2 / 2 * Math.cos(angle));
        int y = (int) (mCenter + mRadius / 2 / 2 * Math.sin(angle));
        //确定某个图片位置
        Rect rect = new Rect(x - imgWidth, y - imgWidth / 2, x + imgWidth / 2, y + imgWidth / 2);
        mCanvas.drawBitmap(bitmap, null, rect, null);

    }


    //绘制每个盘快文本
    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();
        //利用水平偏移量居中
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius * Math.PI / mItemCount / 2 - textWidth / 2);
        path.addArc(mRange, tmpAngle, sweepAngle);
        //垂直偏移量
        int vOffset = mRadius / 2 / 6;
        mCanvas.drawTextOnPath(mStr, path, hOffset, vOffset, mTextPaint);

    }

    //绘制背景
    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(bitmap, null, new Rect(mPadding / 2, mPadding / 2, getMeasuredWidth() - mPadding / 2, getMeasuredHeight() - mPadding / 2), null);
    }

}
