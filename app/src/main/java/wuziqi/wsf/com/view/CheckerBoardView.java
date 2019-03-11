package wuziqi.wsf.com.view;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import wuziqi.wsf.com.MainActivity;
import wuziqi.wsf.com.R;


public class CheckerBoardView extends View {
    private static final String TAG = "CheckerBoardView";
    private Paint mCheckerBoardPaint;
    private static int MAX_LINE = 10;
    private int mPanelWidth ;
    private int mLineHeight ;
    private Bitmap mWhiteFlag;
    private Bitmap mBlackFlag;
    private float ratioFlagOfLineHeight = 0.25f;

    private boolean mIsWhite;
    private ArrayList<Point> mArrayWhite = new ArrayList<>();
    private ArrayList<Point> mArrayBlack = new ArrayList<>();


    public CheckerBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);
        init();
    }

    private void init() {
        mCheckerBoardPaint = new Paint();
        mCheckerBoardPaint.setColor(0x88000000);
        mCheckerBoardPaint.setAntiAlias(true);
        mCheckerBoardPaint.setDither(true);
        mCheckerBoardPaint.setStyle(Paint.Style.STROKE);

        mWhiteFlag = BitmapFactory.decodeResource(getResources(),R.drawable.white_flag);
        mBlackFlag = BitmapFactory.decodeResource(getResources(),R.drawable.black_flag);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode == MeasureSpec.UNSPECIFIED){
            widthSize = widthSize;
        }else if(heightMode == MeasureSpec.UNSPECIFIED){
            heightSize=heightSize;
        }
        setMeasuredDimension(widthSize,widthSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPanelWidth = w;
        mLineHeight = (int) (mPanelWidth*1.0/MAX_LINE);

        mWhiteFlag = Bitmap.createScaledBitmap(mWhiteFlag, (int)(mLineHeight*ratioFlagOfLineHeight),(int) (mLineHeight*ratioFlagOfLineHeight),false);
        mBlackFlag = Bitmap.createScaledBitmap(mBlackFlag, (int)(mLineHeight*ratioFlagOfLineHeight),(int) (mLineHeight*ratioFlagOfLineHeight),false);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onDrawCheckerBoard(canvas);
        onDrawFlag(canvas);
        clearAnimation();
    }

    private void onDrawFlag(Canvas canvas) {
        for (int i=0;i<mArrayWhite.size();i++){
            Point p = mArrayWhite.get(i);
            canvas.drawBitmap(mWhiteFlag,(float)((p.x+0.5-ratioFlagOfLineHeight/2)*mLineHeight),(float)((p.y+0.5-ratioFlagOfLineHeight/2)*mLineHeight),null);
        }
        for (int i=0;i<mArrayBlack.size();i++){
            Point p = mArrayBlack.get(i);
            canvas.drawBitmap(mBlackFlag,(float)((p.x+0.5-ratioFlagOfLineHeight/2)*mLineHeight),(float)((p.y+0.5-ratioFlagOfLineHeight/2)*mLineHeight),null);
        }
    }

    private void onDrawCheckerBoard(Canvas canvas) {
        for(int i=0;i<MAX_LINE;i++){
            canvas.drawLine((float) mLineHeight/2,(float)(0.5+i)*mLineHeight,mPanelWidth-mLineHeight/2,(float)(0.5+i)*mLineHeight,mCheckerBoardPaint);
            canvas.drawLine((float)(0.5+i)*mLineHeight,(float) mLineHeight/2,(float)(0.5+i)*mLineHeight,mPanelWidth-mLineHeight/2,mCheckerBoardPaint);
        }
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if(action==MotionEvent.ACTION_UP){
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x,y);
            if(mArrayWhite.contains(p)||mArrayBlack.contains(p)){
                return false;
            }
            if(mIsWhite){
                mArrayWhite.add(p);
                checkGameOver(mArrayWhite,p);
            }else {
                mArrayBlack.add(p);
                checkGameOver(mArrayBlack,p);
            }
            invalidate();
        }
        return true;
    }

    /*
    *
    *
    * */
    private void checkGameOver(ArrayList<Point> arrayPoint,Point p) {
        boolean leftBreak ;
        boolean rightBreak ;
        int count ;
        final int  horizental = 0;
        final int vertical = 1;
        final int leftSkew = 2;
        final int rightSkew = 3;
        for(int i=0;i<4;i++){
            leftBreak = false;
            rightBreak = false;
            count = 0;
            for(Point leftPoint=p,rightPoint=p;count<4;){
               switch (i){
                   case horizental:
                       if(!leftBreak){
                           leftPoint = new Point(leftPoint.x-1,leftPoint.y);
                           leftBreak = !arrayPoint.contains(leftPoint);
                           count = !leftBreak ? count+1:count;
                       }
                        if(!rightBreak){
                            rightPoint = new Point(rightPoint.x+1,rightPoint.y);
                            rightBreak = !arrayPoint.contains(rightPoint);
                            count = !rightBreak ? count+1:count;
                        }
                       break;
                   case vertical:
                       if(!leftBreak){
                           leftPoint = new Point(leftPoint.x,leftPoint.y-1);
                           leftBreak = !arrayPoint.contains(leftPoint);
                           count = !leftBreak ? count+1:count;
                       }
                       if(!rightBreak){
                           rightPoint = new Point(rightPoint.x,rightPoint.y+1);
                           rightBreak = !arrayPoint.contains(rightPoint);
                           count = !rightBreak ? count+1:count;
                       }
                       break;
                   case leftSkew:
                       if(!leftBreak){
                           leftPoint = new Point(leftPoint.x-1,leftPoint.y-1);
                           leftBreak = !arrayPoint.contains(leftPoint);
                           count = !leftBreak ? count+1:count;
                       }
                       if(!rightBreak){
                           rightPoint = new Point(rightPoint.x+1,rightPoint.y+1);
                           rightBreak = !arrayPoint.contains(rightPoint);
                           count = !rightBreak ? count+1:count;
                       }
                       break;
                   case rightSkew:
                       if(!leftBreak){
                           leftPoint = new Point(leftPoint.x-1,leftPoint.y+1);
                           leftBreak = !arrayPoint.contains(leftPoint);
                           count = !leftBreak ? count+1:count;
                       }
                       if(!rightBreak){
                           rightPoint = new Point(rightPoint.x+1,rightPoint.y-1);
                           rightBreak = !arrayPoint.contains(rightPoint);
                           count = !rightBreak ? count+1:count;
                       }
                       break;
               }
               if(leftBreak && rightBreak){
                   count=5;
                   break;
               }
               if(count==4){
                   mArrayBlack.clear();
                   mArrayWhite.clear();
                   if(mIsWhite){
                       Toast.makeText(getContext(), "白棋胜利", Toast.LENGTH_SHORT).show();
                   }else{
                       Toast.makeText(getContext(),"黑棋胜利",Toast.LENGTH_SHORT).show();
                   }
                   i=4;
                   mIsWhite = false;
                   break;
               }
           }
        }
        mIsWhite=!mIsWhite;
    }

    private Point getValidPoint(int x, int y) {
        Point p = new Point(x/mLineHeight,y/mLineHeight);
        return p;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        MyArraylistParcel myArraylistParcel = (MyArraylistParcel) state;
        mArrayBlack = (ArrayList<Point>) myArraylistParcel.blackArraylist;
        mArrayWhite = (ArrayList<Point>) myArraylistParcel.whiteArraylist;
        requestLayout();
    }

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        MyArraylistParcel myArraylistParcel = new MyArraylistParcel(parcelable);
        myArraylistParcel.whiteArraylist = mArrayWhite;
        myArraylistParcel.blackArraylist = mArrayBlack;
        return myArraylistParcel;
    }

    static class MyArraylistParcel extends BaseSavedState {
        List<Point> whiteArraylist = null;
        List<Point> blackArraylist = null;

        public MyArraylistParcel(Parcelable parcelable){
            super(parcelable);
        }

        protected MyArraylistParcel(Parcel in) {
            super(in);
            if(whiteArraylist==null){
                whiteArraylist = new ArrayList<>();
            }
            if(blackArraylist == null){
                blackArraylist = new ArrayList<>();
            }
            in.readTypedList(whiteArraylist,Point.CREATOR);
            in.readTypedList(blackArraylist,Point.CREATOR);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(whiteArraylist);
            dest.writeTypedList(blackArraylist);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<MyArraylistParcel> CREATOR = new Creator<MyArraylistParcel>() {
            @Override
            public MyArraylistParcel createFromParcel(Parcel in) {
                return new MyArraylistParcel(in);
            }

            @Override
            public MyArraylistParcel[] newArray(int size) {
                return new MyArraylistParcel[size];
            }
        };
    }

}
