package com.example.user.puzzle01.com.example.user.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.user.puzzle01.R;
import com.example.user.puzzle01.com.example.user.LogManager;
import com.example.user.puzzle01.com.example.user.utils.ImagePiece;
import com.example.user.puzzle01.com.example.user.utils.ImageSplitterUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class GamePuzzleLayout extends RelativeLayout implements View.OnClickListener {

    // 3 * 3 拼圖
    private int mColumn = 3;

    private int mPadding;

    //每個piece的邊距
    private int mMargin = 3; //dp

    private ImageView[] mGamePuzzleItems;

    private int mItemWidth;
    private final int src = R.drawable.a4;
    //game image
    private Bitmap mBitmap;
    private List<ImagePiece> mItemBitmaps;
    private boolean once;

    /*game 面板的寬度*/
    private int mWidth;

    private boolean isGameSucess;
    private boolean isGameOver;

    public interface GamePuzzleListener {
        void nextLevel(int nextLevel);

        void timeChanged(int currentTime);

        void gameover();
    }

    public GamePuzzleListener mGamePuzzleListener;

    public void setOnGamePuzzleListener(GamePuzzleListener mGamePuzzleListener) {
        this.mGamePuzzleListener = mGamePuzzleListener;
    }

    private int mLevel = 1;
    private static final int TIME_CHANGED = 0x110;
    private static final int NEXT_LEVEL = 0x111;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_CHANGED:
                    if (isGameSucess || isGameOver || isPause)
                        return;

                    if (mGamePuzzleListener != null) {
                        mGamePuzzleListener.timeChanged(mTime);

                    }
                    if (mTime == 0) {
                        isGameOver = true;
                        mGamePuzzleListener.gameover();
                        return;
                    }
                    mTime--;
                    //1second 後再次觸發
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED, 1000);

                    break;
                case NEXT_LEVEL:
                    mLevel++;
                    if (mGamePuzzleListener != null) {
                        mGamePuzzleListener.nextLevel(mLevel);
                    } else {
                        nextLevel();
                    }
                    break;
                default:
                    break;
            }
        }
    };


    private boolean isTimeEnable = false;
    private int mTime;

    //設定是否開啟計時
    public void setTimeEnable(boolean isTimeEnable) {
        this.isTimeEnable = isTimeEnable;
    }


    public GamePuzzleLayout(Context context) {
        super(context, null);
    }

    public GamePuzzleLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public GamePuzzleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    //設定 layout 本身的大小
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = Math.min(getMeasuredHeight(), getMeasuredWidth());
        Log.d(LogManager.TAG , String.format("getMeasuredHeight() = %d , getMeasuredWidth = %d ,getWidth = %d",
                getMeasuredHeight() ,getMeasuredWidth() , getWidth()));


        //執行一次
        if (!once) {
            //Slicing and Sorting
            initBitmap();
            //set every imageView 's width , height and margin  etc.. properties
            initItem();
            //check if use timing;
            checkTimeEnable();

            once = true;
        }

        setMeasuredDimension(mWidth, mWidth);
    }

    private void checkTimeEnable() {
        if (isTimeEnable) {
            //set time accroding to level
            countTimeBaseLevel();
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    private void countTimeBaseLevel() {
       mTime = (int) Math.pow(2, mLevel) * 60; //  1:120 2:240  3:480 4:960 s

    }

    private void initBitmap() {
        if (mBitmap == null) {
            mBitmap = BitmapFactory.decodeResource(getResources(), src);
        }

        mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap, mColumn);

        //使用自訂sort 完成亂序
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece a, ImagePiece b) {
                //Math.radom() :return 0~ 1(含0 不含1)
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }


    private void initItem() {
        //caculate each item's width
       // mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;
        mItemWidth = (mWidth - getPaddingLeft()-getPaddingRight() - (mColumn - 1) * mMargin) / mColumn ;
        mGamePuzzleItems = new ImageView[mColumn * mColumn];


        for (int i = 0; i < mGamePuzzleItems.length; i++) {
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);

            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());

            mGamePuzzleItems[i] = item;
            item.setId(i + 1);
            //在item(每一小格)中儲存了該位置ImagePiece index
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);


            //  not last column
            // set item's column margin , rightMargin
            if ((i + 1) % mColumn != 0) {
                lp.rightMargin = mMargin;
            }

            //not first column
            if (i % mColumn != 0) {
                lp.addRule(RelativeLayout.RIGHT_OF, mGamePuzzleItems[i - 1].getId());
            }
            //not first row
            if ((i + 1) > mColumn) {
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, mGamePuzzleItems[i - mColumn].getId());
            }
            addView(item, lp);
        }


    }


    private void init() {
        // dp 轉 pixel
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());

        //如果在xml中設至多個padding，則取最小值
        mPadding = min(getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());

    }

    public void restart() {
        isGameOver = false;
        mColumn--;
        nextLevel();
    }
    private boolean isPause;
    public void pause(){
        isPause=true;
        mHandler.removeMessages(TIME_CHANGED);
    }
    public void resume(){
        if(isPause){
            isPause=false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    public void nextLevel() {
        this.removeAllViews();
        mAnimLayout = null;
        mColumn++;
        isGameSucess = false;
        checkTimeEnable();
        initBitmap();
        initItem();
    }

    //傳入多個參數 ，取得最小的一個
    private int min(int... params) {
        int min = params[0];
        for (int param : params) {
            if (param < min)
                min = param;
        }
        return min;
    }


    private ImageView mFirst;
    private ImageView mSecond;

    @Override
    public void onClick(View v) {
        if (isAniming)
            return;
        //兩次都點擊同一個item >取消
        if (mFirst == v) {
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }

        if (mFirst == null) {
            mFirst = (ImageView) v;
            mFirst.setColorFilter(getResources().getColor(R.color.selectedColor));
        } else {
            mSecond = (ImageView) v;
            exchangeView();
        }
    }

    private RelativeLayout mAnimLayout;
    private boolean isAniming;

    //exchange two image
    private void exchangeView() {
        mFirst.setColorFilter(null);

        //setUp animLayout;
        setUpAnimLayout();


        //複製欲交換兩張圖 到動畫層
        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag((String) mFirst.getTag())).getBitmap();
        first.setImageBitmap(firstBitmap);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(getImageIdByTag((String) mSecond.getTag())).getBitmap();
        second.setImageBitmap(secondBitmap);

        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(mItemWidth, mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);


        //設置交換動畫
        TranslateAnimation anim = new TranslateAnimation(0, mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation anim2 = new TranslateAnimation(0, -mSecond.getLeft() + mFirst.getLeft(), 0, -mSecond.getTop() + mFirst.getTop());
        anim2.setDuration(300);
        anim2.setFillAfter(true);
        second.startAnimation(anim2);

        // listen animation
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                //動畫開始時，隱藏原本層的兩張圖
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);
                isAniming = true;
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                //動畫結束時，原本層的圖做交換and show ， and 清除動畫層
                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
                //after every exchange , check if Puzzle is completed
                checkSuccess();
                isAniming = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


    }

    /**
     *
     */
    private void checkSuccess() {
        boolean isSuccess = true;
        for (int i = 0; i < mGamePuzzleItems.length; i++) {
            if (getImageIndexByTag((String) mGamePuzzleItems[i].getTag()) != i) {
                isSuccess = false;
            }

        }
        if (isSuccess) {
            isGameSucess = true;
            mHandler.removeMessages(TIME_CHANGED);

            Toast.makeText(getContext(), "success ", Toast.LENGTH_LONG).show();
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }

    public int getImageIdByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    public int getImageIndexByTag(String tag) {
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }


    private void setUpAnimLayout() {
        if (mAnimLayout == null) {
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }
}
