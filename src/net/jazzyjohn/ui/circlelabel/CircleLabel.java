package net.jazzyjohn.ui.circlelabel;

import com.example.moneydo.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircleLabel extends View {
	
	//Circle BackGround Color
	private int mBackgroundColor;
	//Circle Border Color
	private int mBorderColor;
	//Circle Text Color
	private int mTextColor;
	
	private static float RADIUS_TO_TEXT = 4f;
	
	private float mCenterX;
	
	private float mCenterY;
	
	private float mRadius;
	
	private float mBorderSize;
	
	private String mLabel;
	
	private String mText="";

	private Paint mCirclePaint;
	private Paint mBorderPaint;
	private Paint mTextPaint;
	
	public RectF mCircleBounds = new RectF();
	
  	public RectF mBorderBounds = new RectF();
	
	
  	public void SetText(String text ){
  		mText= text;
  	}
	public CircleLabel(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleLabel,
                0, 0);

           try {
        	  
        	   
        	  
        	   mBackgroundColor = a.getInt(R.styleable.CircleLabel_BackgroundColor, 1);
        	   mBorderColor = a.getInt(R.styleable.CircleLabel_BorderColor, 1);
        	   mTextColor = a.getInt(R.styleable.CircleLabel_TextColor, 1000);
        	   mLabel = a.getString(R.styleable.CircleLabel_Label);
        	 
           } finally {
               a.recycle();
           }
           init();
    }
	private void init() {
		   mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		   mTextPaint.setColor(mTextColor);
	     
	       mTextPaint.setTextAlign(Paint.Align.CENTER);
		   

	       mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	       mCirclePaint.setStyle(Paint.Style.FILL);
	   
	       mCirclePaint.setColor(mBackgroundColor);
	      
	       mBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	       mBorderPaint.setStyle(Paint.Style.FILL);
	       mBorderPaint.setColor(mBorderColor);
	}
	protected void onDraw(Canvas canvas) {
		   super.onDraw(canvas);
		   // Draw the shadow
		   canvas.drawOval(mBorderBounds, mBorderPaint);
		   canvas.drawOval(mCircleBounds, mCirclePaint);
		   
		  
		   // Draw the label text
		   mTextPaint.setTextSize(mRadius/RADIUS_TO_TEXT);
		   if(mLabel==null){
			   canvas.drawText(mText, mCenterX, mCenterY, mTextPaint);
		   }else{
			   canvas.drawText(mLabel, mCenterX, mCenterY-mTextPaint.getTextSize(), mTextPaint);
			   canvas.drawText(mText, mCenterX, mCenterY+mTextPaint.getTextSize(), mTextPaint);
		   }	
		   
		   
	}
	  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	        super.onSizeChanged(w, h, oldw, oldh);

	        //
	        // Set dimensions for text, pie chart, etc
	        //
	        // Account for padding
	        float xpad = (float) (getPaddingLeft() + getPaddingRight());
	        float ypad = (float) (getPaddingTop() + getPaddingBottom());

	       
	        float ww = (float) w - xpad;
	        float hh = (float) h - ypad;
	        
	        mCenterX =ww*50/100;
	        mCenterY =hh*50/100;
	        mRadius = Math.min(hh,ww)/2;
	       
	        float mInnerRadius = mRadius-mBorderSize;
	  		 mCircleBounds = new RectF(
	  				 	mCenterX-mInnerRadius,
		        		mCenterY-mInnerRadius,
		        		mCenterX+mInnerRadius,
		        		mCenterY+mInnerRadius);
		        
		        mBorderBounds= new RectF(
		        		mCenterX-mRadius,
		        		mCenterY-mRadius,
		        		mCenterX+mRadius,
		        		mCenterY+mRadius);
	        

	       
	    }
}
