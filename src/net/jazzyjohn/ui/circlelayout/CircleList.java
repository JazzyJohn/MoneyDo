package net.jazzyjohn.ui.circlelayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.example.moneydo.R;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CircleList extends View {
	
	

	//Circle Root Radius
	private float mRootRadius;
	//circle Node Radius
	private float mNodeRadius;
	//Circle BackGround Color
	private int mRootBackgroundColor;
	//Circle Border Color
	private int mRootBorderColor;
	//Circle Text Color
	private int mRootTextColor;
	//Circle BackGround Color
	private int mNodeBackgroundColor;
	//Circle Border Color
	private int mNodeBorderColor;
	//Circle Text Color
	private int mNodeTextColor;
	//Border Size
	private float mBorderSize;
	//anim speed 
	private long mAnimationTime;
	
	private Paint mNodeCirclePaint;
	private Paint mNodeBorderPaint;
	private Paint mNodeTextPaint;
	 
	private Paint mRootCirclePaint;
	private Paint mRootBorderPaint;
	private Paint mRootTextPaint;
	 
	private static float RADIUS_TO_TEXT = 4f;
	
	private float mListCenterX;
	
	private float mListCenterY;
	
	private float mAbsRootRadius;
	
	private float mAbsNodeRadius;
	
	private float mListSize;
	
	private boolean mVertical;
	
	private AnimatorSet mAllAnim = new AnimatorSet();
	
	private ValueAnimator mStdAnim = ValueAnimator.ofFloat(0f, 1f);
	
	private int mCicrleLvl=0;
	
	private CircleNode mRoot = new CircleNode();
	
	public void setLvl(int CircleLvl){
		mCicrleLvl= CircleLvl;		
	}
	
	public void addRoot( String text , String label,CircleListener listener){
			mRoot.mLabel = label;
			mRoot.mText = text;
			mRoot.mLvl = 0;
			mRoot.SetCoordSize();
			mRoot.mCircleListener = listener;
		
	}
	public CircleNode addNodeToRoot(String text, String label) {
		return addNewNode(mRoot,text,label);
		
	}
	public CircleNode addNodeToRoot(String text, String label,CircleListener listener) {
		return addNewNode(mRoot,text,label,listener);
		
	}
	public CircleNode addNewNode(CircleNode ParentNode,String text, String label,CircleListener listener){
		CircleNode node =addNewNode(ParentNode,text,label);
		node.mCircleListener = listener;
		return node;
	}
	public CircleNode addNewNode(CircleNode ParentNode,String text, String label){
		CircleNode node   = new CircleNode();
		node.mLabel = label;
		node.mText = text;
		node.mLvl =ParentNode.mLvl+1;
		ParentNode.mChilds.add(node);
		node.mParent = ParentNode;
		mRoot.SetCoordSize();
		return node;
	}
	public void invalidate (){
		super.invalidate();
		
	}
	public void startAnim(){
		mAllAnim.start();
	}
	public CircleList(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CircleList,
                0, 0);

           try {
        	  
        	   
        	  
        	   mRootRadius = a.getFloat(R.styleable.CircleList_RootRadius, 1);
        	   mNodeRadius = a.getFloat(R.styleable.CircleList_NodeRadius, 1);
        	   mAnimationTime = a.getInt(R.styleable.CircleList_AnimationTime, 1000);
        	   if(mRootRadius==0){
        		   mRootRadius=1;
        	   }
        	   mBorderSize= a.getFloat(R.styleable.CircleList_BorderSize, 0);
        	   mRootBackgroundColor  =a.getColor(R.styleable.CircleList_RootBackgroundColor,0);
        	   mRootBorderColor =a.getColor(R.styleable.CircleList_RootBorderColor,0);
        	   mRootTextColor = a.getColor(R.styleable.CircleList_RootTextColor,0);
        	   
        	   mNodeBackgroundColor  =a.getColor(R.styleable.CircleList_NodeBackgroundColor,0);
        	   mNodeBorderColor =a.getColor(R.styleable.CircleList_NodeBorderColor,0);
        	   mNodeTextColor = a.getColor(R.styleable.CircleList_NodeTextColor,0);
           } finally {
               a.recycle();
           }
           init();
    }
	private void init() {
		   mNodeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		   mNodeTextPaint.setColor(mNodeTextColor);
	     
	       mNodeTextPaint.setTextAlign(Paint.Align.CENTER);
		   

	       mNodeCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	       mNodeCirclePaint.setStyle(Paint.Style.FILL);
	   
	       mNodeCirclePaint.setColor(mNodeBackgroundColor);
	      
	       mNodeBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	       mNodeBorderPaint.setStyle(Paint.Style.FILL);
	       mNodeBorderPaint.setColor(mNodeBorderColor);
	       
	       mRootTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	       mRootTextPaint.setColor(mRootTextColor);
	     
	       mRootTextPaint.setTextAlign(Paint.Align.CENTER);
		   

	       mRootCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	       mRootCirclePaint.setStyle(Paint.Style.FILL);
	   
	       mRootCirclePaint.setColor(mRootBackgroundColor);
	      
	       mRootBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	       mRootBorderPaint.setStyle(Paint.Style.FILL);
	       mRootBorderPaint.setColor(mRootBorderColor);
	       mAllAnim.setDuration(mAnimationTime);
				
 			
	       mStdAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
				
				@Override
				public void onAnimationUpdate(ValueAnimator animation) {
					mRoot.SetBoundsRecursevly();
					CircleList.this.invalidate();
				}
			});
	       
	}
	protected void onDraw(Canvas canvas) {
		   super.onDraw(canvas);

		   if(mRoot!=null){
			  mRoot.DrawLines(canvas);
			  mRoot.DrawCircle(canvas);	
			  		   
		   }
		   
		   
	}
	  @Override
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
	        
	        mListCenterX =ww*50/100;
	        mListCenterY =hh*50/100;
	        mListSize = Math.min(hh,ww);
	        mVertical = hh>ww;
	        mAbsRootRadius= mListSize*mRootRadius/100;
	        mAbsNodeRadius= mListSize*mNodeRadius/100;
	        // Figure out how big we can make the pie.
	        if(mRoot!=null){
	        	mRoot.SetCoordSize();
	        }
	        

	       
	    }
	   @Override
	   public boolean onTouchEvent(MotionEvent event) {
		   	if(mRoot!=null){
		   		mRoot.CheckClick(event.getX(),event.getY(),event);
		   	}
		   
		    if(event.getActionMasked()==MotionEvent.ACTION_DOWN){
		    	return true;
		    }
		  
		   return super.onTouchEvent(event);
		   
		
	   }
	  
	  public interface CircleListener{
		  
		   public void onClick(CircleNode node);

		   public void onUp(CircleNode node);

		   public void onDown(CircleNode node);
	  }
	  public class CircleNode
	  {
		  	public int mLvl;
		  	
		  	public String mText;
		  	
		  	public float mCircleCenterX;
		  	
		  	public float mCircleCenterY;
		  	
		  	public float mCircleRadius;
		  		  	
		  	private float mCurrentCenterX;
		  	
		  	private float mCurrentCenterY;
		  	
		  	private float mCurrentRadius;
		  	
		  	private boolean mAnimNeed= false;
		  	
		  	public float mBorderSize;
		  	
		  	
		  	public RectF mCircleBounds = new RectF();
			
		  	public RectF mBorderBounds = new RectF();
		  	
		  	public String mLabel;
		  	
		 	public CircleNode mParent;
		 	
		 	public CircleListener mCircleListener;
		  	
		  	public List<CircleNode> mChilds = new ArrayList<CircleNode>();
		  	
		 
		  	public boolean CheckClick(float X,float Y,MotionEvent event){
		  		int action =event.getActionMasked();
		  		if(mBorderBounds.contains(X, Y)){
		  			if(mCircleListener!=null){
		  				
		  				switch(action){
		  					case MotionEvent.ACTION_DOWN:
		  						mCircleListener.onDown(this);
		  						
		  						break;
		  					case MotionEvent.ACTION_UP:
		  						mCircleListener.onUp(this);
		  						mCircleListener.onClick(this);
		  						break;
		  					case MotionEvent.ACTION_MOVE:
		  						if(event.getHistorySize()>0){
			  						if(!mBorderBounds.contains(event.getHistoricalX(0), event.getHistoricalY(0))){
			  							mCircleListener.onDown(this);
				  									  					
				  					}
		  						}
		  						break;
		  						
		  				}
		  				
		  			}
		  			return true;
		  		}else{
		  			if(action==MotionEvent.ACTION_MOVE){
		  				if(event.getHistorySize()>0){
		  					if(mBorderBounds.contains(event.getHistoricalX(0), event.getHistoricalY(0))){
		  						if(mCircleListener!=null){
		  							mCircleListener.onUp(this);
		  						}
		  					}
		  				}
		  			}
		  			
		  			for (Iterator<CircleNode> iterator = mChilds.iterator(); iterator.hasNext();) {
		  				if(iterator.next().CheckClick(X,Y,event)){
			  				return true;
			  			}	  			
					}		  		
		  		}
		  		return false;
		  	}
		 	public void SetBoundsRecursevly() {
		 		for (int i=0;i< mChilds.size(); i++) {
		  			mChilds.get(i).SetBoundsRecursevly();
				}
		 		SetBounds();
		  	
				
			}
			public void SetCoordSize(){
		 		if(mLvl<mCicrleLvl){
		 			int delta   =mCicrleLvl-mLvl;
		 			mCircleCenterX =mListCenterX;
			  		mCircleCenterY =mListCenterY*(1+delta)+mAbsRootRadius;
			  		mCircleRadius =  mAbsRootRadius*2f;
			  		mBorderSize= CircleList.this.mBorderSize;
		 		}else{
		 			CenterdCoordinate();		  		
		 		}
		  		for (int i=0;i< mChilds.size(); i++) {
		  			mChilds.get(i).SetCoordSize(i,mChilds.size());
				}
		  		CheckAnimation();
		  		SetBounds();
		  	}
		 	private void CenterdCoordinate(){
		 		mCircleCenterX =mListCenterX;
		  		mCircleCenterY =mListCenterY;
		  		mCircleRadius =  mAbsRootRadius;
		  		mBorderSize= CircleList.this.mBorderSize;
		 	}
		  	public void SetCoordSize(int number,int totalSize){
		  		if(mLvl==mCicrleLvl){
		  			CenterdCoordinate();
			  		
			  		for (int i=0;i< mChilds.size(); i++) {
			  			mChilds.get(i).SetCoordSize(i,mChilds.size());
					}
		  		}else{
			  		float arcSize = (float) (2.0f*Math.PI/(float)totalSize);
			  		
			  	
			  		float curAngle = arcSize*(float)number-arcSize/2;
			  		
			  		if(mVertical){
			  			mCircleCenterX =(float) (mListCenterX+Math.sin(curAngle)*mAbsRootRadius*2);
			  			mCircleCenterY =(float) (mListCenterY+Math.cos(curAngle)*mAbsRootRadius*2);
			  		}else{
			  			mCircleCenterX =(float) (mListCenterX+Math.cos(curAngle)*mAbsRootRadius*2);
			  			mCircleCenterY =(float) (mListCenterY+Math.sin(curAngle)*mAbsRootRadius*2);
			  		}
			  		mCircleRadius =  mAbsNodeRadius;
			  		mBorderSize= CircleList.this.mBorderSize;
		  		}
		  		CheckAnimation();
		  		SetBounds();
		  		
		  		
		  	}
		  	private void SetBounds(){
		  	
		  		float mInnerRadius = mCurrentRadius-mBorderSize;
		  		 mCircleBounds = new RectF(
		  				 	mCurrentCenterX-mInnerRadius,
			        		mCurrentCenterY-mInnerRadius,
			        		mCurrentCenterX+mInnerRadius,
			        		mCurrentCenterY+mInnerRadius);
			        
			        mBorderBounds= new RectF(
			        		mCurrentCenterX-mCurrentRadius,
			        		mCurrentCenterY-mCurrentRadius,
			        		mCurrentCenterX+mCurrentRadius,
			        		mCurrentCenterY+mCurrentRadius);
			        
			      
		  	}
		  	private void CheckAnimation() {
		  			if(!mAnimNeed){
		  				mCurrentCenterX =mCircleCenterX;
		  				mCurrentCenterY =mCircleCenterY;
		  				mCurrentRadius =mCircleRadius;
		  				
		  			}else{
		  				PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("mCurrentCenterX", mCurrentCenterX,mCircleCenterX);
		  				PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("mCurrentCenterY",mCurrentCenterY,  mCircleCenterY);
		  				PropertyValuesHolder pvhR = PropertyValuesHolder.ofFloat("mCurrentRadius",mCurrentRadius,  mCircleRadius);
		  				ObjectAnimator anim =ObjectAnimator.ofPropertyValuesHolder(this, pvhX, pvhY,pvhR);
		  			
		  				mAllAnim.play(mStdAnim).with(anim);
		  				
		  				anim.start();
		  			}
				
			}
		  	//Animation Setters and getters
		  	public void setMCurrentCenterX(float newValue){
		  		mCurrentCenterX=newValue;
		  		
		  	}
		   	public void setMCurrentCenterY(float newValue){
		  		mCurrentCenterY=newValue;
		  		
		  	}
			public void setMCurrentRadius(float newValue){
				mCurrentRadius=newValue;
		  		
		  	}
		  	
		  	
		  
		  	
			protected void DrawLines(Canvas canvas){
		  		if(mParent!=null){
		  			canvas.drawLine(mParent.mCurrentCenterX, mParent.mCurrentCenterY, mCurrentCenterX, mCurrentCenterY, mRootBorderPaint);
		  		}
		  		for (Iterator<CircleNode> iterator = mChilds.iterator(); iterator.hasNext();) {
		  			 iterator.next().DrawLines(canvas);
		  			
				}
		  		
		  	}
		  	protected void DrawCircle(Canvas canvas){
		  		mAnimNeed= true;
		  		if(mLvl<=mCicrleLvl){
		  			 
			  		   // Draw the shadow
					   canvas.drawOval(mBorderBounds, mRootBorderPaint);
					   canvas.drawOval(mCircleBounds, mRootCirclePaint);
					   
					  
					   // Draw the label text
					   mRootTextPaint.setTextSize(mCurrentRadius/RADIUS_TO_TEXT);
					   if(mLabel==null){
						   canvas.drawText(mText, mCurrentCenterX, mCurrentCenterY, mRootTextPaint);
					   }else{
						   canvas.drawText(mLabel, mCurrentCenterX, mCurrentCenterY-mRootTextPaint.getTextSize(), mRootTextPaint);
						   canvas.drawText(mText, mCurrentCenterX, mCurrentCenterY+mRootTextPaint.getTextSize(), mRootTextPaint);
					   }
					   for (Iterator<CircleNode> iterator = mChilds.iterator(); iterator.hasNext();) {
				  			iterator.next().DrawCircle(canvas);
					   }
			  	}else{
			  		 // Draw the shadow
					   canvas.drawOval(mBorderBounds, mNodeBorderPaint);
					   canvas.drawOval(mCircleBounds, mNodeCirclePaint);
					   
					  
					   // Draw the label text
					   mNodeTextPaint.setTextSize(mCurrentRadius/RADIUS_TO_TEXT);
					   if(mLabel==null){
						   canvas.drawText(mText, mCurrentCenterX, mCurrentCenterY, mNodeTextPaint);
							 
					   }else{
						   canvas.drawText(mLabel, mCurrentCenterX, mCurrentCenterY-mNodeTextPaint.getTextSize(), mNodeTextPaint);
						   canvas.drawText(mText, mCurrentCenterX, mCurrentCenterY+mNodeTextPaint.getTextSize(), mNodeTextPaint);
					   }				  
					   
			  	
			  		
			  		
			  	}
		  		
		  	}
		  
	  }
	
	  
}
