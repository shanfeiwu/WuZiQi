package wuziqi.wsf.com.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.telephony.CarrierConfigManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

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

    private boolean mWhiteIsWinner;
    private boolean mGameOver;
    private static int WIN_COUNT=5;


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
        View view = null;
        view.getVisibility();
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
            try {
                Log.i(TAG,"testCarrierServicesIsTheDefaultImsPackage");
                testCarrierServicesIsTheDefaultImsPackage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            int x = (int) event.getX();
            int y = (int) event.getY();
            Point p = getValidPoint(x,y);
            if(mArrayWhite.contains(p)||mArrayBlack.contains(p)){
                return false;
            }
            if(mIsWhite){
                mArrayWhite.add(p);
                checkGameOver(mArrayWhite,p);
                mIsWhite = false;
            }else {
                mArrayBlack.add(p);
                checkGameOver(mArrayBlack,p);
                mIsWhite = true;
            }
            invalidate();
        }
        return true;
    }

    private void checkGameOver(ArrayList<Point> arrayPoint,Point p) {
        boolean leftBreak = false;
        boolean rightBreak = false;
        int count = 1;
        int caseWinMode = 0;//max == 3
        final int  horizental = 0;
        final int vertical = 1;
        final int leftSkew = 2;
        final int rightSkew = 3;
        for(int i=0;i<4;i++){
           for(Point leftPoint=p,rightPoint=p;count<5&&(leftBreak&&rightBreak);){
               switch (i){
                   case horizental:
                       leftPoint = new Point(leftPoint.x-1,leftPoint.y);
                       rightPoint = new Point(rightPoint.x+1,rightPoint.y);
                       break;
                   case vertical:
                       leftPoint = new Point(leftPoint.x,leftPoint.y-1);
                       rightPoint = new Point(rightPoint.x,rightPoint.y+1);
                       break;
                   case leftSkew:
                       leftPoint = new Point(leftPoint.x-1,leftPoint.y-1);
                       rightPoint = new Point(rightPoint.x+1,rightPoint.y+1);
                       break;
                   case rightSkew:
                       leftPoint = new Point(leftPoint.x-1,leftPoint.y+1);
                       rightPoint = new Point(rightPoint.x+1,rightPoint.y-1);
                       break;
               }
           }
        }
    }

    private Point getValidPoint(int x, int y) {
        Point p = new Point(x/mLineHeight,y/mLineHeight);
        return p;
    }

    public void testCarrierServicesIsTheDefaultImsPackage()
            throws Exception
    {
        if (true)
        {
            Object localObject = (CarrierConfigManager)getContext().getSystemService("carrier_config");
            boolean bool2 = false;
            boolean bool1;
            if (localObject != null) {
                bool1 = true;
            } else {
                bool1 = false;
            }
            Log.i(TAG,"Could not get the carrier config manager."+bool1);
            localObject = ((CarrierConfigManager)localObject).getConfig().getString("config_ims_package_override_string");
            Log.i(TAG,(localObject==null)+"");
            if ((localObject != null) && (((String)localObject).equals("com.google.android.ims"))) {
                bool1 = true;
            } else {
                bool1 = bool2;
            }
            Log.i(TAG,"CS not the default IMS package: "+bool1);
        }
    }

}
