package a6he.android.yzz.com.snake;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/2/18 0018.
 */
public class SnakeView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private SurfaceHolder mHolder;
    //线程池
    private ExecutorService mThreadPool;
    private Food mFood;
    private User mUser;

    //表情集合
    private List<Bitmap> listBiaoqing;

    private Bitmap mBg;
    //矩形区域
    private Rect mRect;
    private Paint mPaint;
    private Random myRandom;
    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new MyListener());
    private Canvas mCanvas;
    private boolean isNeddFood = true;

    private int mState = DOWN;
    public static final int UP = 1;
    public static final int DOWN = 2;
    public static final int LEFT = 3;
    public static final int RIGHT = 4;
    private boolean isChange = false;

    public SnakeView(Context context) {
        super(context);
        init();
    }

    public SnakeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SnakeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mThreadPool.execute(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    private void init() {
        mHolder = getHolder();
        //添加回调接口
        mHolder.addCallback(this);
        mThreadPool = Executors.newFixedThreadPool(5);
        listBiaoqing = new ArrayList<>();
        mBg = BitmapFactory.decodeResource(getResources(), R.mipmap.bg).copy(Bitmap.Config.ARGB_8888, true);

        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.dit));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.diw));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.dix));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.diz));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.djb));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.djc));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.djf));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.djg));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.djh));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.dji));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.djl));
        listBiaoqing.add(BitmapFactory.decodeResource(getResources(), R.mipmap.djm));
        myRandom = new Random();
        mFood = new Food();
        mRect = new Rect(0, 0, getWidth(), getHeight());
        mPaint = new Paint();
        mCanvas = new Canvas(mBg);
        mUser = new User();

    }

    @Override
    public void run() {
        while (!isChange) {
            try {
                draw();
                move();
                checkEat();
                draw();
                if(check()){
                   restart();
                }
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private void restart() {
        mUser.mUserList.clear();
        mUser.mUserIcon.clear();
        mUser.reSet();
        mFood.changeFood();
        mState = DOWN;

    }

    private void move() {
        Point first = mUser.mUserList.getFirst();
        Point last = mUser.mUserList.getLast();
        switch (mState) {
            case UP:
                last.setX(first.getX());
                last.setY(first.getY() - 56);
                break;
            case DOWN:
                last.setX(first.getX());
                last.setY(first.getY() + 56);
                break;
            case LEFT:
                last.setX(first.getX() - 56);
                last.setY(first.getY());
                break;
            case RIGHT:
                last.setX(first.getX() + 56);
                last.setY(first.getY());
                break;
        }
        mUser.mUserList.addFirst(last);
        mUser.mUserList.removeLast();

    }

    public void draw() {
        mBg = BitmapFactory.decodeResource(getResources(), R.mipmap.bg).copy(Bitmap.Config.ARGB_8888, true);
        mCanvas.setBitmap(mBg);
        mFood.drawFoodPoint();
        mUser.drawUserPoint();
        Canvas canvas = mHolder.lockCanvas(mRect);
        canvas.drawBitmap(mBg, new Rect(0, 0, mBg.getWidth(), mBg.getHeight()), mRect, mPaint);
        mHolder.unlockCanvasAndPost(canvas);
    }

    public void checkEat() {
        Point user = mUser.mUserList.getFirst();
        float x = Math.abs(user.getX() - mFood.mFoodPoint.getX());
        float y = Math.abs(user.getY() - mFood.mFoodPoint.getY());
        if (x <= 56 && x >= 0) {
            if (y <= 56 && y >= 0) {
                //吃了
                mUser.mUserIcon.addLast(listBiaoqing.get(myRandom.nextInt(listBiaoqing.size())));
                Point p = mUser.mUserList.getLast();
                Point now = new Point(p.getX(), p.getY());
                switch (mState) {
                    case UP:
                        now.setX(p.getX());
                        now.setY(p.getY() + 56);
                        break;
                    case DOWN:
                        now.setX(p.getX());
                        now.setY(p.getY() - 56);
                        break;
                    case LEFT:
                        now.setX(p.getX() + 56);
                        now.setY(p.getY());
                        break;
                    case RIGHT:
                        now.setX(p.getX());
                        now.setY(p.getY() - 56);
                        break;
                }
                mUser.mUserList.addLast(now);
                //改变事物
                mFood.changeFood();
            }
        }
    }


    class MyListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            //确定方向
            float x = e2.getX() - e1.getX();
            float y = e2.getY() - e1.getY();
            if (Math.abs(x) > Math.abs(y)) {
                //x方向的手势
                if (x > 0) {
                    right();
                } else {
                    left();
                }
            } else {
                //y轴方向
                if (y > 0) {
                    down();
                } else {
                    up();
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }
    }

    private void up() {
        if (mState != UP && mState != DOWN) {
            //可以向上移动
            mState = UP;
        }
    }

    private void down() {
        if (mState != UP && mState != DOWN) {
            //可以向上移动
            mState = DOWN;
        }
    }


    public void left() {
        if (mState != RIGHT && mState != LEFT) {
            //可以向右移动
            mState = LEFT;
        }
    }


    public void right() {
        if (mState != RIGHT && mState != LEFT) {
            //可以向右移动
            mState = RIGHT;
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    //food
    class Food {
        //食物的坐标位置
        private Point mFoodPoint;
        private Random mRandom;
        public static final int size = 56;
        private Bitmap mIcon;

        public Food() {
            mRandom = new Random();
            mFoodPoint = new Point(mRandom.nextInt(mBg.getWidth() - size), mRandom.nextInt(mBg.getHeight() - size));
            mIcon = listBiaoqing.get(myRandom.nextInt(listBiaoqing.size()));
        }

        public void changeFood() {
            mFoodPoint.setX(mRandom.nextInt(mBg.getWidth() - size));
            mFoodPoint.setY(mRandom.nextInt(mBg.getHeight() - size));
            mIcon = listBiaoqing.get(myRandom.nextInt(listBiaoqing.size()));
        }

        public void drawFoodPoint() {
            mCanvas.drawBitmap(mIcon, mFood.mFoodPoint.getX(), mFood.mFoodPoint.getY(), mPaint);
        }

    }

    //user
    class User {
        private LinkedList<Point> mUserList;
        private LinkedList<Bitmap> mUserIcon;

        public User() {
            mUserList = new LinkedList<>();
            mUserIcon = new LinkedList<>();
            Bitmap b = listBiaoqing.get(myRandom.nextInt(listBiaoqing.size()));
            Point point = new Point(56, 56);
            mUserList.addFirst(point);
            mUserIcon.addFirst(b);
        }

        public void drawUserPoint() {
            for (int i = 0; i < mUserList.size(); i++) {
                Bitmap bb = Bitmap.createScaledBitmap(mUserIcon.get(i), mUserIcon.get(i).getWidth() - i, mUserIcon.get(i).getHeight() - i, true);
                Point p = mUserList.get(i);
                mCanvas.drawBitmap(bb, p.getX(), p.getY(), mPaint);
            }
        }

        public void reSet() {
            Bitmap b = listBiaoqing.get(myRandom.nextInt(listBiaoqing.size()));
            Point point = new Point(56, 56);
            mUserList.addFirst(point);
            mUserIcon.addFirst(b);
        }
    }

    public boolean check() {
        //是否撞到墙
        Point p = mUser.mUserList.getFirst();
        if (p.getX() < 0 || p.getX() > mBg.getWidth() - 56) {
            //撞墙
            return true;
        }
        if (p.getY() < 0 || p.getY() > mBg.getHeight() - 56) {
            //撞墙
            return true;
        }
        //是否被自己撞到了
        if (mUser.mUserList.size()<3){
            return false;
        }
        for (int i = 2; i < mUser.mUserList.size(); i++) {
            Point point = mUser.mUserList.get(i);
            float x = Math.abs(point.getX() - p.getX());
            float y = Math.abs(point.getY() - p.getY());
            if (x < 56 ) {
                if (y < 56) {
                    //
                    Log.e("==========","======="+x+"==="+y);
                    return true;
                }
            }
        }
        return false;
    }
}



