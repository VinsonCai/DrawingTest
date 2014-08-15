package com.vinson.drawingtest.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.vinson.drawingtest.events.FPSEvent;

import de.greenrobot.event.EventBus;

public class DrawingSurfaceView extends SurfaceView {

	private boolean mRestrictSpeed = false;
	private SurfaceHolder mHolder;
	private DrawingThread mDrawingThread;
	private Bitmap mBackgroundBitmap;
	private Canvas mBackgroundCanvas;
	private Path mPath;
	private Paint mPaint;

	public DrawingSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView();
	}

	public DrawingSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
	}

	public DrawingSurfaceView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		initCallback();
		initPath();
	}

	private void initCallback() {
		mHolder = getHolder();
		mHolder.addCallback(new Callback() {

			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				stopDrawingThread();
			}

			@Override
			public void surfaceCreated(SurfaceHolder holder) {

			}

			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

				mBackgroundBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
				mBackgroundCanvas = new Canvas(mBackgroundBitmap);
				mBackgroundCanvas.drawColor(Color.WHITE);
				startDrawing();
			}
		});
	}

	private void initPath() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(4f);
		mPaint.setStyle(Style.STROKE);
		mPaint.setColor(Color.RED);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mPath = new Path();
			mPath.moveTo(x, y);
			break;

		case MotionEvent.ACTION_MOVE:
			mPath.lineTo(x, y);
			break;

		case MotionEvent.ACTION_POINTER_UP:
			mPath.lineTo(x, y);
			break;

		default:
			break;
		}
//		mBackgroundCanvas.drawColor(Color.WHITE);
		mBackgroundCanvas.drawPath(mPath, mPaint);
		return true;
	}

	public void clear() {
		mBackgroundCanvas.drawColor(Color.WHITE);

	}

	private void startDrawing() {
		if (null == mDrawingThread || !mDrawingThread.isAlive()) {
			mDrawingThread = new DrawingThread();
			mDrawingThread.start();
		}
	}

	private void stopDrawingThread() {
		if (null != mDrawingThread) {
			mDrawingThread.stopThread();
		}
	}

	private class DrawingThread extends Thread {

		private boolean mIsRunning = false;

		@Override
		public void run() {
			super.run();
			mIsRunning = true;

			int fps = 0;
			FPSEvent event = new FPSEvent();
			long start = System.currentTimeMillis();
			while (mIsRunning) {

				long beforeLock = System.currentTimeMillis();
				Canvas canvas = null;
				canvas = mHolder.lockCanvas();
				if (null != canvas) {
					fps++;
//					canvas.drawBitmap(mBackgroundBitmap, 0, 0, null);
//					canvas.drawLine(0, 0, 100, 100, mPaint);
					if (null != mPath) {
						canvas.drawPath(mPath, mPaint);
					}
					mHolder.unlockCanvasAndPost(canvas);
				}

				if (mRestrictSpeed) {
					long sleepFor = System.currentTimeMillis() - beforeLock;
					sleepFor = 16 - sleepFor;
					if (sleepFor > 0) {
						try {
							Thread.sleep(sleepFor);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}

				long end = System.currentTimeMillis();
				if (end - start > 1000) {
					start = end;
					event.mFpsCount = fps;
					fps = 0;
					EventBus.getDefault().post(event);
				}
			}
		}

		public void stopThread() {
			mIsRunning = false;
		}
	}
}
